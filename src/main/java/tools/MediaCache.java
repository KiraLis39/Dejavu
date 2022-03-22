package tools;

import java.util.HashMap;

public class MediaCache {
    private static MediaCache cache;
    private static HashMap<String, Object> map = new HashMap<>();

    private MediaCache() {}

    public static MediaCache getInstance() {
        if (cache == null) {
            cache = new MediaCache();
        }
        return cache;
    }

    public void add(String name, Object mustCached) {
        map.put(name, mustCached);
    }

    public Object get(String resourceName) {
        if (map.containsKey(resourceName)) {
            return map.get(resourceName);
        }
        return null;
    }
}
