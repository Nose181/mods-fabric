package com.totemsplus;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Monster;

import java.util.List;

/** Lo que pasa cuando un tótem te salva. */
public class Efectos {

    private static final int S = 20; // ticks por segundo

    public static void activar(ServerPlayer p, Tipo tipo) {
        switch (tipo) {
            case FUEGO -> {
                revivir(p, 6f);
                ef(p, "regeneration", 20 * S, 1);
                ef(p, "absorption", 10 * S, 1);
                ef(p, "fire_resistance", 300 * S, 0);
                ef(p, "strength", 30 * S, 0);
                for (Monster m : cercanos(p, 6)) {
                    m.setRemainingFireTicks(8 * S);
                    empujar(p, m, 1.2);
                }
            }
            case AGUA -> {
                revivir(p, 6f);
                ef(p, "regeneration", 30 * S, 1);
                ef(p, "absorption", 10 * S, 1);
                ef(p, "water_breathing", 300 * S, 0);
                ef(p, "dolphins_grace", 120 * S, 0);
                ef(p, "resistance", 10 * S, 0);
            }
            case VIENTO -> {
                revivir(p, 6f);
                ef(p, "regeneration", 20 * S, 1);
                ef(p, "absorption", 10 * S, 1);
                ef(p, "slow_falling", 180 * S, 0);
                ef(p, "speed", 60 * S, 1);
                ef(p, "jump_boost", 60 * S, 1);
                p.setDeltaMovement(p.getDeltaMovement().x, 1.2, p.getDeltaMovement().z);
                p.hurtMarked = true;
                for (Monster m : cercanos(p, 7)) {
                    empujar(p, m, 1.8);
                }
            }
            case VIDA -> {
                revivir(p, p.getMaxHealth());
                ef(p, "regeneration", 20 * S, 2);
                ef(p, "absorption", 30 * S, 2);
                ef(p, "saturation", 5 * S, 0);
            }
            case TRUENO -> {
                revivir(p, 6f);
                ef(p, "regeneration", 20 * S, 1);
                ef(p, "absorption", 10 * S, 1);
                ef(p, "fire_resistance", 30 * S, 0);
                ef(p, "resistance", 20 * S, 0);
                ef(p, "strength", 30 * S, 1);
                ef(p, "speed", 30 * S, 0);
                int rayos = 0;
                for (Monster m : cercanos(p, 10)) {
                    if (rayos >= 3) break;
                    LightningBolt rayo = EntityType.LIGHTNING_BOLT.create(p.serverLevel());
                    if (rayo != null) {
                        rayo.moveTo(m.getX(), m.getY(), m.getZ());
                        rayo.setCause(p);
                        p.serverLevel().addFreshEntity(rayo);
                        rayos++;
                    }
                }
            }
            case HIELO -> {
                revivir(p, 6f);
                ef(p, "regeneration", 20 * S, 1);
                ef(p, "absorption", 20 * S, 1);
                ef(p, "resistance", 20 * S, 1);
                ef(p, "fire_resistance", 40 * S, 0);
                for (Monster m : cercanos(p, 8)) {
                    ef(m, "slowness", 8 * S, 4);
                    ef(m, "weakness", 8 * S, 1);
                    empujar(p, m, 0.5);
                }
            }
        }

        // Particulas y sonido
        ServerLevel nivel = p.serverLevel();
        double x = p.getX();
        double y = p.getY() + 1.0;
        double z = p.getZ();
        nivel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 40, 0.5, 0.8, 0.5, 0.4);
        nivel.sendParticles(tipo.particula, x, y, z, 30, 0.6, 0.8, 0.6, 0.1);
        nivel.playSound(null, p.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, tipo.tono);
    }

    /** Lo basico de cualquier tótem: vida, quita los efectos malos y el fuego. */
    private static void revivir(ServerPlayer p, float vida) {
        p.removeAllEffects();
        p.setHealth(Math.max(1f, Math.min(p.getMaxHealth(), vida)));
        p.clearFire();
        p.fallDistance = 0f;
    }

    /** Aplica un efecto de pocion usando su nombre (ej: "strength"). */
    private static void ef(LivingEntity e, String id, int ticks, int amplificador) {
        BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("minecraft", id))
                .ifPresent(h -> e.addEffect(new MobEffectInstance(h, ticks, amplificador)));
    }

    private static List<Monster> cercanos(ServerPlayer p, double radio) {
        return p.serverLevel().getEntitiesOfClass(Monster.class, p.getBoundingBox().inflate(radio), m -> m.isAlive());
    }

    private static void empujar(ServerPlayer p, LivingEntity e, double fuerza) {
        e.knockback(fuerza, p.getX() - e.getX(), p.getZ() - e.getZ());
    }
}
