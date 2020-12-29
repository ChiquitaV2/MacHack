package mac.hack.utils.file;

import mac.hack.module.ModuleManager;
import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacFileMang {

	private static Path dir;
	public static boolean first_run;
	public static void init() {
		dir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "mac/");
		if (!dir.toFile().exists()) {
			dir.toFile().mkdirs();
			ModuleManager.getModuleByName("HUD").setToggled(true);
			first_run = true;
			//ModuleManager.getModuleByName("IRC").setToggled(true);
		}
	}

	/**
	 * Gets the bleach directory in your minecraft folder.
	 **/
	public static Path getDir() {
		return dir;
	}

	/**
	 * Reads a file and returns a list of the lines.
	 **/
	public static List<String> readFileLines(String... file) {
		try {
			return Files.readAllLines(stringsToPath(file));
		} catch (NoSuchFileException e) {

		} catch (Exception e) {
			System.out.println("Error Reading File: " + stringsToPath(file));
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	/**
	 * Creates a file, doesn't do anything if the file already exists.
	 **/
	public static void createFile(String... file) {
		try {
			if (fileExists(file)) return;
			dir.toFile().mkdirs();
			Files.createFile(stringsToPath(file));
		} catch (Exception e) {
			System.out.println("Error Creating File: " + Arrays.toString(file));
			e.printStackTrace();
		}
	}

	/**
	 * Creates a file, clears it if it already exists
	 **/
	public static void createEmptyFile(String... file) {
		try {
			createFile(file);

			FileWriter writer = new FileWriter(stringsToPath(file).toFile());
			writer.write("");
			writer.close();
		} catch (Exception e) {
			System.out.println("Error Clearing/Creating File: " + Arrays.toString(file));
			e.printStackTrace();
		}
	}

	/**
	 * Adds a line to a file.
	 **/
	public static void appendFile(String content, String... file) {
		try {
			String fileContent = new String(Files.readAllBytes(stringsToPath(file)));
			FileWriter writer = new FileWriter(stringsToPath(file).toFile(), true);
			writer.write(
					(fileContent.endsWith("\n") || !fileContent.contains("\n") ? "" : "\n")
							+ content
							+ (content.endsWith("\n") ? "" : "\n"));
			writer.close();
		} catch (Exception e) {
			System.out.println("Error Appending File: " + Arrays.toString(file));
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if a file exists, returns false otherwise
	 **/
	public static boolean fileExists(String... file) {
		try {
			return stringsToPath(file).toFile().exists();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Deletes a file if it exists.
	 **/
	public static void deleteFile(String... file) {
		try {
			Files.deleteIfExists(stringsToPath(file));
		} catch (Exception e) {
			System.out.println("Error Deleting File: " + Arrays.toString(file));
			e.printStackTrace();
		}
	}

	/**
	 * Gets a file by walking down all of the parameters (starts at .minecraft/bleach/).
	 **/
	public static Path stringsToPath(String... strings) {
		Path path = dir;
		for (String s : strings) path = path.resolve(s);
		return path;
	}

}