package dev.reyaan.ckconfig.api;


import dev.reyaan.ckconfig.ConfigOption;
import dev.reyaan.ckconfig.IntRange;
import dev.reyaan.ckconfig.YAMLSerializer;
import jdk.jfr.Description;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public File configFolder;
    public HashMap<ConfigBuilder, File> registeredConfigs;


    public ConfigManager(String path) {
        this.configFolder = new File(path);
        boolean bl = this.configFolder.mkdirs();
        this.registeredConfigs = new HashMap<>();
    }


    public void registerConfigBuilder(ConfigBuilder config) {
        // Create directory
        var p = Path.of(this.configFolder.getAbsolutePath(), config.key() + ".yml").toString();
        var parts = p.split("/");

        if (parts.length > 1) {
            String[] dirsPath = Arrays.copyOfRange(parts, 0, parts.length-1);
            var configDirs = new File(String.join("/", dirsPath));
            configDirs.mkdirs();
        }

        var configFile = new File(p);
        this.registeredConfigs.put(config, configFile);


        if (this.configFolder.exists()) {
            // Create or read file
            if (!configFile.exists()) {
                createDefaultConfig(configFile, config);
            } else {
                Map<String, Object> data = readConfig(configFile);
                if (data != null)
                    setBuilderValues(data, config);
            }
        }

    }

    // Reload
    public void reloadConfigs() {
        for (var entry : registeredConfigs.entrySet()) {
            var config = entry.getKey();
            var configFile = entry.getValue();

            if (configFile != null) {
                Map<String, Object> data = readConfig(configFile);
                if (data != null)
                    setBuilderValues(data, config);
            }
        }
    }

    // Create default file
    private void createDefaultConfig(File configFile, ConfigBuilder config) {
        try {
            if (configFile.createNewFile()) {
                writeDefaultValues(configFile, config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write default values
    private void writeDefaultValues(File configFile, ConfigBuilder config) throws IOException {
        Writer writer = new PrintWriter(configFile);
        List<ConfigOption> configOptions = createOptionsFromFields(getFieldsFromBuilder(config));
        writer.write(YAMLSerializer.serializeAll(configOptions));
        writer.close();
    }


    // Read from existing
    private Map<String, Object> readConfig(File configFile) {
        try {
            InputStream inputStream = new FileInputStream(configFile);
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setBuilderValues(Map<String, Object> loadedData, ConfigBuilder config) {
        var configOptions= createOptionsFromFields(getFieldsFromBuilder(config));
        for (var configOption : configOptions) {

            if (loadedData.containsKey(configOption.getKey())) {
                Object loadedEntry = loadedData.get(configOption.getKey());
                try {
                    YAMLSerializer.deserialize(configOption, loadedEntry);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Get
    private List<Field> getFieldsFromBuilder(ConfigBuilder config) {
        List<Field> configOptions = new ArrayList<>();

        for (Field field : config.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                configOptions.add(field);
            }
        }
        return configOptions;
    }


    private List<ConfigOption> createOptionsFromFields(List<Field> fields) {
        List<ConfigOption> configOptions = new ArrayList<>();
        for (var f : fields) {
            configOptions.add(createOptionFromField(f));
        }
        return configOptions;
    }

    private ConfigOption createOptionFromField(Field field) {
        try {
            ConfigOption option = new ConfigOption(field.getName(), field.get(null), field);

            if(field.isAnnotationPresent(Description.class)) {
                option.setDescription(field.getAnnotation(Description.class).value());
            }

            if(field.isAnnotationPresent(IntRange.class)) {
                var f = field.getAnnotation(IntRange.class);
                option.setMin(f.min());
                option.setMax(f.max());
            }

            return option;

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}