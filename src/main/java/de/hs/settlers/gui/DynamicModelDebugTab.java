package de.hs.settlers.gui;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import de.hs.settlers.model.dynamic.DynamicModel;

public class DynamicModelDebugTab extends Tab {
	DynamicModel model;
	TreeView<String> tree;

	public DynamicModelDebugTab(DynamicModel model) {
		super();
		this.model = model;
		tree = new TreeView<>();
		setContent(tree);
		setText("Dynamic Model");
		tree.setRoot(model);
		tree.setShowRoot(false);
	}
}
