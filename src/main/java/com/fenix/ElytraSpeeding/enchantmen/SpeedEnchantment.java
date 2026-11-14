package com.fenix.elytraspeeding.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class SpeedEnchantment extends Enchantment {

    public SpeedEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentType.BREAKABLE,
                new EquipmentSlotType[]{EquipmentSlotType.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    // Пороги "стоимости" на столе: I-III с 20, IV-V с 30
    @Override
    public int getMinCost(int level) {
        switch (level) {
            case 1: return 20;
            case 2: return 23;
            case 3: return 26;
            case 4: return 30;
            default: return 34;
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