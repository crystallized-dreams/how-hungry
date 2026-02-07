package ru.alexalabai.howhungry.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import ru.alexalabai.howhungry.HowHungryClient;

@Environment(EnvType.CLIENT)
public class ModClientPackets {
    public static void regAll() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CONFIG_SYNC_PACKET, (client, handler, buf, responseSender) -> {
            HowHungryClient.serverEnabled=buf.getBoolean(0);
            HowHungryClient.serverHungerEnabled=buf.getBoolean(1);
            HowHungryClient.serverRunHunger=buf.getInt(2);
        });
    }
}
