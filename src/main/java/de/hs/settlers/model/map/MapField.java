package de.hs.settlers.model.map;

import java.util.HashMap;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import de.hs.settlers.util.AssociationUtils;

public class MapField extends MapContentHolder<MapContent<MapField>> {
	SimpleObjectProperty<MapType> type = new SimpleObjectProperty<>();
	SimpleIntegerProperty id = new SimpleIntegerProperty();
	SimpleIntegerProperty rotation = new SimpleIntegerProperty(0);
	ObservableMap<Direction, MapField> connections = FXCollections
			.observableMap(new HashMap<Direction, MapField>(2));
	SimpleObjectProperty<Map> map = new SimpleObjectProperty<>();
	SimpleIntegerProperty x = new SimpleIntegerProperty(0);
	SimpleIntegerProperty y = new SimpleIntegerProperty(0);
	SimpleIntegerProperty z = new SimpleIntegerProperty(0);
	ObservableMap<Direction, FieldEdge> edges = FXCollections
			.observableHashMap();
	ObservableMap<Direction, FieldCorner> corners = FXCollections
			.observableHashMap();

	{
		// init x, y and z relationships
		// x + y + z = 0
		// NumberBinding xYToZ = x.add(y).negate();
		// z.bind(xYToZ);

		NumberBinding xZToY = x.add(z).negate();
		y.bind(xZToY);

		// NumberBinding yzToX = y.add(z).negate();
		// x.bind(yzToX);

		// Referential integrity
		AssociationUtils.iInOne(this, map, "fields", Map.class);
		connections.addListener(new MapChangeListener<Direction, MapField>() {
			@Override
			public void onChanged(
					MapChangeListener.Change<? extends Direction, ? extends MapField> change) {
				if (change.wasAdded()) {
					Direction direction = change.getKey();
					MapField other = change.getValueAdded();
					Direction opposite = direction.getOpposite();
					other.getConnections().put(opposite, MapField.this);
				}
				if (change.wasRemoved()) {
					Direction direction = change.getKey();
					MapField other = change.getValueRemoved();
					Direction opposite = direction.getOpposite();
					other.getConnections().remove(opposite);
				}
			}
		});

		edges.addListener(new MapChangeListener<Direction, FieldEdge>() {
			@Override
			public void onChanged(
					MapChangeListener.Change<? extends Direction, ? extends FieldEdge> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					FieldEdge edge = change.getValueAdded();
					edge.getFields().put(dir.getOpposite(), MapField.this);
				}
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					FieldEdge edge = change.getValueRemoved();
					edge.getFields().remove(dir.getOpposite());
				}
			}
		});

		corners.addListener(new MapChangeListener<Direction, FieldCorner>() {
			@Override
			public void onChanged(MapChangeListener.Change<? extends Direction, ? extends FieldCorner> change) {
				if (change.wasAdded()) {
					Direction dir = change.getKey();
					FieldCorner corner = change.getValueAdded();
					corner.getFields().put(dir.getOpposite(), MapField.this);
				}
				if (change.wasRemoved()) {
					Direction dir = change.getKey();
					FieldCorner corner = change.getValueRemoved();
					corner.getFields().remove(dir.getOpposite());
				}
			}
		});
	}

	public Map getMap() {
		return map.get();
	}

	public void setMap(Map map) {
		this.map.set(map);
	}

	public ObjectProperty<Map> mapProperty() {
		return map;
	}

	public MapType getType() {
		return type.get();
	}

	public void setType(MapType type) {
		this.type.set(type);
	}

	public ObservableObjectValue<MapType> typeProperty() {
		return type;
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public int getRotation() {
		return rotation.get();
	}

	public void setRotation(int rotation) {
		this.rotation.set(rotation);
	}

	public IntegerProperty rotationProperty() {
		return rotation;
	}

	public ObservableMap<Direction, MapField> getConnections() {
		return connections;
	}

	public int getX() {
		return x.get();
	}

	public int getY() {
		return y.get();
	}

	public int getZ() {
		return z.get();
	}

	public void setX(int x) {
		this.x.set(x);
	}

	public void setY(int y) {
		this.y.set(y);
	}

	public void setZ(int z) {
		this.z.set(z);
	}

	public IntegerProperty xProperty() {
		return x;
	}

	public IntegerProperty yProperty() {
		return y;
	}

	public IntegerProperty zProperty() {
		return z;
	}

	public ObservableMap<Direction, FieldEdge> getEdges() {
		return edges;
	}

	public ObservableMap<Direction, FieldCorner> getCorners() {
		return corners;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MapField[type=");
		sb.append(getType().getName());
		sb.append(", id=").append(getId());
		sb.append(", rotation=").append(getRotation());
		sb.append(", connections=").append(getConnections().size())
				.append(" items");
		sb.append("]");
		return sb.toString();
	}
}
