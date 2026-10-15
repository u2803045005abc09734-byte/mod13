package dev.phoenix.waterfallparticles.client;

import dev.phoenix.waterfallparticles.WaterfallParticles;
import dev.phoenix.waterfallparticles.client.particle.CascadeParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Client-only standalone implementation of Particular's waterfall cascade locator.
 * No extra droplet/spray particle, leaves, fireflies, bubbles, or other Particular effects.
 */
public final class WaterfallParticlesClient implements ClientModInitializer {
    private static final Map<BlockPos, Integer> CASCADES = new HashMap<>();
    private static final int RESCAN_INTERVAL = 8;
    private static int ticksSinceScan;
    private static ClientLevel lastLevel;

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(
                WaterfallParticles.CASCADE,
                CascadeParticle.Provider::new
        );
        ClientTickEvents.END_CLIENT_TICK.register(WaterfallParticlesClient::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.level == null || client.player == null) {
            CASCADES.clear();
            lastLevel = null;
            ticksSinceScan = 0;
            return;
        }

        ClientLevel level = client.level;
        if (level != lastLevel) {
            CASCADES.clear();
            lastLevel = level;
            ticksSinceScan = 0;
        }

        if (++ticksSinceScan >= RESCAN_INTERVAL) {
            ticksSinceScan = 0;
            scanNearbyWaterfalls(level, client.player.blockPosition());
        }

        // Particular emits one animated cascade particle per detected waterfall block each tick.
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Map.Entry<BlockPos, Integer> entry : CASCADES.entrySet()) {
            BlockPos pos = entry.getKey();
            FluidState above = level.getFluidState(pos.above());
            if (!above.is(FluidTags.WATER)) {
                continue;
            }

            double x = pos.getX();
            double y = pos.getY() + 1.0D + random.nextDouble();
            double z = pos.getZ();

            // Keep particles aligned along the exposed horizontal edge, as in Particular.
            if (random.nextBoolean()) {
                x += random.nextDouble();
                z += random.nextInt(2);
            } else {
                x += random.nextInt(2);
                z += random.nextDouble();
            }

            // Match Particular's original cascade scaling (based on open horizontal water neighbours).
            float size = entry.getValue() / 4.0F;
            float scale = 1.0F - (1.0F - size) / 2.0F;
            // xAux is unused as velocity by CascadeParticle; it carries the requested visual scale.
            level.addParticle(WaterfallParticles.CASCADE, x, y, z, scale, 0.0D, 0.0D);
        }
    }

    private static void scanNearbyWaterfalls(ClientLevel level, BlockPos center) {
        CASCADES.clear();

        // Search nearby loaded terrain only; cascade particles are a local visual effect.
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                for (int dy = -10; dy <= 14; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    FluidState current = level.getFluidState(pos);
                    FluidState above = level.getFluidState(pos.above());
                    FluidState below = level.getFluidState(pos.below());

                    // Same shape as Particular: source water at the cascade base,
                    // non-source water above, and source water below.
                    if (!current.is(FluidTags.WATER) || !current.isSource()) continue;
                    if (!above.is(FluidTags.WATER) || above.isSource()) continue;
                    if (!below.is(FluidTags.WATER) || !below.isSource()) continue;

                    int strength = 0;
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        FluidState side = level.getFluidState(pos.relative(direction));
                        if (side.is(FluidTags.WATER) && side.isSource()) {
                            strength++;
                        }
                    }
                    if (strength == 0) continue;

                    // Particular suppresses cascades in a fully enclosed water column.
                    boolean exposed = false;
                    BlockPos abovePos = pos.above();
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        if (level.getBlockState(abovePos.relative(direction)).isAir()) {
                            exposed = true;
                            break;
                        }
                    }
                    if (exposed) {
                        CASCADES.put(pos, strength);
                    }
                }
            }
        }
    }
}
