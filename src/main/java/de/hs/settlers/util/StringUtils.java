package de.hs.settlers.util;

import java.util.List;

public class StringUtils {
	public static String glue(List<String> strings, String delimiter) {
		StringBuilder sb = new StringBuilder();
		int length = strings.size();
		int i = 0;
		for (String string : strings) {
			sb.append(string);
			if (i < length - 1) {
				sb.append(delimiter);
			}
			i++;
		}
		return sb.toString();
	}
}
