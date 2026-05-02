package ru.alexalabai.howhungry.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import ru.alexalabai.howhungry.HowHungryClient;

@Environment(EnvType.CLIENT)
public class ModClientPackets {
    public static void regAll() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigPayload.ID, (payload, ctx)->
                ctx.client().execute(()->{
                    HowHungryClient.serverEnabled=payload.serverEnabled();
                    HowHungryClient.serverHungerEnabled=payload.serverHungerEnabled();
                    HowHungryClient.serverRunHunger=payload.serverRunHunger();
                }));
    }
}
