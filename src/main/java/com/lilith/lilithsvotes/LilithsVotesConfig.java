package com.lilith.lilithsvotes;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LilithsVotesConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> VOTE_LINKS;
    public static final ForgeConfigSpec.ConfigValue<Integer> LISTENER_PORT;
    public static final ForgeConfigSpec.ConfigValue<String> RSA_KEY_FOLDER;
    public static final ForgeConfigSpec.ConfigValue<Map<String, List<String>>> SITE_REWARDS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> IP_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<Integer> REWARD_COOLDOWN_SECONDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("voting");

        VOTE_LINKS = builder.comment("List of vote site URLs for /vote command")
                .defineList("voteLinks",
                        List.of("https://minecraft-server-list.com/server/12345/vote/"),
                        obj -> obj instanceof String);

        LISTENER_PORT = builder
                .comment("Port the Votifier listener will bind to")
                .defineInRange("listenerPort", 8192, 1, 65535);

        RSA_KEY_FOLDER = builder
                .comment("Folder inside config/ for RSA keys")
                .define("rsaKeyFolder", "lilithsvotes/rsa");

        Map<String, List<String>> defaultMap = new HashMap<>();
        defaultMap.put("ExampleService", List.of("give %player% minecraft:diamond 1"));

        SITE_REWARDS = builder
                .comment("Map of serviceName -> commands for rewards")
                .defineMap("siteRewards", defaultMap,
                        key -> key instanceof String,
                        val -> val instanceof List<?>);

        IP_WHITELIST = builder
                .comment("Allowed IPs or hostnames for vote packets")
                .defineList("ipWhitelist", List.of(), obj -> obj instanceof String);

        REWARD_COOLDOWN_SECONDS = builder
                .comment("Cooldown in seconds to avoid duplicate rewards")
                .defineInRange("rewardCooldownSeconds", 10, 0, 3600);

        builder.pop();
        SPEC = builder.build();
    }
}
