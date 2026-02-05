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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("How hungry?"));


            /// CLIENT CATEGORY ///
            ConfigCategory clientCtg = builder.getOrCreateCategory(Text.of("Client"));
            clientCtg.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Render hunger bar"), ModClientConfig.INSTANCE.drawHunger)
                    .setDefaultValue(true)
                    .setSaveConsumer(val->ModClientConfig.INSTANCE.drawHunger=val)
                    .build());

            /// SERVER BASE CATEGORY ///
            ConfigCategory serverCtg = builder.getOrCreateCategory(Text.of("Server"));
            serverCtg.addEntry(builder.entryBuilder()
                    .startBooleanToggle(Text.of("Enabled"), ModConfig.INSTANCE.enabled)
                    //.setTooltip(Text.of(""))
                    .setDefaultValue(true)
                    .setSaveConsumer(val->ModConfig.INSTANCE.enabled=val)
                    .build());
            if(ModConfig.INSTANCE.enabled) {
                serverCtg.addEntry(builder.entryBuilder()
                        .startIntField(Text.of("Minimal run hunger"), ModConfig.INSTANCE.disableRunningAfterHunger)
                        .setTooltip(Text.of("How much hunger points (2 points=1 chicken leg) is needed to run (-1 to not disable running, 20 is max)"))
                        .setDefaultValue(6)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.disableRunningAfterHunger = val)
                        .build());
                serverCtg.addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Can hunger damage"), ModConfig.INSTANCE.hungerCanDamage)
                        //.setTooltip(Text.of(""))
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.hungerCanDamage = val)
                        .build());
                serverCtg.addEntry(builder.entryBuilder()
                        .startBooleanToggle(Text.of("Give negative effects"), ModConfig.INSTANCE.giveNegativeEffects)
                        .setTooltip(Text.of("Applies random negative effect if player is hungry"))
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> ModConfig.INSTANCE.giveNegativeEffects = val)
                        .build());
                if (ModConfig.INSTANCE.giveNegativeEffects) {
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.of("Maximal negative effect hunger"), ModConfig.INSTANCE.giveNegativeEffectsAfterHunger)
                            .setTooltip(Text.of("How much hunger points (2 points=1 chicken leg) is needed to apply negative effects (20 is max)"))
                            .setDefaultValue(6)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.giveNegativeEffectsAfterHunger = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.of("Negative effects cooldown"), ModConfig.INSTANCE.negativeEffectCooldown)
                            .setTooltip(Text.of("How much to wait before applying next negative effect (in ticks)"))
                            .setDefaultValue(20 * 60 * 5)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.negativeEffectCooldown = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startIntField(Text.of("Effects time"), ModConfig.INSTANCE.effectTime)
                            .setTooltip(Text.of("How much negative effect lasts (in ticks)"))
                            .setDefaultValue(20 * 5)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.effectTime = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startBooleanToggle(Text.of("Can give blindness"), ModConfig.INSTANCE.canGiveBlindness)
                            //.setTooltip(Text.of(""))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.canGiveBlindness = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startBooleanToggle(Text.of("Can give darkness"), ModConfig.INSTANCE.canGiveDarkness)
                            //.setTooltip(Text.of(""))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.canGiveDarkness = val)
                            .build());
                    serverCtg.addEntry(builder.entryBuilder()
                            .startBooleanToggle(Text.of("Can give nausea"), ModConfig.INSTANCE.canGiveNausea)
                            //.setTooltip(Text.of(""))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> ModConfig.INSTANCE.canGiveNausea = val)
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