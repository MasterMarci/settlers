package de.hs.settlers.model.map;

import java.util.HashMap;

public enum Direction {
	NORTH("N", 270),
	NORTH_EAST("NE", 315),
	EAST("E", 0),
	SOUTH_EAST("SE", 45),
	SOUTH("S", NORTH, 90),
	SOUTH_WEST("SW", NORTH_EAST, 135),
	WEST("W", EAST, 180),
	NORTH_WEST("NW", SOUTH_EAST, 225),
	;
	
	private Direction opposite;
	private String code;
	private double angle;
	private static final HashMap<String, Direction> CODES = new HashMap<>();
	
	static {
		for (Direction dir : values()) {
			CODES.put(dir.getCode(), dir);
		}
	}
	
	private Direction(String code, double angle) {
		this.code = code;
		this.angle = angle;
	}
	
	private Direction(String code, Direction opposite, double angle) {
		this(code, angle);
		this.opposite = opposite;
		opposite.opposite = this;
	}
	
	public Direction getOpposite() {
		return opposite;
	}
	
	public String getCode() {
		return code;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public static Direction byCode(String code) {
		return CODES.get(code);
	}
}
