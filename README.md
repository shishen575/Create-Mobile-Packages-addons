# Create: Mobile Packages Addons

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

設定ファイル: `config/cmp_addons-server.toml`（サーバーサイド設定。サーバーを再起動すると反映されます）

### Robo Bee Tier アイテム

| アイテム | クラフト | 説明 |
|---|---|---|
| Robo Bee [Tier I]   | 通常のRoboBee + 金インゴット4個 | デフォルトでは無印と同速度（config調整可） |
| Robo Bee [Tier II]  | Tier I + ダイヤモンド4個 | デフォルト1.5倍速（config調整可） |
| Robo Bee [Tier III] | Tier II + ネザライト4個 + エコーシャード4個 | デフォルト2.5倍速（config調整可） |

Bee Port にTier付きアイテムを入れると、保持しているTierに応じた速度倍率が適用されます。
各Tierの倍率は上記の config 項目でいつでも変更できます。

> **注意:** `assets/cmp_addons/textures/item/robo_bee_t1.png` 等のテクスチャ画像は未収録です。
> モデルファイル（`models/item/robo_bee_t*.json`）は用意済みなので、同名のPNGを
> `src/main/resources/assets/cmp_addons/textures/item/` に配置してください（未配置の場合は紫黒の missing texture になります）。

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
3. `build/libs/cmp_addons-1.0.0.jar` を mods フォルダへ

---

## Mixin について（重要）

`MixinRoboBeeEntity.java` は CMP 本体の `RoboBeeEntity` の `tick()` メソッドに注入して
移動速度の基底値に config の倍率を掛けています。

CMP本体の実装が変わった場合は、速度の保持・適用方法を decompile して
Mixin のターゲットを合わせて修正してください。

---

## ライセンス

MIT
