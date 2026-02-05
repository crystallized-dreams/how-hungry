package ru.alexalabai.howhungry.config;

public class ModClientConfig {
    public boolean drawHunger = true;

    /// FUNCTIONALITY ///
    public static ModClientConfig INSTANCE = new ModClientConfig();
    public void save() {ConfigManager.saveClient(this);}
    public static ModClientConfig load() {return ConfigManager.loadClient();}
}
