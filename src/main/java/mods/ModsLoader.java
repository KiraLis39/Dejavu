package mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import adds.Out;
import adds.Out.LEVEL;
import fomod.ModExample;


public class ModsLoader extends ClassLoader {
	private static Map<String, JarFile> existingJarMap = new HashMap<String, JarFile>();	
	private Map<String, Class<?>> cacheClass = new HashMap<>();	
	private Path modsFolder;
	// Все моды (имя модов) должны кончаться на "Mod.jar"
	
	public ModsLoader(File modsDir) {
		this.modsFolder = modsDir.toPath();		
		preparing(this.modsFolder);

		File[] filesArray = this.modsFolder.toFile().listFiles();
		if (filesArray.length > 0) {
			for (File jar : filesArray) {
				if (jar.getName().endsWith(".jar")) {
					String jarPath = this.modsFolder + "\\" + jar.getName();
					try {existingJarMap.put(jar.getName(), new JarFile(jarPath));
					} catch (IOException e) {
						 Out.Print(ModsLoader.class, LEVEL.ERROR, "Ошибка при загрузке jar '" + jarPath + "': " + e.getMessage());
//						e.printStackTrace();
						continue;
					}
				}
			}
		}
		
		if (existingJarMap.size() > 0) {
			Out.Print(ModsLoader.class, LEVEL.ACCENT, "Подключение " + existingJarMap.size() + " mодов...");			
			loadPlugins();
		}		
	}
    
	private static void preparing(Path path) {
		if (Files.notExists(path)) {
			try {Files.createDirectory(path);
			} catch (IOException e) {
				throw new RuntimeException("Can`t create mods directory!");
			}
		}		
		if (!Files.isDirectory(path)) {throw new RuntimeException("Income 'modsDir' must be a DIRECTORY, not a file!");}
	}

	
	public void loadPlugins() {
		Out.Print(ModsLoader.class, LEVEL.INFO, "\n Вход в loadPlugins с модами: " + Arrays.asList(existingJarMap.keySet()));	   	
	   	
		String jarName = null;
		for (Entry<String, JarFile> modData : existingJarMap.entrySet()) {
    	   try {
	    	   jarName = this.modsFolder + "\\" + modData.getKey();
	    	   
	    	   Out.Print(ModsLoader.class, LEVEL.INFO, "See the '" + jarName + "': Собираем классы мода...");
	           Enumeration<JarEntry> jarEntries = modData.getValue().entries();
	           while (jarEntries.hasMoreElements()) {
	               JarEntry jarEntry = jarEntries.nextElement();
	               if (jarEntry.isDirectory()) {continue;}
	               
	               if (jarEntry.getName().endsWith(".class")) {
	            	   Out.Print(ModsLoader.class, LEVEL.INFO, "Загружаем класс: " + jarEntry + "...");
	            	   cashing(modData.getValue(), jarEntry);
	               }
	           }
	
	           activization();
			} catch (Exception e) {
		    	Out.Print(ModsLoader.class, LEVEL.ERROR, "Ошибка с подключением мода '" + jarName + "': " + e.getMessage() + " (" + e.getCause() + ").");
//		    	e.printStackTrace();
		    	continue;
			}
		}
		
		Out.Print(ModsLoader.class, LEVEL.INFO, "Работа loadPlugins завершена!\n");
   }
   
	private void cashing(JarFile jarFile, JarEntry jarEntry) {
		byte[] classData = null;
       	try {classData = loadClassData(jarFile, jarEntry);
       	} catch (Exception b) {
       		Out.Print(ModsLoader.class, LEVEL.ERROR, "Ошибка при загрузке байт-массива класса '" + jarEntry + "'.");
       		b.printStackTrace();
       	}
       
       if (classData != null) {
    	   String jarEntryReplace = jarEntry.getName().replace('/', '.');
    	   String jarCutted = jarEntryReplace.substring(0, jarEntry.getName().length() - 6);
       	
    	   Class<?> clazz = null;
    	   try {
    		   Out.Print(ModsLoader.class, LEVEL.INFO, "Try to define the class " + jarCutted + "...");
    		   clazz = defineClass(jarCutted, classData, 0, classData.length);
    	   } catch (Exception c) {
    		   Out.Print(ModsLoader.class, LEVEL.ERROR, "Ошибка при определении загруженного класса по имени: " + jarCutted);
    		   c.printStackTrace();
    	   }
       	
    	   if (clazz != null) {this.cacheClass.put(jarCutted, clazz);
    	   } else {Out.Print(ModsLoader.class, LEVEL.ERROR, "Нельзя положить в карту класс, равный null: " + jarCutted + " (" + clazz + ")");}
       } else {Out.Print(ModsLoader.class, LEVEL.ERROR, "При чтении класса мода " + jarFile + " произошла ошибка!");}
	}

	private static byte[] loadClassData(JarFile jarFile, JarEntry jarEntry) throws IOException {
	       long size = jarEntry.getSize();
	       if (size <= 0) {return null;} else if (size > Integer.MAX_VALUE) {throw new IOException("Class size too long");}
	       byte[] buffer = new byte[(int) size];       
	       try (InputStream is = jarFile.getInputStream(jarEntry)) {is.read(buffer);}
	       return buffer;
	   }
   
	private void activization() {
		existingJarMap.clear();
		Out.Print(ModsLoader.class, LEVEL.ACCENT, "Одобрено модов: " + this.cacheClass.size() + ". Начинаем подключение...");
		
		//запуск мода:
		for (String pathName : this.cacheClass.keySet()) {
			if (pathName.endsWith("Mod")) {
				Out.Print(ModsLoader.class, LEVEL.INFO, "Загружаем: " + pathName);
				try {
		    		Class<?> tmp = loadClass(pathName);
					
					Out.Print(ModsLoader.class, LEVEL.INFO, "Вытаскиваем из карты классов класс " + pathName + " и пытаемся получить инстанс....");
					ModExample in = (ModExample) tmp.getDeclaredConstructor().newInstance();
					
					try {
						in.start();
						Out.Print(ModsLoader.class, LEVEL.INFO, "Мод успешно подключен.");
					} catch (Exception e) {
						e.printStackTrace();
						Out.Print(ModsLoader.class, LEVEL.WARN, "Мод '" + pathName + "' не загружен! Ошибка: " + e.getMessage());
					}
					
				} catch (ClassNotFoundException e) {
					try {System.err.println("parent on!"); loadClass(pathName);
					} catch (ClassNotFoundException e1) {
						try {System.err.println("super on!"); super.loadClass(pathName);
						} catch (ClassNotFoundException e2) {System.err.println("Exception e2 fail!");}
					}
				} catch (InstantiationException e) {e.printStackTrace();
				} catch (IllegalAccessException e) {e.printStackTrace();} catch (IllegalArgumentException e) {e.printStackTrace();
				} catch (InvocationTargetException e) {e.printStackTrace();
				} catch (NoSuchMethodException e) {e.printStackTrace();
				} catch (SecurityException e) {e.printStackTrace();
				} catch (Exception e) {e.printStackTrace();}
				
			}
		}
	}
		
	
	public static int getReadyModsCount() {return existingJarMap.size();}
}