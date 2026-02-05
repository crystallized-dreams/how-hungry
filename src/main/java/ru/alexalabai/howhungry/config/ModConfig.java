package ru.alexalabai.howhungry.config;

public class ModConfig {
    public boolean enabled=true;
    public int disableRunningAfterHunger=6;
    public boolean hungerCanDamage=true;
    public boolean giveNegativeEffects=false;
    public int giveNegativeEffectsAfterHunger=6;
    public int negativeEffectCooldown=20*60*5;
    public int effectTime=20*5;
    public boolean canGiveBlindness=true;
    public boolean canGiveDarkness=true;
    public boolean canGiveNausea=true;

    /// FUNCTIONALITY ///
    public static ModConfig INSTANCE = new ModConfig();
    public void save() {ConfigManager.save(this);}
    public static ModConfig load() {return ConfigManager.load();}
}
