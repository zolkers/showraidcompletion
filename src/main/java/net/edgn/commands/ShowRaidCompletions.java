package net.edgn.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.edgn.utils.WynnApiUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShowRaidCompletions {
    private final static String TAG = Formatting.YELLOW + "[" + Formatting.GOLD + "RAID" + Formatting.YELLOW + "] ";

    public static void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> edrCommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("edr")
                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            MinecraftServer server = context.getSource().getClient().getServer();
                            assert server != null;
                            PlayerManager playerManager = server.getPlayerManager();
                            List<String> onlinePlayerNames = playerManager.getPlayerList().stream()
                                    .map(player -> player.getName().getString())
                                    .collect(Collectors.toList());

                            onlinePlayerNames.add(context.getSource().getPlayer().getEntityName());

                            return CommandSource.suggestMatching(onlinePlayerNames, builder);
                        })
                        .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "player"))));

        dispatcher.register(edrCommand);
    }

    private static int execute(FabricClientCommandSource source, String playerName) {
        List<String> raidNames = Arrays.asList("The Nameless Anomaly", "The Canyon Colossus",
                "Orphion's Nexus of Light", "Nest of the Grootslangs");
        List<String> raidLabels = Arrays.asList("TNA", "TCC", "NOL", "NOTG");
        List<Integer> finalRaidCompletions = new ArrayList<>();

        source.sendFeedback(Text.literal(TAG)
                .append(Text.translatable("commands.edgnmod.show_raid_completions.header")
                        .styled(style -> style.withColor(Formatting.GOLD))
                        .append(Text.literal(playerName)
                                .styled(style -> style.withBold(true).withColor(Formatting.RED)))));

        String result = WynnApiUtils.getStringFromURL("https://api.wynncraft.com/v2/player/" + playerName + "/stats");

        for (int i = 0; i < raidNames.size(); i++) {
            int completion = 0;

            String regex = raidNames.get(i) + "\",\"completed\":([0-9]*)";
            Matcher matcher = Pattern.compile(regex).matcher(result);
            while (matcher.find()) {
                completion += Integer.parseInt(matcher.group(1));
            }

            finalRaidCompletions.add(completion);

            source.sendFeedback(Text.literal(Formatting.GOLD + "- ")
                    .append(Text.translatable("commands.edgnmod.show_raid_completions.entry",
                            Text.literal(raidLabels.get(i)).styled(style -> style.withColor(Formatting.AQUA)),
                            Text.literal(": ").styled(style -> style.withColor(Formatting.AQUA)),
                            Text.literal(String.valueOf(finalRaidCompletions.get(i)))
                                    .styled(style -> style.withColor(Formatting.GOLD)),
                            Text.translatable("commands.edgnmod.show_raid_completions.times")
                                    .styled(style -> style.withColor(Formatting.YELLOW)))));
        }

        int sum = finalRaidCompletions.stream().mapToInt(Integer::intValue).sum();
        source.sendFeedback(Text.literal(Formatting.GOLD + "- ")
                .append(Text.translatable("commands.edgnmod.show_raid_completions.total")
                        .styled(style -> style.withColor(Formatting.AQUA))
                        .append(Formatting.AQUA + ": ")
                        .append(Text.literal(Formatting.GOLD + String.valueOf(sum))
                                        .styled(style -> style.withColor(Formatting.GOLD))
                        )));

        return 1;
    }
}