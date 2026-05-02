package ru.alexalabai.howhungry.common;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigPayload(boolean serverEnabled, boolean serverHungerEnabled, int serverRunHunger) implements CustomPayload {
    public static final Id<ConfigPayload> ID = new Id<>(ModPackets.CONFIG_SYNC_PACKET);
    public static final PacketCodec<RegistryByteBuf, ConfigPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ConfigPayload::serverEnabled,
            PacketCodecs.BOOL, ConfigPayload::serverHungerEnabled,
            PacketCodecs.INTEGER, ConfigPayload::serverRunHunger,
            ConfigPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
