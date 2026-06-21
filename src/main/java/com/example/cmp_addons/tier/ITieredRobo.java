package com.example.cmp_addons.tier;

import com.example.cmp_addons.BeeTier;
import org.jetbrains.annotations.Nullable;

/**
 * Mixinで {@code VirtualRobo} に実装させるインターフェース。
 * Tier情報の保持と、Tierに応じた速度倍率の適用を行う。
 */
public interface ITieredRobo {

    /** 現在のTierキー（"tier1"等）。Tierを持たない無印Beeの場合はnull */
    @Nullable String cmpa$getTierKey();

    /**
     * Tierを設定し、現在の速度に倍率を適用する。
     * 配置直後（newRobo呼び出し直後）に1回だけ呼ぶ。
     *
     * @param tier null の場合は無印用倍率（roboBeeSpeedMultiplier）を適用
     */
    void cmpa$setTierAndApplySpeed(@Nullable BeeTier tier);

    /**
     * 保存データ読み込み時にTierキーだけを復元する（速度は再計算しない）。
     */
    void cmpa$setTierKeySilently(@Nullable String tierKey);
}
