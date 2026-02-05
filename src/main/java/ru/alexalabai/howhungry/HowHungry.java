package ru.alexalabai.howhungry;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alexalabai.howhungry.config.ModConfig;

import java.util.*;

@SuppressWarnings("unused")
public class HowHungry implements ModInitializer {
	public static final String MOD_ID = "how-hungry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Map<UUID,Integer> effectTimerKeeper=new HashMap<>();

	@Override
	public void onInitialize() {
		ModConfig.INSTANCE=ModConfig.load();
		LOGGER.info("[HOW_HUNGRY]: I'm so hungry...");
		ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
			effectTimerKeeper.put(serverPlayNetworkHandler.player.getUuid(),0);
		}));
		ServerPlayConnectionEvents.DISCONNECT.register(((serverPlayNetworkHandler, minecraftServer) -> {
			effectTimerKeeper.remove(serverPlayNetworkHandler.player.getUuid());
		}));
		ServerTickEvents.END_SERVER_TICK.register(server->{
			if(!ModConfig.INSTANCE.enabled) return;
			if(!ModConfig.INSTANCE.giveNegativeEffects) return;
			for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if(player.getHungerManager().getFoodLevel()<=ModConfig.INSTANCE.giveNegativeEffectsAfterHunger) {
					if(HowHungry.effectTimerKeeper.get(player.getUuid())<=0) {
						HowHungry.effectTimerKeeper.put(player.getUuid(),Math.max(ModConfig.INSTANCE.negativeEffectCooldown,1));

						List<StatusEffect> viableEffects=new ArrayList<>();
						if(ModConfig.INSTANCE.canGiveBlindness) viableEffects.add(StatusEffects.BLINDNESS);
						if(ModConfig.INSTANCE.canGiveDarkness) viableEffects.add(StatusEffects.DARKNESS);
						if(ModConfig.INSTANCE.canGiveNausea) viableEffects.add(StatusEffects.NAUSEA);

						player.addStatusEffect(new StatusEffectInstance(
								viableEffects.get(player.getRandom().nextInt(viableEffects.size())),
								Math.max(ModConfig.INSTANCE.effectTime,1),0,
								false,false,true)
						);
					} else HowHungry.effectTimerKeeper.put(player.getUuid(), HowHungry.effectTimerKeeper.get(player.getUuid())-1);
				}
			}
		});
	}
}