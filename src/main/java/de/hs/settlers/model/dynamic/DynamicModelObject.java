package de.hs.settlers.model.dynamic;

import java.util.HashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import de.hs.settlers.util.PropertyUtils;

public class DynamicModelObject extends ModelTreeItem {
	private ObservableMap<String, Property<?>> properties = FXCollections.observableMap(new HashMap<String, Property<?>>());
	private ObjectProperty<DynamicCollection> collection = new SimpleObjectProperty<>();
	private StringProperty id = new SimpleStringProperty();
	private StringProperty type = new SimpleStringProperty();

	{
		collection.addListener(new ChangeListener<DynamicCollection>() {

			@Override
			public void changed(
					ObservableValue<? extends DynamicCollection> obj,
					DynamicCollection oldValue, DynamicCollection newValue) {
				if (oldValue != null) {
					oldValue.getObjects().remove(getId());
				}
				if (newValue != null) {
					newValue.getObjects().put(getId(), DynamicModelObject.this);
					type.set(newValue.getType());
				}

			}
		});
		valueProperty().bind(type.concat("@").concat(id));

		properties.addListener(new MapChangeListener<String, Property>() {
			@Override
			public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends Property> change) {
				if (change.wasAdded()) {
					PropertyWrapper wrapper = new PropertyWrapper(change.getValueAdded(), change.getKey());
					getChildren().add(wrapper);
				}
			}
		});

		addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<Object>>() {

			@Override
			public void handle(javafx.scene.control.TreeItem.TreeModificationEvent<Object> e) {
				for (TreeItem<String> child : getChildren()) {
					child.getChildren(); // update lazy cache
				}
			}
		});
	}

	public DynamicCollection getCollection() {
		return this.collection.get();
	}

	public void setCollection(DynamicCollection collection) {
		this.collection.set(collection);
	}

	public ObjectProperty<DynamicCollection> collectionProperty() {
		return this.collection;
	}

	public String getId() {
		return id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}

	public StringProperty idProperty() {
		return id;
	}

	public String getType() {
		return type.get();
	}

	public StringProperty typeProperty() {
		return type;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@SuppressWarnings("unchecked")
	public <T> Property getProperty(String name, Class<T> type) {
		Property<T> property = (Property<T>) properties.get(name);
		if (property != null && property.getValue() != null) {
			if (!type.isAssignableFrom(property.getValue().getClass())) {
				throw new IllegalStateException("Property " + name + " has type " + property.getValue().getClass().getSimpleName() + " but " + type.getSimpleName() + " was desired.");
			}
		}
		if (property == null) {
			property = PropertyUtils.getDesiredProperty(type);
			properties.put(name, property);
		}
		return property;
	}

	public ObservableMap<String, Property<?>> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getType()).append("@").append(getId());
		return sb.toString();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRoot() {
		return false;
	}
}
