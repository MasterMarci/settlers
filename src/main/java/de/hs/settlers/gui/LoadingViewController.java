package de.hs.settlers.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class LoadingViewController extends ViewController {

	@FXML
	HBox root;

	@FXML
	Label statusLabel;

	@Override
	public Node getRootNode() {
		return root;
	}

	@Override
	public void displayError(String error) {
		statusLabel.getStyleClass().add("error");
		statusLabel.setText(error);
	}

	@Override
	public void onHide() {
		statusLabel.getStyleClass().remove("error");
	}
}
