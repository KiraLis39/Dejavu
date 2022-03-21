package resourses;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;

import builders.FoxFontBuilder;



public class Registry {
	public static final String version = "0.1.3.5";
	
	public static final File picDir = new File("./resources/pictures/");
	public static final File curDir = new File("./resources/cur/");
	public static final File usersDir = new File("./users/");
	public static final File modsDir = new File("./mods/");
	public static final File dataDir = new File("./data/");
	public static final File blockPath = new File("./data/db/");
	public static final File scenesDir = new File("./resources/pictures/scenes/");
	public static final File personasDir = new File("./resources/pictures/personas/");
	public static final File npcAvatarsDir = new File("./resources/pictures/npc/");
	public static final File lastUserFile = new File("./users/luser.dat");

	public static final String picExtention = ".png";

	public static Font f0 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 20, true);
	public static Font f1 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 18, true);
	public static Font f2 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 16, true);
	public static Font f5 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 22, true);
	public static Font f3 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.SEGOE_SCRIPT, 18, false);
	public static Font f4 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.SEGOE_SCRIPT, 26, true);
	
	public static void render(Graphics2D g2D, boolean hq) {
//		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		if (hq) {g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		} else {g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);}
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
}
