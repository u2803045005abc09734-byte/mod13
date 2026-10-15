package dev.phoenix.waterfallparticles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/** Registers only the animated waterfall-cascade particle. */
public final class WaterfallParticles implements ModInitializer {
    public static final String MOD_ID = "waterfallparticles";
    public static final SimpleParticleType CASCADE = FabricParticleTypes.simple(true);

    @Override
    public void onInitialize() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "cascade"),
                CASCADE
        );
    }
}
