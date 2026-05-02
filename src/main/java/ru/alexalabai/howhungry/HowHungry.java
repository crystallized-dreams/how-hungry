package ru.alexalabai.howhungry;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alexalabai.howhungry.common.ModPackets;
import ru.alexalabai.howhungry.config.ModConfig;

import java.util.*;

@SuppressWarnings("unused")
public class HowHungry implements ModInitializer {
	public static final String MOD_ID = "how-hungry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Map<UUID,Integer> effectTimerKeeper=new HashMap<>();
	public static final String invalidEffect="[HOW_HUNGRY]: Found invalid negative effect identifier: \"{}\"";

	@Override
	public void onInitialize() {
		LOGGER.info("[HOW_HUNGRY]: I'm so hungry...");
		ModConfig.INSTANCE=ModConfig.load();

		for(String possibleEffect : ModConfig.INSTANCE.negativeEffects) {
			Identifier id=Identifier.tryParse(possibleEffect);
			if(id==null) {
				ModConfig.INSTANCE.negativeEffects.remove(possibleEffect);
				LOGGER.info(invalidEffect,possibleEffect);
				continue;
			}
			if(Registries.STATUS_EFFECT.get(id)==null) {
				ModConfig.INSTANCE.negativeEffects.remove(possibleEffect);
				LOGGER.info(invalidEffect,possibleEffect);
			}
		}
		ModPackets.regAll();
		ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
			effectTimerKeeper.put(serverPlayNetworkHandler.player.getUuid(),0);
			ModPackets.sendInfoToPlayer(serverPlayNetworkHandler.player);
		}));
		ServerPlayConnectionEvents.DISCONNECT.register(((serverPlayNetworkHandler, minecraftServer) -> {
			effectTimerKeeper.remove(serverPlayNetworkHandler.player.getUuid());
		}));
		ServerTickEvents.END_SERVER_TICK.register(server->{
			if(!ModConfig.INSTANCE.enabled) return;
			if(!ModConfig.INSTANCE.giveNegativeEffects||!ModConfig.INSTANCE.hungerEnabled) return;
			for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if(player.getHungerManager().getFoodLevel()<=6&&!ModConfig.INSTANCE.hungerEnabled) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION,20*5,0,true,false,false));
				}
				if(player.getHungerManager().getFoodLevel()<=ModConfig.INSTANCE.giveNegativeEffectsAfterHunger&&!(player.isCreative()||player.isSpectator())) {
					if(HowHungry.effectTimerKeeper.get(player.getUuid())<=0) {
						HowHungry.effectTimerKeeper.put(player.getUuid(),Math.max(ModConfig.INSTANCE.negativeEffectCooldown,1));

						Identifier id=Identifier.tryParse(ModConfig.INSTANCE.negativeEffects.get(player.getRandom().nextInt(ModConfig.INSTANCE.negativeEffects.size())));
						if(id==null) return;
						if(Registries.STATUS_EFFECT.get(id)==null) return;
						Optional<StatusEffect> effect = Registries.STATUS_EFFECT.getOrEmpty(id);

						effect.ifPresent(statusEffect->
							player.addStatusEffect(new StatusEffectInstance(
                        	        Registries.STATUS_EFFECT.getEntry(statusEffect),
								Math.max(ModConfig.INSTANCE.effectTime,1),0,
									false,false,true)
							)
						);
					} else if(!ModConfig.INSTANCE.giveNegativeEffectsOnlyWhenRunning||(ModConfig.INSTANCE.giveNegativeEffectsOnlyWhenRunning&&player.isSprinting()))
						HowHungry.effectTimerKeeper.put(player.getUuid(), HowHungry.effectTimerKeeper.get(player.getUuid())-1);
				}
			}
		});
	}
}