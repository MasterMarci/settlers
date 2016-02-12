package de.hs.settlers.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class ParseUtilsTest {
	@Test
	public void testPattern() {
		Pattern pattern = ParseUtils.KEY_VALUE_PATTERN;

		assertTrue(pattern.matcher("TEAM=Team A USER=tuxitux OTHER=bla").matches());

		assertTrue(pattern.matcher("KEY=value hahaha ha").matches());
	}

	@Test
	public void testKeyValueParser() {
		String input = "USER TEAM=Team A USER=tuxitux OTHER=bla";

		ParseUtils.ParseResult result = ParseUtils.parseKeyValueLine(input);

		assertEquals(result.getObjectName(), "USER");
		assertEquals(result.getData().get("TEAM"), "Team A");
		assertEquals(result.getData().get("USER"), "tuxitux");
		assertEquals(result.getData().get("OTHER"), "bla");

		input = "MAP NAME=map_balanced CREATOR=SE VERSION=1";

		result = ParseUtils.parseKeyValueLine(input);

		assertEquals("MAP", result.getObjectName());
		assertEquals("map_balanced", result.get("NAME"));
		assertEquals("SE", result.get("CREATOR"));
		assertEquals("1", result.get("VERSION"));

	}
}
