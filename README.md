# Lilith’s Votes v1.0.0

Forge 1.20.1 mod to integrate Votifier voting with configurable rewards.

## Features
- /vote command shows clickable vote links
- Console-executed reward commands
- Configurable IP whitelist
- 10-second cooldown per vote per player
- RSA keypair generation for Votifier

## Setup
1. Place mod JAR in server `mods/`.
2. Run server once; keys generate at `config/lilithsvotes/rsa/`.
3. Copy `public.key` to vote sites.
4. Configure `config/lilithsvotes-common.toml`.

## Config Example
```toml
[voting]
voteLinks = ["https://minecraft-server-list.com/server/12345/vote/"]
listenerPort = 8192
rsaKeyFolder = "lilithsvotes/rsa"
rewardCooldownSeconds = 10
ipWhitelist = []

[voting.siteRewards]
ExampleService = ["give %player% minecraft:diamond 1"]
```
