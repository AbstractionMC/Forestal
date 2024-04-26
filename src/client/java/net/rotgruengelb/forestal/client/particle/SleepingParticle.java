package net.rotgruengelb.forestal.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SleepingParticle extends SpriteBillboardParticle {

	protected SleepingParticle(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
		this.gravityStrength = 0.0F;
		this.ascending = true;
		this.velocityY *= 1.2D;
		this.scale *= 0.75F;
		this.collidesWithWorld = false;

		this.velocityX = velocityX;
		this.velocityZ = velocityZ;
	}

	@Override
	public void tick() {
		super.tick();

		this.x += Math.sin(this.age * 0.5) * 0.4D / 1.5;
		this.z += Math.cos(this.age * 0.5) * 0.4D / 1.5;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint) {
		return 15728880;
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {

		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			SleepingParticle particle = new SleepingParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
			particle.setMaxAge((int) (12.0D / (clientWorld.random.nextDouble() * 0.9D + 0.2D)));
			particle.setSprite(this.spriteProvider);
			return particle;
		}
	}
}
