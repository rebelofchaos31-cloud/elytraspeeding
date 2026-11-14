package com.fenix.elytraspeeding.enchantment;

import com.fenix.elytraspeeding.ElytraSpeeding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ElytraSpeeding.MOD_ID);

    public static final RegistryObject<Enchantment> SPEED =
            ENCHANTMENTS.register("speed", SpeedEnchantment::new);

    public static final RegistryObject<Enchantment> SAFETY_PILLOW =
            ENCHANTMENTS.register("safety_pillow", SafetyPillowEnchantment::new);

    /** Уровень чара на предмете в слоте нагрудника (там носится элитра) */
    public static int levelOnChest(LivingEntity entity, Enchantment enchantment) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlotType.CHEST);
        return stack.isEmpty() ? 0 : EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }
}