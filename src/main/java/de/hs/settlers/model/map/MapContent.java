package de.hs.settlers.model.map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents something that you can place on a T
 * @param <T> one of {MapField, FieldCorner, FieldEdge}
 */
@SuppressWarnings("rawtypes")
public class MapContent<T extends MapContentHolder> {
	private SimpleObjectProperty<T> on = new SimpleObjectProperty<>();
	
	public T getOn() {
		return on.get();
	}
	
	public void setOn(T on) {
		this.on.set(on);
	}
	
	public ObjectProperty<T> onProperty() {
		return on;
	}
}
