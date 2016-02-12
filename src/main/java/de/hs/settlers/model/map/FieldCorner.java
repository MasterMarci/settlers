package de.hs.settlers.model.map;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class FieldCorner extends MapContentHolder<MapContent<FieldCorner>> {
	private ObservableMap<Direction, MapField> fields = FXCollections.observableHashMap();
	private ObservableMap<Direction, FieldEdge> edges = FXCollections.observableHashMap();
	
	{
		// referential integrity
		
		fields.addListener(new MapChangeListener<Direction, MapField> () {
			@Override
			public void onChanged(MapChangeListener.Change<? extends Direction, ? extends MapField> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					MapField field = change.getValueAdded();
					field.getCorners().put(dir.getOpposite(), FieldCorner.this);
				} 
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					MapField field = change.getValueRemoved();
					field.getCorners().remove(dir.getOpposite());
				}
			}
		});
		
		edges.addListener(new MapChangeListener<Direction, FieldEdge>() {
			@Override
			public void onChanged(MapChangeListener.Change<? extends Direction, ? extends FieldEdge> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					FieldEdge edge = change.getValueAdded();
					edge.getCorners().put(dir.getOpposite(), FieldCorner.this);
				}
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					FieldEdge edge = change.getValueRemoved();
					edge.getCorners().remove(dir.getOpposite());
				}
			}
		});
	}
	
	public ObservableMap<Direction, MapField> getFields() {
		return fields;
	}
	
	public ObservableMap<Direction, FieldEdge> getEdges() {
		return edges;
	}
}
