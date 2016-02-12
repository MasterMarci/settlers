package de.hs.settlers.model.map;

import java.util.HashMap;

public class MapTypes {
	private static final HashMap<String, MapType> TYPES = new HashMap<>();
	public static final MapType WATER = new MapType("WATER", "water.png");
	public static final MapType PORT_WOOL = new MapType("PORT_WOOL", "dock-wool.png");
	public static final MapType PORT_3TO1 = new MapType("PORT_3TO1", "dock.png");
	public static final MapType FIELD = new MapType("FIELD", "grain.png");
	public static final MapType PASTURE = new MapType("PASTURE", "pasture.png");
	public static final MapType FOREST = new MapType("FOREST", "forest.png");
	public static final MapType HILL = new MapType("HILL", "field.png");
	public static final MapType MOUNTAIN = new MapType("MOUNTAIN", "hill.png");
	public static final MapType DESERT = new MapType("DESERT", "desert.png");
	public static final MapType PORT_ORE = new MapType("PORT_ORE", "dock-ore.png");
	public static final MapType PORT_BRICK = new MapType("PORT_BRICK", "dock-brick.png");
	public static final MapType PORT_GRAIN = new MapType("PORT_GRAIN", "dock-grain.png");
	public static final MapType PORT_LUMBER = new MapType("PORT_LUMBER", "dock-lumber.png");
	public static final MapType BARBARIAN_START = new MapType("BARBARIAN_START", "barbarian-start.png");
	public static final MapType BARBARIAN_END = new MapType("BARBARIAN_END", "barbarian-end.png");
	
	public static MapType byName(String name) {
		return TYPES.get(name);
	}	
	
	static void registerType(MapType type) {
		TYPES.put(type.getName(), type);
	}
}
