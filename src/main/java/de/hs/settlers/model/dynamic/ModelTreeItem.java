package de.hs.settlers.model.dynamic;

import javafx.scene.control.TreeItem;

public abstract class ModelTreeItem extends TreeItem<String> {
	public abstract boolean isRoot();
}
