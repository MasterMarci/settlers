package de.hs.settlers.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import de.hs.settlers.ApplicationHolder;

public abstract class ViewController extends ApplicationHolder
{
	public abstract Node getRootNode();

	/**
	 * Init method <br>
	 * 
	 * Called after the view has been linked to the controller.<br> Will only be
	 * called once per instance
	 */
	public void init() {

	}

	/**
	 * Called when the view is shown<br> Can be called multiple times per
	 * instance
	 */
	public void onShow() {

	}

	/**
	 * Called when the view is hidden<br> Can be called multiple times per
	 * instance
	 */
	public void onHide() {

	}

	private SimpleBooleanProperty preloaded = new SimpleBooleanProperty(false);

	/**
	 * @return if this view is preloaded.<br>A preloaded view has only one
	 *         instance over the complete runtime of the application.<br>Handle
	 *         onShow() and onHide() accordingly
	 */
	public boolean isPreloaded() {
		return preloaded.get();
	}

	public void setPreloaded(boolean preloaded) {
		this.preloaded.set(preloaded);
	}

	public SimpleBooleanProperty preloadedProperty() {
		return preloaded;
	}

	public abstract void displayError(String error);

	public final Node wrapRoot() {
		Node root = getRootNode();
		AnchorPane wrap = new AnchorPane();
		wrap.getChildren().add(root);
		AnchorPane.setBottomAnchor(root, 0d);
		AnchorPane.setLeftAnchor(root, 0d);
		AnchorPane.setRightAnchor(root, 0d);
		AnchorPane.setTopAnchor(root, 0d);
		return wrap;
	}
}
