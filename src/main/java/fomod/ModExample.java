package fomod;

public abstract class ModExample implements Plugin, Runnable {
	public static enum MOD_TYPE {OTHER}
	
	private static int modsCounter = 0;
	
	private int modID = 0;
	
	private MOD_TYPE modType;
	private String modName;
	private String modVerse;
	private String modAutor;
	private String modDescript;
	
	private Thread modThread;
	
	@Override
	public void init(MOD_TYPE other, String name, String version, String autor, String descript) {
		this.modType = other;
		this.modName = name;
		this.modVerse = version;
		this.modAutor = autor;
		this.modDescript = descript;
		
		this.modID = modsCounter;
		modsCounter++;
	}
	
	@Override
	public void start() {
		modThread = new Thread(this);
		modThread.setDaemon(true);
		modThread.start();
	}
	
	@Override public void setID(int modID) {this.modID = modID;}
	@Override	public void setType(MOD_TYPE modType) {this.modType = modType;}
	@Override	public void setName(String modName) {this.modName = modName;}
	@Override	public void setVersion(String modVerse) {this.modVerse = modVerse;}
	@Override	public void setAutor(String modAutor) {this.modAutor = modAutor;}
	@Override	public void setDescript(String modDescript) {this.modDescript = modDescript;}
	
	@Override	public int getID() {return this.modID;}
	@Override	public MOD_TYPE getType() {return this.modType;}
	@Override	public String getName() {return this.modName;}
	@Override	public String getVersion() {return this.modVerse;}
	@Override	public String getAutor() {return this.modAutor;}
	@Override	public String getDescript() {return this.modDescript;}
}