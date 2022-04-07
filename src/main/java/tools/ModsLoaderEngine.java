package tools;

import lombok.NonNull;
import mod.fomod.ModExample;
import fox.Out;
import fox.Out.LEVEL;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static registry.Registry.modsDir;

/**
 * 1) Все моды должны кончаться на "Mod"
 */
public class ModsLoaderEngine extends ClassLoader {
    private static final Map<String, Class<?>> cachedClassMap = new HashMap<>();
    private static List<ModExample> runnedModsList = new ArrayList<>();

    public ModsLoaderEngine() {
        super("ModLoader", getSystemClassLoader());
        if (modsDir.toFile().listFiles() == null || modsDir.toFile().listFiles().length == 0) {
            return;
        }

        Map<String, JarFile> existingJarMap = new HashMap<>();
        for (File file : modsDir.toFile().listFiles()) {
            if (file.getName().endsWith(".jar")) {
                String jarPath = modsDir + "\\" + file.getName();
                try {
                    JarFile jar = new JarFile(jarPath);
                    existingJarMap.put(jar.getName(), jar);
                } catch (Exception e) {
                    Out.Print(ModsLoaderEngine.class, LEVEL.ERROR, "Ошибка при загрузке jar '" + jarPath + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        if (existingJarMap.size() > 0) {
            Out.Print(ModsLoaderEngine.class, LEVEL.ACCENT, "Подключение " + existingJarMap.size() + " mодов...");
            loadMap(existingJarMap);
        }
    }

    private void loadMap(@NonNull Map<String, JarFile> existingJarMap) {
        Out.Print(ModsLoaderEngine.class, LEVEL.ACCENT, "Вход в loadMap с модами: " + List.of(existingJarMap.keySet()));

        String modJarPath = null;
        Enumeration<JarEntry> jarEntries;
        for (Entry<String, JarFile> modData : existingJarMap.entrySet()) {
            try {
                modJarPath = modsDir + "\\" + modData.getKey();
                jarEntries = modData.getValue().entries();

                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                        continue;
                    }

                    Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Загружаем класс: " + jarEntry + "...");
                    cashing(modData.getValue(), jarEntry);
                }

                launchMods();
            } catch (IOException io) {
                Out.Print(ModsLoaderEngine.class, LEVEL.WARN, "Ошибка чтения класса '" + modJarPath + "': " + io.getMessage());
                io.printStackTrace();
            } catch (NoClassDefFoundError ncd) {
                Out.Print(ModsLoaderEngine.class, LEVEL.WARN, "Не найдено определение класса: " + ncd.getMessage());
                ncd.printStackTrace();
            } catch (ClassNotFoundException cnf) {
                Out.Print(ModsLoaderEngine.class, LEVEL.WARN, "Не обнаружен класс: " + cnf.getMessage());
                cnf.printStackTrace();
            } catch (NoSuchMethodException nsm) {
                Out.Print(ModsLoaderEngine.class, LEVEL.WARN, "Не обнаружен метод класса '" + modJarPath + "': " + nsm.getMessage());
                nsm.printStackTrace();
            } catch (Exception b) {
                Out.Print(ModsLoaderEngine.class, LEVEL.WARN, "Ошибка с подключением мода '" + modJarPath + "': " + b.getMessage());
                b.printStackTrace();
            }
        }

        Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Работа loadPlugins завершена!\n");
    }

    private void cashing(@NonNull JarFile jarFile, @NonNull JarEntry jarEntry) throws NoClassDefFoundError, IOException, ClassNotFoundException {
        String jarEntryReplace = jarEntry.getName().replace('/', '.');
        if (jarEntryReplace.equals("mod.fomod.ModExample.class")) {return;}
        String jarCutted = jarEntryReplace.substring(0, jarEntry.getName().length() - 6);
        byte[] classData = loadClassData(jarFile, jarEntry);

        Out.Print(ModsLoaderEngine.class, LEVEL.ACCENT, "Try to define the class " + jarCutted + "...");

        Class<?> clazz;
        try {
            clazz = defineClass(jarCutted, classData, 0, classData.length);
            resolveClass(clazz);
        } catch (NoClassDefFoundError e) {
            throw e;
        }
        cachedClassMap.put(jarCutted, clazz);
        Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Successful defined " + jarCutted + ".\n");
    }

    private static byte[] loadClassData(@NonNull JarFile jarFile, @NonNull JarEntry jarEntry) throws IOException {
        byte[] buffer;
        try (InputStream is = jarFile.getInputStream(jarEntry)) {
            int available = is.available();
            if (available <= 0) {return null;}
            buffer = new byte[available];
            DataInputStream dis = new DataInputStream(is);
            dis.readFully(buffer);
            dis.close();
        }
        return buffer;
    }

    private void launchMods() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Entry<String, Class<?>>> allowedModsDoors = cachedClassMap.entrySet().stream().filter(e -> e.getKey().endsWith("Core")).toList();
        Out.Print(ModsLoaderEngine.class, LEVEL.ACCENT, "Одобрено модов: " + allowedModsDoors.size() + ". Начинаем подключение...");

        for (Entry allowedEntry : Collections.unmodifiableList(allowedModsDoors)) {
            ModExample mod;
            Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Вытаскиваем из карты класс " + allowedEntry.getKey() + " и пытаемся получить инстанс мода...");

            mod = (ModExample) cachedClassMap.get(allowedEntry.getKey()).getDeclaredConstructor().newInstance();
            mod.start();
            while (mod.getType() == null) {

            }
            runnedModsList.add(mod);
            Out.Print(ModsLoaderEngine.class, LEVEL.ACCENT, "Мод '" + mod.getName() + "' (" + mod.getDescription() + ") успешно запущен.");
        }
    }

    public static void stopMods() throws Exception {
        Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Остановка модов...");

        for (ModExample runnedMod : Collections.unmodifiableList(runnedModsList)) {
            runnedMod.stop();
            Out.Print(ModsLoaderEngine.class, LEVEL.INFO, "Мод '" + runnedMod.getName() + "' (" + runnedMod.getDescription() + ") остановлен.");
        }
    }

    public static int getReadyModsCount() {
        return runnedModsList != null ? runnedModsList.size() : 0;
    }
}