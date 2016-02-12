package de.hs.settlers.model.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hs.settlers.net.message.text.DownloadMapMessage;
import de.hs.settlers.net.message.text.ListMapsMessage;

public class MapTest {
	@Test
	public void testMapParser() {
		String in = "CREATOR: tux\nVERSION: 3\n1;DESERT;0;N,2;\n2;MOUNTAIN;360;S,1;\n";
		Map map = new Map();
		map.setMapData(in);
		map.parseMap();
		MapField id1 = map.getField(1);
		MapField id2 = map.getField(2);

		assertEquals(MapTypes.DESERT, id1.getType());
		assertEquals(MapTypes.MOUNTAIN, id2.getType());

		assertNotNull(id1);
		assertNotNull(id2);

		assertNull(map.getField(0));

		assertEquals(id2, id1.getConnections().get(Direction.NORTH));

		assertEquals(3, map.getVersion());
		assertEquals("tux", map.getCreator());

		assertEquals(0, id1.getRotation());
		assertEquals(0, id2.getRotation());
	}

	@Test
	public void testMapManager() {
		MapManager manager = new MapManager();
		ListMapsMessage lmm = new ListMapsMessage();
		lmm.setData("MAP NAME=dust3 VERSION=3 CREATOR=tux");
		manager.getMapListListener().onMessage(lmm);

		Map dust3 = manager.getMapWithName("dust3");

		assertNotNull(dust3);

		DownloadMapMessage dmm = new DownloadMapMessage();
		dmm.setAnswerTo(new DownloadMapMessage(dust3.getMapTitle()));
		dmm.setData("CREATOR: tux\nVERSION: 3\n1;DESERT;0;N,2;\n2;MOUNTAIN;360;S,1;\n");
		manager.getDownloadMapListener().onMessage(dmm);
		assertTrue(manager.getMapWithName("dust3").getFields().size() == 2);

		Map map = new Map();
		map.setMapTitle("dust2");

		manager.getMaps().add(map);

		assertEquals(map, manager.getMapWithName("dust2"));

		assertEquals(manager, map.getMapManager());

	}
}
