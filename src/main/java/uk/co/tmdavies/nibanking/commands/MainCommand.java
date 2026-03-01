package uk.co.tmdavies.nibanking.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.utils.CurrencyHelper;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.util.Objects;

public class MainCommand {

    private static final String commandName = "nibanking";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(commandName)
                .then(
                        Commands.literal("request")
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("reference", StringArgumentType.greedyString())
                                        .executes(MainCommand::requestOption))))
                )
                .then(
                        Commands.literal("calculate")
                                .then(Commands.argument("player", EntityArgument.player()))
                                        .executes(MainCommand::calculateOption)
                )
                .then(
                        Commands.literal("calculateraw")
                                .then(Commands.argument("player", EntityArgument.player()))
                                        .executes(MainCommand::calculateRawOption)

                )
        );
    }

    private static Object[] extractContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extraction = new Object[] {
                context.getSource(),
                context.getSource().getPlayer(),
                EntityArgument.getPlayer(context, "player"),
                DoubleArgumentType.getDouble(context, "amount"),
                StringArgumentType.getString(context, "reference")
        };

        if (extraction[1] == null) {
            NIBanking.LOGGER.error("MainCommand sender is null");

            return null;
        }

        return extraction;
    }

    private static int requestOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extract = extractContext(context);

        if (extract == null) {
            return 0;
        }

        ServerPlayer player = (ServerPlayer) extract[1];
        Player target = (Player) extract[2];
        double amount = (double) extract[3];
        String reference = (String) extract[4];

        NIBanking.LOGGER.info("[MainCommand#requestOption] Dump: {}, {}, {}, {}", player.toString(), target.toString(), amount, reference);

        return 1;
    }

    private static int calculateOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");

        int amount = CurrencyHelper.calculateInventoryValue(player);

        player.sendSystemMessage(Utils.Chat("&7You currently have: &6%s&7.", String.valueOf(amount)));

        return 1;
    }

    private static int calculateRawOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");

        int amount = CurrencyHelper.calculateInventoryValue(player);

        player.sendSystemMessage(Component.literal(String.valueOf(amount)));

        return 1;
    }
}
