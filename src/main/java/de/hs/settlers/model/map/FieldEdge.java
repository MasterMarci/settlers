package de.hs.settlers.model.map;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class FieldEdge extends MapContentHolder<MapContent<FieldEdge>> {
	private ObservableMap<Direction, MapField> fields = FXCollections.observableHashMap();
	private ObservableMap<Direction, FieldCorner> corners = FXCollections.observableHashMap();
	
	{
		// referential integrity
		fields.addListener(new MapChangeListener<Direction, MapField> () {
			@Override
			public void onChanged(MapChangeListener.Change<? extends Direction, ? extends MapField> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					MapField field = change.getValueAdded();
					field.getEdges().put(dir.getOpposite(), FieldEdge.this);
				} 
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					MapField field = change.getValueRemoved();
					field.getEdges().remove(dir.getOpposite());
				}
			}
		});
		
		corners.addListener(new MapChangeListener<Direction, FieldCorner> () {
			@Override
			public void onChanged(MapChangeListener.Change<? extends Direction, ? extends FieldCorner> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					FieldCorner corner = change.getValueAdded();
					corner.getEdges().put(dir.getOpposite(), FieldEdge.this);
				}
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					FieldCorner corner = change.getValueRemoved();
					corner.getEdges().remove(dir.getOpposite());
				}
			}
		});
	}
	
	public ObservableMap<Direction, MapField> getFields() {
		return fields;
	}
	
	public ObservableMap<Direction, FieldCorner> getCorners() {
		return corners;
	}
}
