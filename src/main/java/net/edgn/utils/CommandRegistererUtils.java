package net.edgn.utils;

import com.mojang.brigadier.CommandDispatcher;
import net.edgn.commands.ShowRaidCompletions;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CommandRegistererUtils {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerClientCommands(dispatcher);
        });
    }

    private static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        CommandDispatcher<FabricClientCommandSource> d = dispatcher;

        ShowRaidCompletions.register(d);
    }
}