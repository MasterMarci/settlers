package de.hs.settlers.model.dynamic;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class DynamicModel extends ModelTreeItem {
	private ObservableMap<String, DynamicCollection> collections = FXCollections.observableMap(new HashMap<String, DynamicCollection>());

	{
		valueProperty().set("Model root");
		getCollections().addListener(new MapChangeListener<String, DynamicCollection>() {
			@Override
			public void onChanged(MapChangeListener.Change<? extends String, ? extends DynamicCollection> change) {
				if (change.wasAdded()) {
					getChildren().add(change.getValueAdded());
				} else if (change.wasRemoved()) {
					getChildren().add(change.getValueRemoved());
				}
			}
		});
	}

	/**
	 * @param type
	 *            the type the collection holds
	 * @return either an existing or new collection for the given type
	 */
	public DynamicCollection getCollection(String type) {
		DynamicCollection collection = collections.get(type);
		if (collection == null) {
			collection = new DynamicCollection(type);
			collections.put(type, collection);
		}
		return collection;
	}

	public ObservableMap<String, DynamicCollection> getCollections() {
		return collections;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRoot() {
		return true;
	}
	
	public DynamicModelObject getObject(String address) {
		return DynamicModelUtils.getObjectByAdress(address, this);
	}
}
