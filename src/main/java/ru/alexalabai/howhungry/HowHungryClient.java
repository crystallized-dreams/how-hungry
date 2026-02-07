package ru.alexalabai.howhungry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;
import ru.alexalabai.howhungry.common.ModClientPackets;
import ru.alexalabai.howhungry.config.ModClientConfig;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class HowHungryClient implements ClientModInitializer {
    public static boolean serverEnabled=true;
    public static boolean serverHungerEnabled=true;
    public static int serverRunHunger=6;
    static boolean notifiedAboutDisabledHunger=false;
    @Override
    public void onInitializeClient() {
        ModClientConfig.INSTANCE=ModClientConfig.load();
        ModClientPackets.regAll();
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            if(!notifiedAboutDisabledHunger&&!serverHungerEnabled&&serverEnabled) {
                if(client.player!=null) client.player.sendMessage(Text.translatable("text.how-hungry.hunger_disabled"));
                notifiedAboutDisabledHunger=true;
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(((networkHandler, client) -> {
            serverEnabled=true;
            serverHungerEnabled=true;
            serverRunHunger=6;
            notifiedAboutDisabledHunger=false;
        }));
    }
}
