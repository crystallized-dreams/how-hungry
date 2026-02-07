package ru.alexalabai.howhungry.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.alexalabai.howhungry.HowHungry;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.how-hungry.title"));

            /// CLIENT CATEGORY ///
            ConfigCategory clientCtg = builder.getOrCreateCategory(Text.translatable("config.how-hungry.client"));
            clientCtg.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.translatable("config.how-hungry.client.render_hunger_bar"), ModClientConfig.INSTANCE.drawHunger)
                    .setDefaultValue(true)
                    .setSaveConsumer(val->ModClientConfig.INSTANCE.drawHunger=val)
                    .build());

            /// SERVER BASE CATEGORY ///
            ConfigCategory serverCtg = builder.getOrCreateCategory(Text.translatable("config.how-hungry.server"));
            serverCtg.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.translatable("config.how-hungry.server.enabled"), ModConfig.INSTANCE.enabled)
                    //.setTooltip(Text.of(""))
                    .setDefaultValue(true)
                    .setSaveConsumer(val->ModConfig.INSTANCE.enabled=val)
                    .build());
            serverCtg.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.translatable("config.how-hungry.server.hunger_enabled"), ModConfig.INSTANCE.hungerEnabled)
                    .setTooltip(Text.translatable("config.how-hungry.server.hunger_enabled.tooltip"))
                    .setDefaultValue(true)
                    .setSaveConsumer(val->ModConfig.INSTANCE.hungerEnabled=val)
                    .build());
            if(ModConfig.INSTANCE.enabled&&ModConfig.INSTANCE.hungerEnabled) {
                serverCtg.addEntry(builder.entryBuilder()
                        .startIntField(Text.translatable("config.how-hungry.server.min_run_hunger"), ModConfig.INSTANCE.disableRunningAfterHunger)
                        .setTooltip(Text.translatable("config.how-hungry.server.min_run_hunger.tooltip"))
                        .setDefaultValue(6)
                        .setMin(-1).setMax(20)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.disableRunningAfterHunger = val)
                        .build());
                serverCtg.addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.translatable("config.how-hungry.server.hunger_damage"), ModConfig.INSTANCE.hungerCanDamage)
                        //.setTooltip(Text.of(""))
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.hungerCanDamage = val)
                        .build());
                serverCtg.addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.translatable("config.how-hungry.server.negative_effects"), ModConfig.INSTANCE.giveNegativeEffects)
                        .setTooltip(Text.translatable("config.how-hungry.server.negative_effects.tooltip"))
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.giveNegativeEffects = val)
                        .build());
                if (ModConfig.INSTANCE.giveNegativeEffects) {
                    serverCtg.addEntry(builder.entryBuilder()
                            .startBooleanToggle(Text.translatable("config.how-hungry.server.negative_effects.apply_when_running"), ModConfig.INSTANCE.giveNegativeEffectsOnlyWhenRunning)
                            //.setTooltip(Text.translatable("config.config.how-hungry.server.negative_effects.apply_when_running.tooltip"))
                            .setDefaultValue(false)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.giveNegativeEffectsOnlyWhenRunning = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.translatable("config.how-hungry.server.negative_effects.hunger_needed"), ModConfig.INSTANCE.giveNegativeEffectsAfterHunger)
                            .setTooltip(Text.translatable("config.how-hungry.server.negative_effects.hunger_needed.tooltip"))
                            .setDefaultValue(6)
                            .setMin(0).setMax(20)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.giveNegativeEffectsAfterHunger = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.translatable("config.how-hungry.server.negative_effects.cooldown"), ModConfig.INSTANCE.negativeEffectCooldown)
                            .setTooltip(Text.translatable("config.how-hungry.server.negative_effects.cooldown.tooltip"))
                            .setDefaultValue(20 * 60 * 5)
                            .setMin(1)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.negativeEffectCooldown = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.translatable("config.how-hungry.server.negative_effects.duration"), ModConfig.INSTANCE.effectTime)
                            .setTooltip(Text.translatable("config.how-hungry.server.negative_effects.duration.tooltip"))
                            .setDefaultValue(20 * 5)
                            .setMin(1)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.effectTime = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startStrList(Text.translatable("config.how-hungry.server.negative_effects.values"),ModConfig.INSTANCE.negativeEffects)
                                    .setSaveConsumer(val->{
                                        for(String possibleEffect : val) {
                                            Identifier id=Identifier.tryParse(possibleEffect);
                                            if(id==null) {
                                                HowHungry.LOGGER.info(HowHungry.invalidEffect,possibleEffect);
                                                val.remove(possibleEffect);
                                                continue;
                                            }
                                            if(Registries.STATUS_EFFECT.get(id)==null) {
                                                HowHungry.LOGGER.info(HowHungry.invalidEffect,possibleEffect);
                                                val.remove(possibleEffect);
                                            }
                                        }
                                        ModConfig.INSTANCE.negativeEffects=val;
                                    })
                            .setDefaultValue(List.of("minecraft:blindness","minecraft:darkness","minecraft:nausea"))
                            .build());
                }
            }

            return builder.setSavingRunnable(()->{
                ModConfig.INSTANCE.save();
                ModClientConfig.INSTANCE.save();
            }).build();
        };
    }
}