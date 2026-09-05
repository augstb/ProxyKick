package fr.stillcraft.proxykick.core;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads/saves YamlDocuments using SafeConstructor rather than SnakeYAML's plain, unsafe
 * Constructor - net.md_5.bungee.config.YamlConfiguration always builds its Yaml with a plain
 * Constructor, which lets a crafted YAML file (e.g. a "!!javax.script.ScriptEngineManager" tag)
 * instantiate arbitrary Java classes on load (CVE-2022-1471). config.yml/locale files are meant to
 * be admin-authored, but there's no reason to keep that footgun around.
 */
final class YamlStore {
    private YamlStore() {}

    private static Yaml newYaml() {
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(new SafeConstructor(loaderOptions), new Representer(dumperOptions), dumperOptions);
    }

    static YamlDocument load(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Object loaded = newYaml().load(reader);
            Map<String, Object> map;
            if (loaded instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> casted = (Map<String, Object>) loaded;
                map = casted;
            } else {
                map = new LinkedHashMap<>();
            }
            return new YamlDocument(map);
        }
    }

    static void save(YamlDocument document, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            newYaml().dump(document.asMap(), writer);
        }
    }
}
