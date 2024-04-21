package net.rotgruengelb.forestal.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.rotgruengelb.forestal.Forestal;

public class ForestalSoundEvents {

	public static final SoundEvent ENTITY_GRIZZLY_BEAR_STEP = registerSoundEvent("entity.forestal.grizzly_bear.step");
	public static final SoundEvent ENTITY_GRIZZLY_BEAR_AMBIENT = registerSoundEvent("entity.forestal.grizzly_bear.ambient");
	public static final SoundEvent ENTITY_GRIZZLY_BEAR_AMBIENT_BABY = registerSoundEvent("entity.forestal.grizzly_bear.ambient_baby");
	public static final SoundEvent ENTITY_GRIZZLY_BEAR_HURT = registerSoundEvent("entity.forestal.grizzly_bear.hurt");
	public static final SoundEvent ENTITY_GRIZZLY_BEAR_DEATH = registerSoundEvent("entity.forestal.grizzly_bear.death");

	private static SoundEvent registerSoundEvent(String id) {
		return registerSoundEvent(new Identifier(Forestal.MOD_ID, id));
	}

	private static SoundEvent registerSoundEvent(Identifier id) {
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	public static void registerModSoundEvents() {
		Forestal.LOGGER.debug("Registering ForestalSoundEvents for " + Forestal.MOD_ID);
	}
}
