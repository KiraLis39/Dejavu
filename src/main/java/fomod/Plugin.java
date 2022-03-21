package fomod;

import fomod.ModExample.MOD_TYPE;

public interface Plugin {
	void init(MOD_TYPE type, String name, String version, String autor, String descript);
	void start();
	
	void setId(int id);
	int getId();
	
	void setType(MOD_TYPE modType);
	MOD_TYPE getType();
	
	void setName(String modName);
	String getName();
	
	void setVersion(String modVerse);
	String getVersion();
	
	void setAuthor(String modAuthor);
	String getAuthor();
	
	void setDescription(String modDescription);
	String getDescription();
}