package de.hs.settlers.model.map;

public class MapType {
	private String name;
	private String image;
	
	public MapType(String name, String image) {
		this.name = name;
		this.image = image;
		MapTypes.registerType(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}
}
