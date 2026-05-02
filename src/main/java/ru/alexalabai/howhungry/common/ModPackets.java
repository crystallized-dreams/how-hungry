package ru.alexalabai.howhungry.common;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.alexalabai.howhungry.HowHungry;
import ru.alexalabai.howhungry.config.ModConfig;

public class ModPackets {
    public static final Identifier CONFIG_SYNC_PACKET = Identifier.of(HowHungry.MOD_ID, "config_sync");

    public static void regAll() {
        PayloadTypeRegistry.playS2C().register(ConfigPayload.ID, ConfigPayload.CODEC);
    }

    public static void sendInfoToPlayer(ServerPlayerEntity player) {
        if(!ModConfig.INSTANCE.requireOnClient) return;
        HowHungry.LOGGER.info("[HOW_HUNGRY]: Sent configuration packet to {}",player.getDisplayName().getString());
        ServerPlayNetworking.send(player, new ConfigPayload(ModConfig.INSTANCE.enabled,ModConfig.INSTANCE.hungerEnabled,ModConfig.INSTANCE.disableRunningAfterHunger));
    }
}
