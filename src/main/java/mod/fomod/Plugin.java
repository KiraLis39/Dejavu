package mod.fomod;

import java.io.Serializable;

public interface Plugin extends Serializable {
	enum MOD_TYPE {OTHER}

	void init(MOD_TYPE type, String name, String version, String author, String description);

	void start();
	void stop();

	void setId(int id);
	int getId();
	
	void setType(MOD_TYPE type);
	MOD_TYPE getType();
	
	void setName(String name);
	String getName();
	
	void setVersion(String version);
	String getVersion();
	
	void setAuthor(String author);
	String getAuthor();
	
	void setDescription(String description);
	String getDescription();
}