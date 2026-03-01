package uk.co.tmdavies.nibanking.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .then(Commands.argument("reference", StringArgumentType.greedyString())
                                        .executes(MainCommand::requestOption))))
                )
                .then(
                        Commands.literal("calculate")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(MainCommand::calculateOption))
                )
                .then(
                        Commands.literal("calculateraw")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(MainCommand::calculateRawOption))

                )
                .then(
                        Commands.literal("removecoin")
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(MainCommand::removeCoinOption)))

                )
                .then(
                        Commands.literal("addcoin")
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(MainCommand::addCoinOption)))

                )
        );
    }

    private static Object[] extractContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extraction = new Object[] {
                context.getSource(),
                context.getSource().getPlayer(),
                EntityArgument.getPlayer(context, "player"),
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
        int amount = IntegerArgumentType.getInteger(context, "amount");
        String reference = StringArgumentType.getString(context, "reference");

        NIBanking.LOGGER.info("[MainCommand#requestOption] Dump: {}, {}, {}, {}", player.toString(), target.toString(), amount, reference);

        return 1;
    }

    private static int calculateOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extract = extractContext(context);

        if (extract == null) {
            return 0;
        }

        Player player = (Player) extract[2];

        int amount = CurrencyHelper.calculateInventoryValue(player);

        player.sendSystemMessage(Utils.Chat("&7You currently have: &6%s&7.", String.valueOf(amount)));

        return 1;
    }

    private static int calculateRawOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extract = extractContext(context);

        if (extract == null) {
            return 0;
        }

        Player target = (Player) extract[2];

        int amount = CurrencyHelper.calculateInventoryValue(target);

        target.sendSystemMessage(Component.literal(String.valueOf(amount)));

        return 1;
    }

    private static int removeCoinOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extract = extractContext(context);

        if (extract == null) {
            return 0;
        }

        ServerPlayer player = (ServerPlayer) extract[1];
        Player target = (Player) extract[2];
        int amount = IntegerArgumentType.getInteger(context, "amount");

        boolean success = CurrencyHelper.removeValueFromInventory(target, amount);

        if (success) {
            player.sendSystemMessage(Utils.Chat("&7Removed &6%s &7amount from &6%s&7's inventory.", String.valueOf(amount), target.getName().getString()));
            target.sendSystemMessage(Utils.Chat("&7Removed &6%s &7amount from your inventory.", String.valueOf(amount)));
        } else {
            player.sendSystemMessage(Utils.Chat("&cUnable to remove coins from player."));
        }

        return 1;
    }

    private static int addCoinOption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Object[] extract = extractContext(context);

        if (extract == null) {
            return 0;
        }

        ServerPlayer player = (ServerPlayer) extract[1];
        Player target = (Player) extract[2];
        int amount = IntegerArgumentType.getInteger(context, "amount");

        boolean success = CurrencyHelper.addValueToInventory(target, amount);

        if (success) {
            player.sendSystemMessage(Utils.Chat("&7Added &6%s &7amount to &6%s&7's inventory.", String.valueOf(amount), target.getName().getString()));
            target.sendSystemMessage(Utils.Chat("&7Added &6%s &7amount to your inventory.", String.valueOf(amount)));
        } else {
            player.sendSystemMessage(Utils.Chat("&cUnable to add coins to player."));
        }

        return 1;
    }
}
