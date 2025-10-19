package com.lilith.lilithsvotes;

import com.lilith.lilithsvotes.command.LilithsVoteCommand;
import com.lilith.lilithsvotes.events.VoteReceivedEvent;
import com.lilith.lilithsvotes.network.VoteListenerServer;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod(LilithsVotes.MOD_ID)
public class LilithsVotes {
    public static final String MOD_ID = "lilithsvotes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Map<String, Long> LAST_REWARD = new ConcurrentHashMap<>();
    private VoteListenerServer voteListenerServer;

    public LilithsVotes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LilithsVotesConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Lilith’s Votes v1.0.0 initializing...");
        try {
            voteListenerServer = new VoteListenerServer();
            int port = LilithsVotesConfig.LISTENER_PORT.get();
            voteListenerServer.start(port);
            LOGGER.info("VoteListenerServer started on port {}", port);
        } catch (Exception e) {
            LOGGER.error("Failed to start VoteListenerServer", e);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LilithsVoteCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onVoteReceived(VoteReceivedEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            LOGGER.warn("Vote received but server instance is null");
            return;
        }

        server.execute(() -> {
            var playerList = server.getPlayerList();
            var player = playerList.getPlayerByName(event.getUsername());
            if (player == null) {
                LOGGER.info("Vote for offline player {} received from service {}", event.getUsername(), event.getServiceName());
                return;
            }

            int cooldownSec = LilithsVotesConfig.REWARD_COOLDOWN_SECONDS.get();
            long now = System.currentTimeMillis();
            Long last = LAST_REWARD.get(player.getGameProfile().getName());
            if (last != null && (now - last) < cooldownSec * 1000L) {
                long wait = cooldownSec - ((now - last) / 1000L);
                player.sendMessage(net.minecraft.network.chat.Component.literal("You must wait " + wait + " more seconds before receiving another vote reward."), player.getUUID());
                return;
            }

            var siteRewards = LilithsVotesConfig.SITE_REWARDS.get();
            if (siteRewards.containsKey(event.getServiceName())) {
                var commands = siteRewards.get(event.getServiceName());
                for (String cmd : commands) {
                    String processed = cmd.replace("%player%", player.getGameProfile().getName());
                    server.getCommands().performCommand(server.createCommandSourceStack(), processed);
                }
                LAST_REWARD.put(player.getGameProfile().getName(), now);
                player.sendMessage(net.minecraft.network.chat.Component.literal("Thanks for voting on " + event.getServiceName() + "! Rewards granted."), player.getUUID());
                player.sendMessage(net.minecraft.network.chat.Component.literal("Please wait " + cooldownSec + " seconds between votes to ensure rewards process correctly."), player.getUUID());
            } else {
                player.sendMessage(net.minecraft.network.chat.Component.literal("Thanks for voting! No reward configured for " + event.getServiceName()), player.getUUID());
                LOGGER.info("No reward configured for service {}", event.getServiceName());
            }
        });
    }
}
