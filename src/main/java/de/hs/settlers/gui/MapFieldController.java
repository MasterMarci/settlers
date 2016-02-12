package de.hs.settlers.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import de.hs.settlers.model.map.MapField;

public class MapFieldController extends ImageView {
	private MapField field;
	private double imageSize;
	private double edgeSize;
	private GameViewController controller;

	public MapFieldController(MapField field, GameViewController controller, double imageSize) {
		this.controller = controller;
		this.field = field;
		this.imageSize = imageSize;
		this.edgeSize = imageSize / Math.sqrt(3);
		setPreserveRatio(true);
		try {
			setImage(new Image(MapFieldController.class.getResourceAsStream("/images/fields/" + field.getType().getImage())));
		} catch (Exception e) {
			System.out.println(field.getType().getImage());
			e.printStackTrace();
		}
		double minX = -toX(field.getMap().getMinQ());
		double minY = -toY(field.getMap().getMinR(), field.getMap().getMinQ());
		
		setFitHeight(imageSize);
		rotateProperty().bind(field.rotationProperty()); // rotation axis is z by default, which is fine
		
		double x = minX + toX(field.getX());
		double y = minY + toY(field.getZ(), field.getX());
		AnchorPane.setLeftAnchor(this, x);
		AnchorPane.setTopAnchor(this, y);
		
		this.translateXProperty().bind(this.controller.panX);
		this.translateYProperty().bind(this.controller.panY);
	}

	public MapField getField() {
		return field;
	}
	
	public double toX(int q) {
		// formula taken from http://www.redblobgames.com/grids/hexagons/.
		return q * edgeSize * 3./2.;
	}
	
	public double toY(int r, int q) {
		// formula taken from http://www.redblobgames.com/grids/hexagons/.
		return edgeSize * Math.sqrt(3.) * (r + q / 2.);
	}
}
