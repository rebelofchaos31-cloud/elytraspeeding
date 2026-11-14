package com.fenix.elytraspeeding.client;

import client.ClientSetup;
import com.fenix.elytraspeeding.ElytraSpeeding;
import com.fenix.elytraspeeding.enchantment.ModEnchantments;
import com.fenix.elytraspeeding.SpeedState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ElytraSpeeding.MOD_ID, value = Dist.CLIENT)
public class SpeedometerHandler {

    private static final double BASE_BPS = 34.0;         // "обычная" макс. скорость, б/с
    private static final double BOOST_PER_TICK = 0.02;   // +2% за тик при пикировании
    private static final double ROCKET_BOOST = 0.05;     // +5% за тик пока работает ракета
    private static final double LOOKAHEAD_SEC = 0.5;     // на сколько секунд вперёд проверяем чанки
    private static final double BRAKE = 0.95;            // торможение, если чанки впереди не загружены

    private static double lastSet = 0.0;
    private static double rocketSpeed = 0.0;
    private static int lastTick = -1;

    private static double capBps(int limit) {
        return BASE_BPS * SpeedState.multiplier(limit);
    }

    /** true, если на пути (0.5 с вперёд) все чанки уже загружены */
    private static boolean chunksLoadedAhead(ClientPlayerEntity p, Vector3d v) {
        double len = v.length();
        if (len < 1.0E-6) return true;
        double dist = len * 20.0 * LOOKAHEAD_SEC; // блоков
        if (dist < 16.0) return true;
        Vector3d dir = v.scale(1.0 / len);
        for (int i = 1; i <= 2; i++) {
            double d = dist * i / 2.0;
            BlockPos pos = new BlockPos(p.getX() + dir.x * d, 0, p.getZ() + dir.z * d);
            if (!p.level.hasChunkAt(pos)) return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        boolean flying = p.isFallFlying();
        int max = p.isCreative() ? 6 : Math.min(5, ModEnchantments.levelOnChest(p, ModEnchantments.SPEED.get()));

        if (ClientSetup.LIMIT_UP != null) {
            while (ClientSetup.LIMIT_UP.consumeClick()) {
                if (flying && SpeedState.limit < max) SpeedState.limit++;
            }
        }
        if (ClientSetup.LIMIT_DOWN != null) {
            while (ClientSetup.LIMIT_DOWN.consumeClick()) {
                if (flying && SpeedState.limit > 0) SpeedState.limit--;
            }
        }
        if (SpeedState.limit > max) SpeedState.limit = max;

        if (!flying) {
            lastSet = 0.0;
            rocketSpeed = 0.0;
            lastTick = -1;
            return;
        }

        // игра на паузе или тик игрока не шёл: ничего не трогаем
        if (p.tickCount == lastTick) return;
        lastTick = p.tickCount;

        double dx = p.getX() - p.xo, dy = p.getY() - p.yo, dz = p.getZ() - p.zo;
        double realBps = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;

        int limit = SpeedState.limit;
        Vector3d v = p.getDeltaMovement();
        double speed = v.length();
        boolean rocket = false;
        boolean braking = false;

        if (limit > 0 && speed > 1.0E-6) {
            rocket = !p.level.getEntitiesOfClass(Entity.class,
                    p.getBoundingBox().inflate(3.0),
                    e -> e.getType() == EntityType.FIREWORK_ROCKET).isEmpty();

            Vector3d out = null;

            if (rocket) {
                if (rocketSpeed < speed) rocketSpeed = speed;
                rocketSpeed *= 1.0 + ROCKET_BOOST;
                if (limit < 6) rocketSpeed = Math.min(rocketSpeed, capBps(limit) / 20.0);

                Vector3d dir = v.normalize().scale(0.5).add(p.getLookAngle().scale(0.5)).normalize();
                out = dir.scale(rocketSpeed);
            } else {
                rocketSpeed = 0.0;
                boolean accelerating = p.xRot > 0 || speed > lastSet;

                if (limit >= 6) {
                    if (accelerating) out = v.scale(1.0 + BOOST_PER_TICK);
                } else {
                    double cap = capBps(limit) / 20.0;
                    if (speed > cap) {
                        out = v.scale(Math.max(cap, speed * 0.97) / speed);
                    } else if (accelerating) {
                        out = v.scale(Math.min(cap, speed * (1.0 + BOOST_PER_TICK)) / speed);
                    }
                }
            }

            // регулятор: не вылетаем за границу загруженных чанков
            Vector3d check = out != null ? out : v;
            if (!chunksLoadedAhead(p, check)) {
                out = check.scale(BRAKE);
                braking = true;
                if (rocket) rocketSpeed = out.length();
            }

            if (out != null) {
                p.setDeltaMovement(out);
                lastSet = out.length();
            } else {
                lastSet = speed;
            }
        } else {
            lastSet = speed;
            rocketSpeed = 0.0;
        }

        String cap = limit >= 6 ? "∞" : String.format("%.0f", capBps(limit));
        mc.gui.setOverlayMessage(new StringTextComponent(
                String.format("Скорость: %.1f б/с | Лимит: %d (потолок %s) | ракета: %s%s",
                        realBps, limit, cap, rocket ? "да" : "нет",
                        braking ? " | ЧАНКИ!" : "")), false);
    }
}