package net.rotgruengelb.forestal;

import net.fabricmc.api.ModInitializer;
import net.rotgruengelb.forestal.block.ForestalBlocks;
import net.rotgruengelb.forestal.entity.ForestalEntities;
import net.rotgruengelb.forestal.item.ForestalItems;
import net.rotgruengelb.forestal.sound.ForestalSoundEvents;
import net.rotgruengelb.forestal.world.gen.ForestalWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forestal implements ModInitializer {

	public static final String MOD_ID = "forestal";
	public static final Logger LOGGER = LoggerFactory.getLogger("forestal");

	@Override
	public void onInitialize() {
		ForestalBlocks.registerModBlocks();
		ForestalEntities.registerModEntities();
		ForestalItems.registerModItems();
		ForestalWorldGeneration.generateModWorldGeneration();
		ForestalSoundEvents.registerModSoundEvents();
	}
}