package by.fxg.speceditor.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.i18n.I18n;
import by.fxg.speceditor.SpecEditor;

public class Utils {
	private static final Pattern timePattern = Pattern.compile("([0-9]+)([wdhms])");
	private static final String[] PREDEFINED_DF_FORMATS = {"#", "#.#", "#.##", "#.###", "#.####", "#.#####", "#.######"};
	private static final DecimalFormat PREDEFINED_DF = new DecimalFormat("#");
	public static final String PATH_SYMBOLS = "acbdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_/.";
	public static FileNameExtensionFilter FILENAMEFILTER_MODELS, FILENAMEFILTER_IMAGES;
	
	public static void init() {
		FILENAMEFILTER_MODELS = new FileNameExtensionFilter(format(I18n.get("speceditor.utils.fileNameFilter.models"), " (obj; g3db; gltf)"), "obj", "g3db", "g3dj", "gltf", "glb");
		FILENAMEFILTER_IMAGES = new FileNameExtensionFilter(format(I18n.get("speceditor.utils.fileNameFilter.images"), " (png; jpeg; bmp; etc; ktx)"), "png", "jpg", "jpeg", "bmp", "etc1", "ktx", "zktx");
		
	}
	
	/** SpecEditor window width **/
	public static int getWidth() { return SpecEditor.get.width; }
	
	/** SpecEditor window height **/
	public static int getHeight() { return SpecEditor.get.height; }
	
	/** Returns parsed time in seconds in format: [1D 2H 3M 4S 5D...] 
	 *  Time units: [S]econds, [M]inutes, [H]ours, [D]ays, [W]eeks
	 * **/
	public static long parseTime(String string) {
		if (string != null) {
			long time = 0L;
			Matcher matcher = timePattern.matcher(string.toLowerCase());
			while (matcher.find()) {
				long timeUnit = Long.parseLong(matcher.group(1));
				switch (matcher.group(2)) {
					case "s": time += timeUnit; break;
					case "m": time += timeUnit * 60L; break;
					case "h": time += timeUnit * 3600L; break;
					case "d": time += timeUnit * 86400L; break;
					case "w": time += timeUnit * 604800L; break;
				}
			}
			return time;
		}
		return -1L;
	}
	
	/**<pre> Calling examples:
	 *    isValidPath("c:/test");      //returns true
	 *    isValidPath("c:/te:t");      //returns false
	 *    isValidPath("c:/te?t");      //returns false
	 *    isValidPath("c/te*t");       //returns false
	 *    isValidPath("good.txt");     //returns true
	 *    isValidPath("not|good.txt"); //returns false
	 *    isValidPath("not:good.txt"); //returns false </pre> */
	public static boolean isPathValid(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}
	
	/** Replaces materials in specified ModelInstance with new ones specified in newMaterials array **/
	public static void replaceMaterialsInModelInstance(ModelInstance modelInstance, Array<Material> newMaterials) {
		Map<Material, Material> replacementMap = new HashMap<>();
		if (modelInstance != null && newMaterials != null && newMaterials.size > 0) {
			for (int i = 0; i != modelInstance.materials.size; i++) {
				for (int j = 0; j != newMaterials.size; j++) {
					if (modelInstance.materials.get(i).id.equals(newMaterials.get(j).id)) {
						replacementMap.put(modelInstance.materials.get(i), newMaterials.get(j));
						modelInstance.materials.set(i, newMaterials.get(j));
					}
				}
			}
			for (int i = 0; i != modelInstance.nodes.size; i++) {
				replaceMaterialsInModelInstanceNodes(modelInstance.nodes.get(i), replacementMap);
			}
		}
	}
	
	private static void replaceMaterialsInModelInstanceNodes(Node node, Map<Material, Material> replacementMap) {
		Material tmp = null;
		for (int i = 0; i != node.parts.size; i++) {
			if ((tmp = replacementMap.get(node.parts.get(i).material)) != null) {
				node.parts.get(i).material = tmp;
			}
		}
		for (int i = 0; i != node.getChildCount(); i++) {
			replaceMaterialsInModelInstanceNodes(node.getChild(i), replacementMap);
		}
	}
	
	public static String dFormat(double value, int symbolsAfterDot) {
		PREDEFINED_DF.applyPattern(PREDEFINED_DF_FORMATS[symbolsAfterDot]);
		return PREDEFINED_DF.format(value);
	}
	
	public static String format(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) builder.append(object);
		return builder.toString();
	}
	
	public static void logInfo(String tag, Object... objects) { Gdx.app.log(format("INFO][", tag), format(objects)); }
	public static void logWarn(String tag, Object... objects) { Gdx.app.error(format("WARN][", tag), format(objects)); }
	public static void logError(Throwable throwable, String tag, Object... objects) {
		Gdx.app.error(format("ERROR][", tag), format(objects));
		if (SpecEditor.DEBUG && throwable != null) throwable.printStackTrace();
	}
	public static void logDebug(Object... objects) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		Gdx.app.debug(format("DEBUG][", element.getClassName(), ":", element.getLineNumber()), format(objects));
	}
	public static void logDebugWarn(Object... objects) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		Gdx.app.error(format("DEBUG-WARN][", element.getClassName(), ":", element.getLineNumber()), format(objects));
	}
}
