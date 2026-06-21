package com.example.cmp_addons.tier;

import org.jetbrains.annotations.Nullable;

/**
 * Mixinで {@code RoboEntity} に実装させるインターフェース。
 * クライアント側（描画用）にTierキーを同期するためのアクセサ。
 */
public interface ITieredEntity {

    /** 同期されたTierキー（"tier1"等）。無印の場合は空文字 */
    @Nullable String cmpa$getTierKey();
}
