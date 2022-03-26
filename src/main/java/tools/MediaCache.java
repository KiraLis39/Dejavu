package tools;

import lombok.NonNull;
import render.FoxRender;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public void add(@NonNull String name, Object mustCached) {
        if (mustCached != null) {
            map.put(name, mustCached);
        }
    }

    public Object get(@NonNull String resourceName) {
        if (map.containsKey(resourceName)) {
            return map.get(resourceName);
        }
        return null;
    }
}
