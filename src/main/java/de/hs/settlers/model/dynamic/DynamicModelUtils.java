package de.hs.settlers.model.dynamic;

public class DynamicModelUtils {
	public static DynamicModelObject getObjectByAdress(String address, DynamicModel model) {
		String splt[] = address.split("@");
		String type = splt[0];
		String id = splt[1];
		return model.getCollection(type).getObject(id);
	}
}
