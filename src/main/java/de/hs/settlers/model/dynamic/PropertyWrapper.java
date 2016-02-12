package de.hs.settlers.model.dynamic;

import java.util.List;
import java.util.Map.Entry;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

public class PropertyWrapper extends ModelTreeItem {
	@SuppressWarnings("rawtypes")
	private Property property;
	private String key;
	private boolean lazy = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PropertyWrapper(Property property, String key) {
		super();
		this.property = property;
		this.key = key;
		if (property instanceof ListProperty) {
			ListProperty list = (ListProperty) property;
			for (Object o : list) {
				if (o instanceof DynamicModelObject) {
					getChildren().add(new PropertyWrapper(new SimpleObjectProperty<DynamicModelObject>((DynamicModelObject) o), "item"));
				}
			}
			list.addListener(new ListChangeListener() {
				@Override
				public void onChanged(Change change) {
					updateValue(change.getList());
					while (change.next()) {
						if (change.wasAdded()) {
							for (Object i : change.getAddedSubList()) {
								if (i instanceof DynamicModelObject) {
									getChildren().add(new PropertyWrapper(new SimpleObjectProperty<DynamicModelObject>((DynamicModelObject) i), "item"));
								}
							}
						}
						if (change.wasRemoved()) {
							// TODO?
						}
					}
				}
			});
		} else {
			property.addListener(new ChangeListener<Object>() {
				@Override
				public void changed(ObservableValue<? extends Object> obj, Object ov, Object nv) {
					updateValue(nv);
				}
			});
		}
		updateValue(property.getValue());

		addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<Object>>() {

			@Override
			public void handle(javafx.scene.control.TreeItem.TreeModificationEvent<Object> e) {
				for (TreeItem<String> child : getChildren()) {
					child.getChildren(); // update lazy cache
				}
			}
		});
	}

	@Override
	public boolean isLeaf() {
		if (property instanceof ListProperty) {
			return false;
		} else if (property.getValue() instanceof DynamicModelObject) {
			return false;
		}
		return true;
	}

	@Override
	public ObservableList<TreeItem<String>> getChildren() {
		if (lazy) {
			lazy = false;
			if (property.getValue() instanceof DynamicModelObject) {
				DynamicModelObject dmo = ((DynamicModelObject) property.getValue());
				for (Entry<String, Property<?>> e : dmo.getProperties().entrySet()) {
					getChildren().add(new PropertyWrapper(e.getValue(), e.getKey()));
				}
				dmo.getProperties().addListener(new MapChangeListener<String, Property<?>>() {
					@Override
					public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends Property<?>> change) {
						if (change.wasAdded()) {
							getChildren().add(new PropertyWrapper(change.getValueAdded(), change.getKey()));
						}
					}
				});
			}

		}
		return super.getChildren();
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	public void updateValue(Object nv) {
		if (nv instanceof List) {
			valueProperty().set(PropertyWrapper.this.key + ": [" + ((List) nv).size() + "]");
		} else {
			valueProperty().set(PropertyWrapper.this.key + ": " + (nv == null ? "(null)" : nv.toString()));
		}
	}
}
