package de.hs.settlers.model.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import de.hs.settlers.model.Game;
import de.hs.settlers.util.AssociationUtils;

/**
 * Hexagonal map implementation following instructions on http://www.redblobgames.com/grids/hexagons/
 * <br>
 * Using flat tops, x as columns, y as ascending rows, z as descending rows.
 *
 */
public class Map {

	private SimpleStringProperty mapTitle = new SimpleStringProperty();
	/**
	 * Contains the downloaded map data
	 */
	private SimpleStringProperty mapData = new SimpleStringProperty();
	private SimpleObjectProperty<MapManager> mapManager = new SimpleObjectProperty<>();
	private SimpleStringProperty creator = new SimpleStringProperty();
	private SimpleIntegerProperty version = new SimpleIntegerProperty(0);
	private ObservableSet<MapField> fields = FXCollections.observableSet(new LinkedHashSet<MapField>());
	private ObservableSet<Game> games = FXCollections.observableSet(new LinkedHashSet<Game>());
	private SimpleIntegerProperty minR = new SimpleIntegerProperty(Integer.MAX_VALUE);
	private SimpleIntegerProperty minQ = new SimpleIntegerProperty(Integer.MAX_VALUE);

	{
		AssociationUtils.manyInMe(this, games, "map", Game.class);
	}

	ObservableSet<Game> getGames() {
		return games;
	}

	public int getVersion() {
		return version.get();
	}

	public void setVersion(int version) {
		this.version.set(version);
	}

	public SimpleIntegerProperty versionProperty() {
		return version;
	}

	public String getMapTitle() {
		return this.mapTitle.get();
	}

	public String getMapData() {
		return this.mapData.get();
	}

	public void setMapTitle(String value) {
		this.mapTitle.set(value);
	}

	public void setMapData(String value) {
		this.mapData.set(value);
	}

	public StringProperty mapTitleProperty() {
		return mapTitle;
	}

	public StringProperty mapDataProperty() {
		return mapData;
	}

	public MapManager getMapManager() {
		return mapManager.get();
	}

	public void setMapManager(MapManager mapManager) {
		this.mapManager.set(mapManager);
	}

	public SimpleObjectProperty<MapManager> mapManagerProperty() {
		return this.mapManager;
	}

	public String getCreator() {
		return creator.get();
	}

	public void setCreator(String creator) {
		this.creator.set(creator);
	}

	public SimpleStringProperty creatorProperty() {
		return creator;
	}

	public ObservableSet<MapField> getFields() {
		return fields;
	}

	{
		AssociationUtils.iInOne(this, mapManager, "maps", MapManager.class);
		AssociationUtils.manyInMe(this, fields, "map", MapField.class);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Map [name=").append(getMapTitle()).append(", version=").append(getVersion()).append(", creator=").append(getCreator()).append("]");
		return builder.toString();
	}

	public static Pattern MAP_DATA_CSV_PATTERN = Pattern.compile("(\\d*);([A-Z_0-9]*);(\\d*);(.*);");
	public static Pattern MAP_DATA_KV_PATTERN = Pattern.compile("([A-Z]+): (.*)");

	/**
	 * Parses the map data in the mapData property
	 */
	public void parseMap() {
		String lines[] = getMapData().split("\n");
		HashMap<Integer, String[]> connections = new HashMap<>();
		for (String line : lines) {
			Matcher m = MAP_DATA_CSV_PATTERN.matcher(line);
			if (m.matches()) {
				MapField mapField = new MapField();
				mapField.setId(Integer.parseInt(m.group(1)));
				int rotation = Integer.parseInt(m.group(3));
				while (rotation >= 360) {
					rotation -= 360;
				}
				mapField.setRotation(rotation);
				mapField.setType(MapTypes.byName(m.group(2)));
				getFields().add(mapField);
				connections.put(mapField.getId(), m.group(4).split(";"));
			} else {
				m = MAP_DATA_KV_PATTERN.matcher(line);
				if (m.matches()) {
					if (m.group(1).equals("CREATOR")) {
						setCreator(m.group(2));
					} else if (m.group(1).equals("VERSION")) {
						setVersion(Integer.parseInt(m.group(2)));
					}
				}
			}
		}
		Set<MapField> fieldsWithCoords = new HashSet<>();
		for (Entry<Integer, String[]> connection : connections.entrySet()) {
			MapField mapField = getField(connection.getKey());
			boolean coordsInit = false;
			if (fieldsWithCoords.isEmpty()) {
				mapField.setX(0);
				mapField.setZ(0);
				coordsInit = true;
			}
			for (String val : connection.getValue()) {
				String co[] = val.split(",");
				Direction direction = Direction.byCode(co[0]);
				int id = Integer.parseInt(co[1]);
				MapField other = getField(id);
				mapField.getConnections().put(direction, other);
				if (!coordsInit && fieldsWithCoords.contains(other)) {
					int r = other.getZ();
					int q = other.getX();
					
					switch (direction) {
					case NORTH:
						r -= 1;
						break;
					case NORTH_EAST:
						r -= 1;
						q += 1;
						break;
					case SOUTH_EAST:
						q += 1;
						break;
					case SOUTH:
						r += 1;
						break;
					case SOUTH_WEST:
						q -= 1;
						r += 1;
						break;
					case NORTH_WEST:
						q -= 1;
						break;
					default:
						throw new IllegalStateException("can't use direction " + direction + " in flat top hex maps");
					}
					
					if (q < minQ.get()) {
						minQ.set(q);
					}
					
					if (r < minR.get()) {
						minR.set(r);
					}
					
					mapField.setX(q);
					mapField.setZ(r);
					coordsInit = true;
				}
			}
			fieldsWithCoords.add(mapField);
		}
		Direction edgeToField[] = {
				Direction.NORTH, Direction.NORTH_WEST, Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST, Direction.NORTH_EAST
		};
		Direction cornerToField[] = {
				Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST
		};
		for (MapField field : fields) {
			// create edges
			for (Direction dir : edgeToField) {
				if (!field.getEdges().containsKey(dir)) {
					FieldEdge edge = new FieldEdge();
					field.getEdges().put(dir, edge);
					if (field.getConnections().containsKey(dir)) {
						MapField other = field.getConnections().get(dir);
						Direction otherDir = dir.getOpposite();
						if (!other.getEdges().containsKey(otherDir)) {
							other.getEdges().put(otherDir, edge);
						}
					}
				}
			}
			// create corners
		}
	}

	public MapField getField(int id) {
		for (MapField field : getFields()) {
			if (field.getId() == id) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * @param x the column
	 * @param z the decreasing row
	 * @return the field at the given coordinates
	 */
	public MapField getFieldXZ(int x, int z) {
		for (MapField field : getFields()) {
			if (field.getX() == x && field.getZ() == z) {
				return field;
			}
		}
		return null;
	}
	
	public int getMinQ() {
		return minQ.get();
	}
	
	public int getMinR() {
		return minR.get();
	}
}