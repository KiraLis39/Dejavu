package door;

import fox.Out;

import java.awt.Toolkit;

public class Exit {
	
	public static void exit(int i) {exit(i, null);}
	
	public static void exit(int i, String comment) {
		Toolkit.getDefaultToolkit().beep();
		
		Out.Print(Exit.class, Out.LEVEL.ACCENT, "Сохранение...");
		IOM.saveAll();
		
		Out.Print(Exit.class, Out.LEVEL.ERROR, "Код #" + i);
		if (comment != null) {Out.Print(Exit.class, Out.LEVEL.ACCENT, "Комментарий завершения: " + comment);}
		try {Thread.sleep(250);} catch (InterruptedException ex) {/* IGNORE SLEEP */}
		
		System.exit(i);
	}
}
