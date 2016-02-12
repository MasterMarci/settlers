package de.hs.settlers.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {
	public static final Pattern OBJECT_NAME_PATTERN = 
			Pattern.compile("^([A-Z0-9 ]+) ([A-Z]+=.+)$");
	public static final Pattern KEY_VALUE_PATTERN = 
			Pattern.compile("^([A-Z0-9]+)=([^=]+)( [A-Z]+=.+)?$");

	public static ParseResult parseKeyValueLine(String line) {
		Matcher object = OBJECT_NAME_PATTERN.matcher(line.trim());
		if (!object.matches()) {
			return null;
		}
		String objectName = object.group(1);
		HashMap<String, String> data = new HashMap<String, String>();

		String vals = object.group(2);
		do {
			Matcher matcher = KEY_VALUE_PATTERN.matcher(vals);

			if (!matcher.matches()) {
				break;
			}

			String key = matcher.group(1);
			String value = matcher.group(2);
			data.put(key, value);
			vals = matcher.group(3);
			if (vals != null) {
				vals = vals.trim();
			}
		} while (vals != null);

		return new ParseResult(objectName, data);
	}

	public static class ParseResult {
		private String objectName;
		private HashMap<String, String> data;

		public ParseResult(String objectName, HashMap<String, String> data) {
			super();
			this.objectName = objectName;
			this.data = data;
		}

		public String getObjectName() {
			return objectName;
		}

		public HashMap<String, String> getData() {
			return data;
		}

		public String get(String key) {
			return getData().get(key);
		}
	}
}
