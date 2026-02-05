package ru.alexalabai.howhungry.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("server.how-hungry.json");
    private static final Path CLIENT_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("client.how-hungry.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    static <T> void abstractSave(T config, Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static <T> T abstractLoad(Class<T> config, Path path) {
        if (!Files.exists(path)) {
            return null;
        }

        //Parse JSON.
        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, config);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(ModConfig config) {abstractSave(config,CONFIG_PATH);}
    public static ModConfig load() {
        ModConfig cfg=abstractLoad(ModConfig.class,CONFIG_PATH);
        if(cfg==null) return new ModConfig();
        return cfg;
    }

    public static void saveClient(ModClientConfig config) {abstractSave(config,CLIENT_CONFIG_PATH); }
    public static ModClientConfig loadClient() {
        ModClientConfig cfg=abstractLoad(ModClientConfig.class,CLIENT_CONFIG_PATH);
        if(cfg==null) return new ModClientConfig();
        return cfg;
    }
}
