package fr.stillcraft.proxykick.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal dotted-path YAML document (get/set navigate nested maps, e.g. "kick.kicked").
 * Replaces net.md_5.bungee.config.Configuration so config loading isn't tied to
 * bungeecord-config's YamlConfiguration, which always builds its Yaml with a plain,
 * unsafe SnakeYAML Constructor (see YamlStore).
 */
public final class YamlDocument {
    private final Map<String, Object> data;

    public YamlDocument() { this(new LinkedHashMap<>()); }

    public YamlDocument(Map<String, Object> data) {
        this.data = (data != null) ? data : new LinkedHashMap<>();
    }

    public Map<String, Object> asMap() { return data; }

    public Object get(String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) return null;
            @SuppressWarnings("unchecked")
            Map<String, Object> nextMap = (Map<String, Object>) next;
            current = nextMap;
        }
        return current.get(parts[parts.length - 1]);
    }

    public String getString(String path) {
        Object value = get(path);
        return (value != null) ? String.valueOf(value) : "";
    }

    public boolean getBoolean(String path) {
        Object value = get(path);
        return (value instanceof Boolean) && (Boolean) value;
    }

    // Setting null removes the key (used to drop legacy keys during config migration).
    public void set(String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            Map<String, Object> nextMap;
            if (next instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> existing = (Map<String, Object>) next;
                nextMap = existing;
            } else {
                nextMap = new LinkedHashMap<>();
                current.put(parts[i], nextMap);
            }
            current = nextMap;
        }
        String lastKey = parts[parts.length - 1];
        if (value == null) {
            current.remove(lastKey);
        } else {
            current.put(lastKey, value);
        }
    }
}
