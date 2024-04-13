package net.rotgruengelb.forestal;

import net.fabricmc.api.ModInitializer;
import net.rotgruengelb.forestal.block.ForestalBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forestal implements ModInitializer {

	public static final String MOD_ID = "forestal";
	public static final Logger LOGGER = LoggerFactory.getLogger("forestal");

	@Override
	public void onInitialize() {
		ForestalBlocks.registerModBlocks();
	}
}