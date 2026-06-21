package com.example.create_mobile_packages_addons;

/**
 * Robo Bee の強化段階を表します。
 *
 * <p>各Tierの実際の速度倍率は {@code config/create_mobile_packages_addons-server.toml} で設定します。
 */
public enum BeeTier {
    TIER_1("tier1"),
    TIER_2("tier2"),
    TIER_3("tier3");

    /** NBT / アイテム名で使うキー */
    public final String key;

    BeeTier(String key) {
        this.key = key;
    }

    /** NBTのtierキーからEnumを解決。不明な場合はTIER_1を返す */
    public static BeeTier fromKey(String key) {
        for (BeeTier t : values()) {
            if (t.key.equals(key)) return t;
        }
        return TIER_1;
    }
}
