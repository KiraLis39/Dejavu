package ru.dejavu;

import java.awt.Toolkit;

import adds.IOM;
import adds.Out;
import adds.Out.LEVEL;


public class Exit {
	
	public static void exit(int i) {exit(i, null);}
	
	public static void exit(int i, String comment) {
		Toolkit.getDefaultToolkit().beep();
		
		Out.Print(Exit.class, LEVEL.ACCENT, "Сохранение...");
		IOM.saveAll();
		
		Out.Print(Exit.class, LEVEL.ERROR, "Код #" + i);
		if (comment != null) {Out.Print(Exit.class, LEVEL.ACCENT, "Комментарий завершения: " + comment);}
		try {Thread.sleep(250);} catch (InterruptedException ex) {/* IGNORE SLEEP */}
		
		System.exit(i);
	}
}
