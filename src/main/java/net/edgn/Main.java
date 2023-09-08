package net.edgn;


import net.edgn.utils.CommandRegistererUtils;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistererUtils.registerCommands();
	}
}