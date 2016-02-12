package de.hs.settlers.model.map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

@SuppressWarnings("rawtypes")
public class MapContentHolder<T extends MapContent> {
	private SimpleObjectProperty<T> content = new SimpleObjectProperty<>();
	
	public T getContent() {
		return content.get();
	}
	
	public void setContent(T content) {
		this.content.set(content);
	}
	
	public ObjectProperty<T> contentProperty() {
		return content;
	}
}
