package dev.phoenix.waterfallparticles.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Port of Particular's animated CascadeParticle for Minecraft 26.3.
 * It uses the original twelve cascade texture frames and the original short lifetime,
 * gravity, lateral drift and solid-block clipping behavior.
 */
public final class CascadeParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    private CascadeParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, float requestedScale) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.lifetime = 9;
        this.quadSize = Math.max(0.1F, Math.min(1.0F, requestedScale));
        this.gravity = 0.4F;
        this.xd = this.random.nextDouble() * 0.25D - 0.125D;
        this.yd = 0.0D;
        this.zd = this.random.nextDouble() * 0.25D - 0.125D;
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
        this.removeIfInsideSolidBlock();
    }

    @Override
    public void tick() {
        super.tick();
        this.removeIfInsideSolidBlock();
        this.setSpriteFromAge(this.sprites);
    }

    private void removeIfInsideSolidBlock() {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        if (this.level.getBlockState(pos).isSolidRender()) {
            this.setAlpha(0.0F);
            this.remove();
        }
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        // Particular used the opaque particle sheet for this animated cascade.
        return SingleQuadParticle.Layer.OPAQUE;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xAux,
                double yAux,
                double zAux,
                RandomSource random
        ) {
            // The locator passes the original Particular size multiplier via xAux.
            float scale = xAux > 0.0D ? (float) xAux : 1.0F;
            return new CascadeParticle(level, x, y, z, this.sprites, scale);
        }
    }
}
