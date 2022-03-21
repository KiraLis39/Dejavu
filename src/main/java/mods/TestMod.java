package mods;

import adds.Out;
import adds.Out.LEVEL;
import fomod.ModExample;


public class TestMod extends ModExample {

	@Override
	public void run() {
		init(MOD_TYPE.OTHER, "Test mod", "0.0.0.1-Alpha", "KiraLis39", "Without comments...");
		Out.Print(getClass(), LEVEL.ACCENT, "Подключен мод: '" + getName() + "' v." + getVersion() + " (" + getAutor() + ")");
		
		while (true) {
			System.out.println(">>> MOD ACTIVED");
			try {Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
}