package registry;

import configurations.Configuration;
import configurations.UserConf;
import fox.FoxFontBuilder;
import java.awt.Font;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Registry {
	// GLOBAL DATA:
	public static final String version = "0.1.4.0";
	public static final Charset charset = StandardCharsets.UTF_8;
	public static final String picExtension = ".png";

	// CONFIGURATIONS:
	public static Configuration configuration;
	public static UserConf userConf;

	// DIRECTORIES:
	public static final Path picDir = Paths.get("resources/pictures/");
	public static final Path curDir = Paths.get("resources/cur/");
	public static final Path usersDir = Paths.get("users/");
	public static final Path modsDir = Paths.get("mod/");
	public static final Path dataDir = Paths.get("data/");
	public static final Path blockPath = Paths.get("data/db/");
	public static final Path scenesDir = Paths.get("resources/pictures/scenes/");
	public static final Path personasDir = Paths.get("resources/pictures/personas/");
	public static final Path npcAvatarsDir = Paths.get("resources/pictures/npc/");

	public static Path usersSaveDir;

	// FILES:
	public static final Path globalConfigFile = Paths.get("data/configuration.json");

	// FONTS:
	public static final Font f0 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 20, true);
	public static final Font f1 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 18, true);
	public static final Font f2 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 16, true);
	public static final Font f3 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.SEGOE_SCRIPT, 18, false);
	public static final Font f4 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.SEGOE_SCRIPT, 26, true);
	public static final Font f5 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSTANTIA, 28, true);
	public static final Font f6 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 28, true);
	public static final Font f7 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 22, true);
	public static final Font f8 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.MONOTYPE_CORSIVA, 24, true);
	public static final Font f9 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL, 30, true);
	public static final Font fontDialog = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 22, false);
	public static final Font fontName = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 22, true);
}
