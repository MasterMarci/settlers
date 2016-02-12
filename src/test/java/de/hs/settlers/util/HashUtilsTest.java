package de.hs.settlers.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HashUtilsTest {
	@Test
	public void testSha1Hash() {
		String in = "BLABLA";
		String out = HashUtils.hashFunctionSHA(in);
		assertEquals("b8ae8d86daa90b41f2c7b0f4a4809e86309960f8", out);
	}
	
	@Test
	public void test2Sha1Hash() {
		String in = "!QAY2wsx";
		String out = HashUtils.hashFunctionSHA(in);
		assertEquals("9fc12e727aeb686938c540fa3b303a11b2745181", out);
	}
}
