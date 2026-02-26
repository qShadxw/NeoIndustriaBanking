package uk.co.tmdavies.nibanking.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import uk.co.tmdavies.nibanking.NIBanking;

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
}
