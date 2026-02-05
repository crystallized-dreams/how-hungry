package ru.alexalabai.howhungry;

import net.fabricmc.api.ClientModInitializer;
import ru.alexalabai.howhungry.config.ModClientConfig;

@SuppressWarnings("unused")
public class HowHungryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientConfig.INSTANCE=ModClientConfig.load();
    }
}
