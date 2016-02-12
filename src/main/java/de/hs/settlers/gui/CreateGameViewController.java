package de.hs.settlers.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.net.event.NetworkErrorListener;
import de.hs.settlers.net.event.NetworkEventListener;
import de.hs.settlers.net.message.text.CreateGameMessage;
import de.hs.settlers.net.message.text.JoinGameMessage;
import de.hs.settlers.util.PropertyUtils;

public class CreateGameViewController extends ViewController implements EventHandler<KeyEvent> {

	@FXML
	VBox root;

	@FXML
	ComboBox<Map> map;
	
	@FXML
	ComboBox<String> cbMaxPlayers;
	
	@FXML
	ComboBox<String> cbAiPlayers;

	@FXML
	CheckBox testgameSelect;
	
	@FXML
	Label errorLabel;

	@FXML
	TextField gameName;

	@FXML
	Button cancelButton;

	@FXML
	Button createButton;

	// flags for keylistener
	boolean altKeyPressed;
	boolean aKeyPressed;
	boolean cKeyPressed;
	
	static int MAXPLAYER= 6;
	
	@Override
	public Node getRootNode() {
		return root;
	}

	@Override
	public void displayError(String error) {
		SettlersApplication.LOGGER.severe(error);
		errorLabel.setVisible(true);
		errorLabel.setText(error);
	}

	public void onCreateAction() {
		int maxPlayers = cbMaxPlayers.getSelectionModel().getSelectedIndex() + 2;
		int maxAiPlayers = cbAiPlayers.getSelectionModel().getSelectedIndex();
		if (maxAiPlayers == -1) {
			maxAiPlayers = 0;
		}
		
		boolean testgame = testgameSelect.isSelected();
		Map map = this.map.getValue();
		String name = gameName.getText().trim();
		if (name.isEmpty()) {
			displayError("No name given");
			return;
		}
		getApplication().getTextCommunicationProtocol().sendMessage(new CreateGameMessage(name, maxPlayers, map, testgame));
	}

	public void onCancelAction() {
		root.getScene().getWindow().hide();
	}

	@Override
	public void init() {
		errorLabel.managedProperty().bind(errorLabel.visibleProperty());
		errorLabel.setVisible(false);

		root.setId("gameCreateRoot");

		PropertyUtils.link(getApplication().getMapManager().getMaps(), map.getItems());
		map.setConverter(new StringConverter<Map>() {

			@Override
			public String toString(Map map) {
				if (map == null) {
					return "(select map)";
				}
				return map.getMapTitle();
			}

			@Override
			public Map fromString(String mapName) {
				return getApplication().getMapManager().getMapWithName(mapName);
			}
		});
		map.setCellFactory(new Callback<ListView<Map>, ListCell<Map>>() {

			@Override
			public ListCell<Map> call(ListView<Map> arg0) {
				return new ListCell<Map>() {
					@Override
					protected void updateItem(Map item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getMapTitle());
						}
					}
				};
			}
		});

		altKeyPressed = false;
		aKeyPressed = false;
		cKeyPressed = false;

		getRootNode().setOnKeyPressed(this);
		getRootNode().setOnKeyReleased(this);
		addUnderlineToButton(cancelButton, 2);
		addUnderlineToButton(createButton, 1);
	}

	private NetworkErrorListener<CreateGameMessage> createGameErrors = new NetworkErrorListener<CreateGameMessage>(CreateGameMessage.class) {

		@Override
		public void onError(CreateGameMessage message, final String error) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					displayError(error);
				}
			});
		}

	};

	private NetworkEventListener<CreateGameMessage> createGameSuccess = new NetworkEventListener<CreateGameMessage>(CreateGameMessage.class) {

		@Override
		public void onMessage(CreateGameMessage message) {
			onCancelAction();

			/*
			 * TODO this is rather unchecked, what if an error occurs when you
			 * join the game? Also, since we don't have the JSON protocol
			 * implemented yet, this will most likely result in a ton of errors
			 */
			getApplication().getTextCommunicationProtocol().sendMessage(new JoinGameMessage(((CreateGameMessage) message.getAnswerTo()).getGameName(), false));
		}
	};

	@Override
	public void onShow() {
		getApplication().getTextCommunicationProtocol().getEventManager().registerListener(createGameErrors);
		getApplication().getTextCommunicationProtocol().getEventManager().registerListener(createGameSuccess);

		// Init some game settings
		gameName.setText(getApplication().getUser().getName() + "'s Game");
		
		cbMaxPlayers.getItems().clear();
		cbAiPlayers.getItems().clear();
		
		for (int i = 2; i < MAXPLAYER + 1; i++ ) {
			cbMaxPlayers.getItems().add(i + " Players");
		}
		cbMaxPlayers.setValue("2 Players");
		cbAiPlayers.getItems().add(0 + " AI Player");
		cbAiPlayers.setValue("0 AI Players");
		if (cbAiPlayers.getItems().size() == 1) {
			cbAiPlayers.getItems().add(1 + " AI Player");
		}
		cbMaxPlayers.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> value,
					String nextItem, String selectedItem) {
				int selectedMaxPlayer = cbMaxPlayers.getSelectionModel().getSelectedIndex() + 2;

				cbAiPlayers.getItems().clear();
				for (int i = 0; i < selectedMaxPlayer; i++) {
					if (i == 1) {
						cbAiPlayers.getItems().add(i + " AI Player");
					} else {
						cbAiPlayers.getItems().add(i + " AI Players");
					}
				}
				cbAiPlayers.setValue("0 AI Players");
			}
		});

		map.setValue(getApplication().getMapManager().getMapWithName("map_balanced"));
	}

	@Override
	public void onHide() {
		getApplication().getTextCommunicationProtocol().getEventManager().unregisterListener(createGameErrors);
		getApplication().getTextCommunicationProtocol().getEventManager().unregisterListener(createGameSuccess);
	}

	/**
	 * Adding a underline to the first letter of an button
	 * 
	 * @param Button
	 *            @ author: ckoch
	 */
	private void addUnderlineToButton(Button btn, int underlinePos) {
		HBox layout = new HBox(-1);

		String btnText = btn.getText();
		btn.setText("");

		if (underlinePos == 1) {
			Label shortcut = new Label(btnText.substring(underlinePos - 1, underlinePos));
			shortcut.setUnderline(true);

			Label newBtnText = new Label(btnText.substring(underlinePos, btnText.length()));

			layout.getChildren().setAll(shortcut, newBtnText);
		} else {
			Label firstPart = new Label(btnText.substring(0, underlinePos - 1));

			Label shortcut = new Label(btnText.substring(underlinePos - 1, underlinePos));
			shortcut.setUnderline(true);

			Label lastPart = new Label(btnText.substring(underlinePos, btnText.length()));

			layout.getChildren().setAll(firstPart, shortcut, lastPart);
		}

		layout.setAlignment(Pos.CENTER);
		btn.setGraphic(layout);
	}

	/**
	 * checking flags for pressed keys to detect hotkeys @ author: ckoch
	 */
	@Override
	public void handle(KeyEvent e) {
		if (e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
			if (e.getCode().equals(KeyCode.ALT)) {
				altKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.A)) {
				aKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.C)) {
				cKeyPressed = true;
			}
		} else if (e.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			altKeyPressed = false;
			aKeyPressed = false;
			cKeyPressed = false;
		}
		if (altKeyPressed && aKeyPressed) {
			onCancelAction();
		} else if (altKeyPressed && cKeyPressed) {
			onCreateAction();
		}
	}
}
