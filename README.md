# Create-Mobile-Packages-addons

**Create: Mobile Packages** のバランスを config で調整できる Addon Mod です。

---

## 機能

### 速度倍率設定（config）

| 設定項目 | デフォルト | 説明 |
|---|---|---|
| `roboBeeSpeedMultiplier` | `1.0` | Tierアイテムを持たない無印 Robo Bee の速度倍率 |
| `tier1SpeedMultiplier` | `1.0` | Robo Bee [Tier I] の速度倍率 |
| `tier2SpeedMultiplier` | `1.5` | Robo Bee [Tier II] の速度倍率 |
| `tier3SpeedMultiplier` | `2.5` | Robo Bee [Tier III] の速度倍率 |

設定ファイル: `config/create_mobile_packages_addons-server.toml`（サーバーサイド設定。サーバーを再起動すると反映されます）

### Robo Bee Tier アイテム

| アイテム | クラフト | 説明 |
|---|---|---|
| Robo Bee [Tier I]   | 通常のRoboBee + 金インゴット4個 | デフォルトでは無印と同速度（config調整可） |
| Robo Bee [Tier II]  | Tier I + ダイヤモンド4個 | デフォルト1.5倍速（config調整可） |
| Robo Bee [Tier III] | Tier II + ネザライト4個 + エコーシャード4個 | デフォルト2.5倍速（config調整可） |

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

CMP本体の実装（`RoboManager`/`VirtualRobo`/`RoboEntity`/`DroneEntityRenderer`のメソッド名や
シグネチャ）が変わった場合は、各Mixinのターゲットを decompile して合わせ直してください。

---

## ライセンス

MIT
