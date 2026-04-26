package ru.alexalabai.howhungry.common;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.alexalabai.howhungry.HowHungry;
import ru.alexalabai.howhungry.config.ModConfig;

public class ModPackets {
    public static final Identifier CONFIG_SYNC_PACKET = new Identifier(HowHungry.MOD_ID, "config_sync");

    public static void sendInfoToPlayer(ServerPlayerEntity player) {
        if(!ModConfig.INSTANCE.requireOnClient) return;
        HowHungry.LOGGER.info("[HOW_HUNGRY]: Sent configuration packet to {}",player.getDisplayName().getString());
        PacketByteBuf responseBuf = PacketByteBufs.create();
        responseBuf.writeBoolean(ModConfig.INSTANCE.enabled);
        responseBuf.writeBoolean(ModConfig.INSTANCE.hungerEnabled);
        responseBuf.writeInt(ModConfig.INSTANCE.disableRunningAfterHunger);
        ServerPlayNetworking.send(player, CONFIG_SYNC_PACKET, responseBuf);
    }
}
