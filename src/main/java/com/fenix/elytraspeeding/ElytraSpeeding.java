package com.fenix.elytraspeeding;

import com.fenix.elytraspeeding.enchantment.ModEnchantments;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ElytraSpeeding.MOD_ID)
public class ElytraSpeeding {
    public static final String MOD_ID = "elytraspeeding";

    public ElytraSpeeding() {
        ModEnchantments.ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}