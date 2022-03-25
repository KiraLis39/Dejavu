package tools;

import fox.FoxCursor;
import interfaces.Cached;

import java.awt.*;
import java.awt.image.BufferedImage;

public enum Cursors implements Cached {
    SimpleCursor("curSimpleCursor"),
    TextCursor("curTextCursor"),
    GalleryCursor("curGalleryCursor"),
    PinkCursor("curPinkCursor"),
    OtherCursor("curOtherCursor"),
    CrossCursor("curCrossCursor"),
    BlueCursor("curBlueCursor"),
    OrangeCursor("curOrangeCursor");

    String value;

    Cursors(String value) {
        this.value = value;
    }

    public Cursor get() {
        return FoxCursor.createCursor((BufferedImage) cache.get(this.value()), this.value());
    }

    private String value() {
        return value;
    }
}
