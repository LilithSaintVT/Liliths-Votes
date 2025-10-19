package com.lilith.lilithsvotes.command;

import com.lilith.lilithsvotes.LilithsVotesConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;

public class LilithsVoteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vote")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    source.sendSuccess(() -> Component.literal("Vote for the server at these sites:"), false);

                    for (String link : LilithsVotesConfig.VOTE_LINKS.get()) {
                        Component clickable = Component.literal(link)
                                .setStyle(Style.EMPTY
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
                                        .withUnderlined(true));
                        source.sendSuccess(() -> clickable, false);
                    }

                    int cooldown = LilithsVotesConfig.REWARD_COOLDOWN_SECONDS.get();
                    source.sendSuccess(() -> Component.literal("Please wait " + cooldown + " seconds between votes to ensure rewards apply."), false);
                    return 1;
                })
        );
    }
}
