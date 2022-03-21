package mods;

import fomod.ModExample;
import fox.Out;
import fox.Out.LEVEL;

import java.util.concurrent.TimeUnit;

public class TestMod extends ModExample {
    @Override
    public void run() {
        init(MOD_TYPE.OTHER, "Test mod", "0.0.0.1-Alpha", "KiraLis39", "Without comments...");
        Out.Print(getClass(), LEVEL.ACCENT, "Подключен мод: '" + getName() + "' v." + getVersion() + " (" + getAuthor() + ")");

        while (true) {
            System.out.println(">>> TEST MOD ACTIVE");
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}