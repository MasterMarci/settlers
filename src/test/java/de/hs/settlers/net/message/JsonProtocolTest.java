package de.hs.settlers.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.hs.settlers.model.dynamic.DynamicModel;
import de.hs.settlers.model.dynamic.DynamicModelObject;
import de.hs.settlers.net.JsonCommunication;

public class JsonProtocolTest {
	@Test
	public void testJsonProtocol() {
		JsonCommunication com = new JsonCommunication();
		com.setTestMode(true);
		DynamicModel model = com.getModel();
		model.getCollection("SettlersOfCatanGame").getAsListProperties().add("users");
		
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"users\",\"@nv\":\"UserAssets@6633d742\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"name\",\"@nv\":\"narrowtux's Game\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"running\",\"@nv\":\"false\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"round\",\"@nv\":\"0\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"speed\",\"@nv\":\"1.4\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"UserAssets@6633d742\",\"@prop\":\"game\",\"@nv\":\"SettlersOfCatanGame@45e5182f\"}");
		assertEquals(1, model.getCollection("SettlersOfCatanGame").getObjects().size());
		DynamicModelObject game = model.getCollection("SettlersOfCatanGame").getObject("45e5182f");
		assertEquals(5, game.getProperties().size());
		assertTrue(List.class.isAssignableFrom(game.getProperties().get("users").getValue().getClass()));
		DynamicModelObject userAssets = model.getObject("UserAssets@6633d742");
		List users = (List) game.getProperty("users", List.class);
		assertEquals(userAssets, users.get(0));
		assertEquals("narrowtux's Game", game.getProperty("name", String.class).getValue());
		assertEquals(false, game.getProperty("running", Boolean.class).getValue());
		assertEquals(0, game.getProperty("round", Integer.class).getValue());
		assertEquals(1.4, game.getProperty("speed", Double.class).getValue());
		

		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"UserAssets@6633d742\",\"@prop\":\"game\",\"@ov\":\"SettlersOfCatanGame@45e5182f\"}");
		com.readLine("{\"@ts\":\"1371036625730\",\"@src\":\"SettlersOfCatanGame@45e5182f\",\"@prop\":\"users\",\"@ov\":\"UserAssets@6633d742\"}");
		
		assertFalse(users.contains(userAssets));
		assertNull(userAssets.getProperties().get("game").getValue());
	}
}
