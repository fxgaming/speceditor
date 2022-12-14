package by.fxg.pilesos.i18n;

import java.util.HashMap;
import java.util.Map;

public class I18n {
	private static String language = "en";
	public static Map<String, I18nPool> map = new HashMap<>();
	
	public static void init() {
		language = System.getProperty("user.language");
		if (!map.containsKey(language)) language = "en";
		
		//freeing memory (at least i hope this freeing memory)
		for (I18nPool pool : map.values()) {
			if (!pool.language.equals(language) && !pool.language.equals("en")) {
				pool.map.clear();
			}
		}
	}
	
	public static String get(String code) {
		return map.get(language).getString(code);
	}
	
	public static void setLanguage(String key) {
		language = map.containsKey(key) ? key : "en";
	}
	
	public static String getLanguage() {
		return language;
	}

	public static void addPool(I18nPool pool) {
		if (pool != null) map.put(pool.language, pool);
	}
	
	public static I18nPool getPool(String language) {
		return map.get(language);
	}

	public static class I18nPool {
		public String language;
		public Map<String, String> map = new HashMap<>();
		
		public I18nPool(String language) {
			this.language = language;
		}
		
		public String getString(String code) {
			String value = this.map.get(code);
			return value != null ? value : code;
		}
	}
}
