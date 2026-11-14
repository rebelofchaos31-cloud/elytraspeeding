package com.fenix.elytraspeeding.enchantment;

import com.fenix.elytraspeeding.ElytraSpeeding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ElytraSpeeding.MOD_ID)
public class SafetyPillowHandler {

    private static final int COOLDOWN_TICKS = 100; // 5 секунд

    // Уровень III: полностью блокируем урон и отключаем полёт на 5 с
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (event.getSource() != DamageSource.FLY_INTO_WALL) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide) return;

        int lvl = ModEnchantments.levelOnChest(entity, ModEnchantments.SAFETY_PILLOW.get());
        if (lvl < 3) return;

        event.setCanceled(true);
        if (entity instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity) entity;
            p.getCooldowns().addCooldown(Items.ELYTRA, COOLDOWN_TICKS);
            p.stopFallFlying();
        }
    }

    // Уровни I-II: -40% / -60%
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource() != DamageSource.FLY_INTO_WALL) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide) return;

        int lvl = ModEnchantments.levelOnChest(entity, ModEnchantments.SAFETY_PILLOW.get());
        if (lvl == 1) event.setAmount(event.getAmount() * 0.6f);
        else if (lvl == 2) event.setAmount(event.getAmount() * 0.4f);
    }

    // Пока идёт кулдаун, взлететь нельзя
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide) return;
        PlayerEntity p = event.player;
        if (p.isFallFlying() && p.getCooldowns().isOnCooldown(Items.ELYTRA)) {
            p.stopFallFlying();
        }
    }
}