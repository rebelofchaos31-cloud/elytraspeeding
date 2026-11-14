package com.fenix.elytraspeeding.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class SafetyPillowEnchantment extends Enchantment {

    public SafetyPillowEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentType.BREAKABLE,
                new EquipmentSlotType[]{EquipmentSlotType.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    // I-II с 20-25; III только на высоких бросках полного стола
    @Override
    public int getMinCost(int level) {
        switch (level) {
            case 1: return 20;
            case 2: return 25;
            default: return 36;
        }
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem;
    }
}