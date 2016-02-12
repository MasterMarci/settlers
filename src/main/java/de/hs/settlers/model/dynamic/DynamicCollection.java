package de.hs.settlers.model.dynamic;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DynamicCollection extends ModelTreeItem {
	private StringProperty type = new SimpleStringProperty();
	private ObservableList<String> asListProperties = FXCollections.observableArrayList();
	private ObservableMap<String, DynamicModelObject> objects = FXCollections.observableMap(new HashMap<String, DynamicModelObject>());

	{
		objects.addListener(new MapChangeListener<String, DynamicModelObject>() {

			@Override
			public void onChanged(MapChangeListener.Change<? extends String, ? extends DynamicModelObject> change) {
				if (change.wasAdded()) {
					change.getValueAdded().setId(change.getKey());
					change.getValueAdded().setCollection(DynamicCollection.this);
					getChildren().add(change.getValueAdded());
				}
				if (change.wasRemoved()) {
					change.getValueRemoved().setCollection(null);
					getChildren().add(change.getValueRemoved());
				}
			}
		});
	}

	public DynamicCollection(String type) {
		setType(type);
		valueProperty().set("Collection: " + type);
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getType() {
		return this.type.get();
	}

	StringProperty typeProperty() {
		return this.type;
	}

	public DynamicModelObject getObject(String id) {
		DynamicModelObject object = objects.get(id);
		if (object == null) {
			object = new DynamicModelObject();
			objects.put(id, object);
		}
		return object;
	}

	public ObservableMap<String, DynamicModelObject> getObjects() {
		return objects;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	public ObservableList<String> getAsListProperties() {
		return asListProperties;
	}
}
