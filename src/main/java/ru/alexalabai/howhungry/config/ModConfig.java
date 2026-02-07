package ru.alexalabai.howhungry.config;

import java.util.List;

public class ModConfig {
    public boolean enabled=true;
    public boolean hungerEnabled=true;
    public int disableRunningAfterHunger=6;
    public boolean hungerCanDamage=true;
    public boolean giveNegativeEffects=false;
    public boolean giveNegativeEffectsOnlyWhenRunning=false;
    public int giveNegativeEffectsAfterHunger=6;
    public int negativeEffectCooldown=20*60*5;
    public int effectTime=20*5;
    public List<String> negativeEffects=List.of("minecraft:blindness","minecraft:darkness","minecraft:nausea");

    /// FUNCTIONALITY ///
    public static ModConfig INSTANCE = new ModConfig();
    public void save() {ConfigManager.save(this);}
    public static ModConfig load() {return ConfigManager.load();}
}
