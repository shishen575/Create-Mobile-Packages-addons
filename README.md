# Create-Mobile-Packages-addons

**Create: Mobile Packages** のバランスを config で調整できる Addon Mod です。

---

## 機能

### 速度倍率設定（config）

| 設定項目 | デフォルト | 説明 |
|---|---|---|
| `roboBeeSpeedMultiplier` | `1.0` | Tierアイテムを持たない無印 Robo Bee の速度倍率 |
| `tier1SpeedMultiplier` | `2.0` | Robo Bee [Tier I] の速度倍率（基本速度の+100%） |
| `tier2SpeedMultiplier` | `4.0` | Robo Bee [Tier II] の速度倍率（基本速度の+300%） |
| `tier3SpeedMultiplier` | `6.0` | Robo Bee [Tier III] の速度倍率（基本速度の+500%） |
| `stackSize` | `64` | Robo Bee Tier アイテム（I/II/III共通）の最大スタック数 |

設定ファイル: `config/create_mobile_packages_addons-server.toml`（サーバーサイド設定。サーバーを再起動すると反映されます）

### Robo Bee Tier アイテム

| アイテム | クラフト | 説明 |
|---|---|---|
| Robo Bee [Tier I]   | 通常のRoboBeeの周囲を金インゴット8個で囲む | デフォルト+100%速度（config調整可） |
| Robo Bee [Tier II]  | Tier Iの周囲をダイヤモンド8個で囲む | デフォルト+300%速度（config調整可） |
| Robo Bee [Tier III] | Tier IIの周囲をネザライトインゴット8個で囲む | デフォルト+500%速度（config調整可） |

3つとも1スタック最大64個（config調整可）。

3つとも専用クリエイティブタブ「Create: Mobile Packages Addons」にまとめて表示され、
JEI等のレシピ表示MODのアイテムリストにも表示されます
（クラフトレシピも通常の `minecraft:crafting_shaped` なので自動的にJEIで表示されます）。

Bee Port にTier付きアイテムを入れると、保持しているTierに応じた速度倍率が適用されます。
各Tierの倍率は上記の config 項目でいつでも変更できます。

見た目は本物のRobo Beeと同じ3Dモデル（CMP本体のBlockbenchモデルをそのまま使用）で、
本体パネルの色だけTierごとに変えています（金/シアン/紫）。インベントリ表示・手持ち表示・
**飛行中（Bee Port間を移動中）の見た目**のいずれも、保持しているTierに応じたテクスチャになります。

今後、ケースの容量・加工時間・コストなど他のバランス項目もこのアドオンに追加していく想定です。

---

## セットアップ・ビルド方法

### 必要なもの

- JDK 17
- Minecraft Forge 1.20.1-47.x
- **Create Mod** (0.5.1.f 以上)
- **Create: Mobile Packages** (0.5.0 以上) の jar ファイル

### 手順

1. `libs/` フォルダに以下を配置:
   - `create_mobile_packages-1.20.1-0.6.1.jar`
2. ビルド:
   ```bash
   ./gradlew build
   ```
3. `build/libs/create_mobile_packages_addons-1.0.0.jar` を mods フォルダへ

---

## GitHub Actions による自動ビルド

`.github/workflows/build.yml` で push / PR / リリース作成時に自動ビルドします。

**Create: Mobile Packages は再配布できない第三者MODのjarのため、リポジトリには含めていません。**
CIでビルドするには、リポジトリの Settings → Secrets and variables → Actions で
以下のSecretを登録してください:

| Secret名 | 値 |
|---|---|
| `CMP_JAR_URL` | `create_mobile_packages-1.20.1-0.6.1.jar` をダウンロードできるURL（自分の private storage 等） |

設定すると、CIが該当URLから jar を取得して `libs/` に配置し、`./gradlew build` を実行します。
ビルド済みjarは Actions の Artifacts、リリース作成時はそのリリースに自動添付されます。

---

## Mixin について（重要）

CMP本体には「配置済みのRobo Beeが、どのアイテム（Tier）から生まれたか」を保持する仕組みが
存在しないため、以下のMixinチェーンで実現しています。

1. `TieredRoboBeeItem` が CMP本体の `RoboBeeItem` を継承し、配置(`useOn`)直前に
   自分のTierを `PLACING_TIER`（ThreadLocal）にセットする。
2. `MixinRoboManager` が `RoboManager#newRobo` の戻り値（生成されたVirtualRoboのUUID）を捉え、
   `PLACING_TIER` の値を読んでそのVirtualRoboにTierをタグ付けする（同時に速度倍率を適用）。
3. `MixinVirtualRobo` が `VirtualRobo` にTierフィールドを追加し、`serializeNBT`/`deserializeNBT`
   経由でワールド保存・再読み込み後もTierを保持する。
4. `MixinRoboEntity` が `RoboEntity` に同期データ（`EntityDataAccessor<String>`）を追加し、
   `syncFromVirtual` のたびにTierをクライアントへ同期する。
5. `MixinDroneEntityRenderer` が描画時のテクスチャ取得 (`getTextureLocation`) を横取りし、
   同期されたTierに応じたテクスチャに切り替える。
6. `MixinBeePortBeeStackHandler` が Bee Port の Bee スロット判定 (`mayPlace`) を横取りし、
   CMP本体の「`CMPItems.ROBO_BEE.get()` と完全一致するアイテムのみ許可」という制限に加えて
   `TieredRoboBeeItem` も挿入できるようにする（ドラッグ＆ドロップでの挿入に対応）。
7. `MixinBeePortMenu` が `quickMoveStack`（シフトクリック移動）の同種の厳密一致チェックを
   横取りし、Tierアイテムをシフトクリックでもスロットへ送れるようにする。
8. `MixinRoboBeeBehaviorController` が配達完了後の `handleShutdown`（Bee Portへの帰還処理）を
   横取りする。CMP本体は到着したBeeを常に無印の `CMPItems.ROBO_BEE` としてポートへ戻すため、
   これがないと配達後にTierが失われる。VirtualRoboに保持しているTierから正しいアイテムを
   生成してポートへ戻すようにする。
9. `MixinBeePortBlockEntity` が `tryConsumeDrone`（Bee Port保管中のBeeを発進させる処理）を
   横取りする。CMP本体は発進時に取り出したアイテムのTierを見ずに捨てているため、これが
   ないと「Bee Portに保管 → 発進」した場合に速度Tierが適用されない（直接右クリックで配置
   した場合は `TieredRoboBeeItem#useOn` 経由で伝わるため発生しない）。取り出したアイテムの
   Tierを `PLACING_TIER` にセットし、直後の `RoboManager#newRobo` 呼び出しに反映させる。
10. `MixinRoboManager` に `RoboManager#newRequestRobo`（リクエスト経由でBeeを発進させる
    別経路）用のフックも追加。CMP本体には発進経路が2つあり、片方だけ対応していると
    リクエスト経由の発進だけTierが反映されない不具合が起きていた。
11. `MixinVirtualRobo` が `updateEta` 内の `CMPHelper.calcETA` 呼び出しを横取りする。
    CMP本体の到着時間計算は常にconfigのデフォルト速度を使っており、Tierで上昇した
    実際の速度を無視していたため、到着予測時間が不正確だった。実際の速度
    (`VirtualRobo#getSpeed()`) を使って再計算する。
12. `MixinBeePortBeeStackHandler`/`MixinBeePortMenu` は、スロットに既に**別のTier**の
    Beeが入っている場合は挿入・シフトクリック移動を拒否する。CMP本体はスロットに
    単一種類のアイテムしか入らない前提で実装されているため、Tierが混在すると
    内部の状態管理が壊れ「ターゲットがない」表示などの不具合につながる。

### Mixinに関する重要な注意（SRG名について）

CMP本体が**ヴァニラのメソッドをオーバーライドしている部分**（`Slot#mayPlace`、
`AbstractContainerMenu#quickMoveStack`、`Entity#defineSynchedData` 等）は、配布されている
jarの中では `m_5857_` のような **SRG名** にリネームされています。これらをターゲットにする
Mixinは `remap=false` を指定した上で、人間が読める名前とSRG名の**両方**を
`method = {"mayPlace", "m_5857_"}` のように併記しています（`remap=true` はrefmapの生成
設定が無いと効かないため使っていません）。

また、Forge環境でのMixin登録は **`mods.toml`の`[[mixins]]`では機能せず**、
`build.gradle`のjarタスクで `MANIFEST.MF` に `MixinConfigs: ...mixins.json` を
書き込む方式でのみ正しく読み込まれます（本アドオンの`build.gradle`で設定済み）。

CMP本体の実装（`RoboManager`/`VirtualRobo`/`RoboEntity`/`DroneEntityRenderer`/
`BeePortBlockEntity`のメソッド名やシグネチャ）が変わった場合は、各Mixinのターゲットを
decompile して合わせ直してください。

---

## ライセンス

MIT
