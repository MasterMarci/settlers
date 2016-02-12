package de.hs.settlers.gui;

import java.util.ArrayList;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import de.hs.settlers.model.AllLobbyUsers;
import de.hs.settlers.model.Game;
import de.hs.settlers.model.Player;
import de.hs.settlers.model.Recipient;
import de.hs.settlers.model.Team;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.net.event.NetworkEventListener;
import de.hs.settlers.net.event.PollingNetworkEventListener;
import de.hs.settlers.net.message.text.ChatReceiveMessage;
import de.hs.settlers.net.message.text.ChatSendMessage;
import de.hs.settlers.net.message.text.ListGamesMessage;
import de.hs.settlers.net.message.text.ListPlayersMessage;
import de.hs.settlers.net.message.text.LogoutMessage;
import de.hs.settlers.util.PropertyUtils;

public class LobbyViewController extends ViewController implements
		EventHandler<KeyEvent> {

	@FXML
	VBox root;

	@FXML
	FlowPane filterPane;

	@FXML
	Accordion detailAccordion;

	@FXML
	ImageView settlersLogoWide;

	@FXML
	TitledPane playersPane;

	@FXML
	TitledPane gamePane;

	@FXML
	Button filterToggleButton;

	@FXML
	Button joinGameButton;

	@FXML
	Button watchGameButton;

	@FXML
	VBox gameListLayout;

	@FXML
	TableView<Game> gameList;

	@FXML
	Button logoutButton;

	@FXML
	Button createGameButton;

	@FXML
	Button refreshButton;

	@FXML
	TextField filterTextField;

	@FXML
	CheckBox filterFullCheckBox;

	@FXML
	CheckBox filterStartedCheckBox;

	@FXML
	ComboBox<Map> filterMapComboBox;

	@FXML
	ListView<Player> globalPlayerList;

	@FXML
	Button sendButton;

	@FXML
	TextField chatTextField;

	@FXML
	TabPane chatTabs;

	@FXML
	TableColumn<Game, String> mapColumn;

	@FXML
	TableColumn<Game, String> gameTitleColumn;

	@FXML
	TableColumn<Game, String> amountOfPlayersColumn;

	@FXML
	TableColumn<Game, String> statusColumn;

	@FXML
	Rectangle gamePreview;

	@FXML
	TextArea gameInfo;

	// flags for keylistener
	boolean altKeyPressed;
	boolean cKeyPressed;
	boolean fKeyPressed;
	boolean jKeyPressed;
	boolean lKeyPressed;
	boolean rKeyPressed;
	boolean sKeyPressed;
	boolean wKeyPressed;

	// flag for accordion
	boolean playerPaneSelected;

	// flag for filter
	boolean filterOpened;

	private String selectedGame;

	boolean init; // used for join and leave notifications in chat

	private Stage createGameStage = null;
	private Stage mapListStage = null;

	LinkedList<ChatTab> chatTabList = new LinkedList<ChatTab>();

	private ObservableList<Game> masterData = FXCollections
			.observableArrayList();

	public ObservableList<Game> getMasterData() {
		return masterData;
	}

	private ObservableList<Game> filteredData = FXCollections
			.observableArrayList();

	private ListPlayersMessageListener listPlayersMessageListener;

	private ChatReceiveMessageListener chatReceiveMessageListener;

	public ObservableList<Game> getFilteredData() {
		return filteredData;
	}

	@Override
	public Node getRootNode() {
		return root;
	}

	@Override
	public void init() {

		super.init();

		altKeyPressed = false;
		cKeyPressed = false;
		fKeyPressed = false;
		jKeyPressed = false;
		lKeyPressed = false;
		rKeyPressed = false;
		sKeyPressed = false;
		wKeyPressed = false;

		playerPaneSelected = true;

		filterOpened = false;

		selectedGame = "";

		init = true;

		gameList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		chatTabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);

		Image img = new Image("/images/settlersLogoWide.png");
		settlersLogoWide.setImage(img);

		filterPane.managedProperty().bind(filterPane.visibleProperty());
		filterPane.setVisible(false);

		gameList.setPlaceholder(new Text("no games running - just create one"));

		// Add all games to masterdata
		masterData.addAll(getApplication().getGameManager().getGames());

		getApplication().getGameManager().getGames()
				.addListener(new SetChangeListener<Game>() {

					@Override
					public void onChanged(
							javafx.collections.SetChangeListener.Change<? extends Game> change) {
						if (change.wasAdded()) {
							masterData.add(change.getElementAdded());
						}
						if (change.wasRemoved()) {
							masterData.remove(change.getElementRemoved());
						}
					}
				});

		// register GUI-elements to EventListener
		getRootNode().setOnKeyPressed(this);
		getRootNode().setOnKeyReleased(this);

		// unselect game by clicking on the background
		getRootNode().setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				unSelectGame();
			}
		});

		addUnderlineToButton(logoutButton);
		addUnderlineToButton(createGameButton);
		addUnderlineToButton(sendButton);
		addUnderlineToButton(refreshButton);
		addUnderlineToButton(filterToggleButton);
		addUnderlineToButton(joinGameButton);
		addUnderlineToButton(watchGameButton);

		// listen to the GameList
		masterData.addListener(new ListChangeListener<Game>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Game> change) {
				updateFilteredData();
			}
		});

		filteredData.addAll(masterData);

		masterData.addListener(new ListChangeListener<Game>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Game> change) {
				updateFilteredData();
			}
		});
		Map selectMap = new Map();
		selectMap.setMapTitle("(no filter)");
		PropertyUtils.link(getApplication().getMapManager().getMaps(),
				filterMapComboBox.getItems());
		filterMapComboBox.getItems().add(selectMap);
		filterMapComboBox.setConverter(new StringConverter<Map>() {

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
		filterMapComboBox
				.setCellFactory(new Callback<ListView<Map>, ListCell<Map>>() {

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

		// prevending zu collapse the whole accordion
		// either playerPane or gamePane is always open
		detailAccordion.setExpandedPane(playersPane);
		playersPane.setCollapsible(false);
		gamePane.setVisible(false);

		// clicking on playersPane while playersPane is active opens the
		// gamePane
		// otherwise clicking the playerPane while gamePane is active opens
		// playerPane
		playersPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (playerPaneSelected) {
					detailAccordion.setExpandedPane(gamePane);
					playerPaneSelected = false;
				} else {
					detailAccordion.setExpandedPane(playersPane);
					playerPaneSelected = true;
				}
			}
		});

		// clicking on gamePane while gamePane is active opens the playersPane
		// otherwise clicking the playerPane while playersPane is active opens
		// gamePane
		gamePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (playerPaneSelected) {
					detailAccordion.setExpandedPane(gamePane);
					playerPaneSelected = false;
				} else {
					detailAccordion.setExpandedPane(playersPane);
					playerPaneSelected = true;
				}
			}
		});

		gameList.setRowFactory(new Callback<TableView<Game>, TableRow<Game>>() {

			@Override
			public TableRow<Game> call(TableView<Game> tableView) {
				return new TableRow<Game>() {
					{
						setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
							@Override
							public void handle(ContextMenuEvent e) {
								GameContextMenu menu = new GameContextMenu(
										getItem(), getApplication());
								menu.show(LobbyViewController.this
										.getRootNode().getScene().getWindow(),
										e.getScreenX(), e.getScreenY());
							}
						});
						itemProperty().addListener(new ChangeListener<Game>() {
							@Override
							public void changed(
									ObservableValue<? extends Game> obj,
									Game oldValue, Game newValue) {
								if (newValue != null) {
									setOnKeyPressed(new GameJoiner<KeyEvent>(
											newValue, false, getApplication()));
									// setOnMouseClicked(new
									// GameJoiner<MouseEvent>(newValue, false,
									// getApplication()));
								}
							}
						});

						setOnMouseClicked(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								if (!gameList.getSelectionModel()
										.getSelectedItem().getGameTitle()
										.equals("")) {
									for (TitledPane t : detailAccordion
											.getPanes()) {
										if (t.getText().equals("Game")) {
											// disables collaps property to
											// playerpane if nothing is seleted
											Platform.runLater(new Runnable() {
												@Override
												public void run() {
													gamePane.setVisible(true);
													playersPane
															.setCollapsible(true);
													detailAccordion
															.setExpandedPane(gamePane);
													playerPaneSelected = false;
													selectedGame = gameList
															.getSelectionModel()
															.getSelectedItem()
															.getGameTitle();
												}
											});
										}
									}

									gameInfo.clear();
									gameInfo.appendText("Title: "
											+ gameList.getSelectionModel()
													.getSelectedItem()
													.getGameTitle() + "\n");
									gameInfo.appendText("Map: "
											+ gameList.getSelectionModel()
													.getSelectedItem()
													.getMapTitle() + "\n");
									gameInfo.appendText("Players: "
											+ gameList.getSelectionModel()
													.getSelectedItem()
													.getPlayersJoin()
											+ "/"
											+ gameList.getSelectionModel()
													.getSelectedItem()
													.getMaxPlayers() + "\n");
									gameInfo.appendText("KI Players: "
											+ gameList.getSelectionModel()
													.getSelectedItem()
													.getKiPlayers() + "\n");
								}
							}
						});
					}
				};
			}
		});
		
		gameTitleColumn.setCellValueFactory(
				new PropertyValueFactory<Game, String>("gameTitle"));
		mapColumn.setCellValueFactory(
				new PropertyValueFactory<Game, String>("mapTitle"));
		amountOfPlayersColumn.setCellValueFactory(
				new PropertyValueFactory<Game, String>("playerInCanJoin"));
		statusColumn.setCellValueFactory(
				new PropertyValueFactory<Game, String>("status"));

		// Add filtered data to the table
		gameList.setItems(filteredData);
		

		globalPlayerList
				.setCellFactory(new Callback<ListView<Player>, ListCell<Player>>() {

					@Override
					public ListCell<Player> call(ListView<Player> arg0) {
						final ListCell<Player> cell = new ListCell<Player>() {
							@Override
							protected void updateItem(Player player,
									boolean empty) {
								super.updateItem(player, empty);

								if (player != null) {
									setText(player.getName());
								}
							}
						};
						cell.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent event) {
								if (event.getClickCount() > 1) {
									ChatTab tab = null;
									for (ChatTab c : chatTabList) {
										if (c.getTab()
												.getText()
												.equals(cell.getItem()
														.getName())) {
											tab = c;
										}
									}
									if (tab == null
											&& !cell.getItem()
													.getName()
													.equals(getApplication()
															.getUser()
															.getName())) {
										final ChatTab newChatTab = new ChatTab(cell
												.getItem(), cell.getItem()
												.getName(), getApplication());
										chatTabList.add(newChatTab);
										chatTabs.getTabs().add(
												newChatTab.getTab());
										tab = newChatTab;
										
										// removing chattab from linkedlist if it got closed
										newChatTab.getTab().setOnClosed(new EventHandler<Event>() {
											
											@Override
											public void handle(Event e) {
												chatTabList.remove(newChatTab);
												
											}
										});
									}

									if (tab != null) {
										chatTabs.getSelectionModel().select(
												tab.getTab());
										chatTextField.requestFocus();
									}
								}
							}
						});
						return cell;
					}

				});

		chatReceiveMessageListener = new ChatReceiveMessageListener(
				ChatReceiveMessage.class);
	}

	@Override
	public void onShow() {
		if (listPlayersMessageListener == null) {
			listPlayersMessageListener = new ListPlayersMessageListener(
					ListPlayersMessage.class);
		}
		getApplication().getTextCommunicationProtocol().getEventManager()
				.registerListener(listPlayersMessageListener);
		listPlayersMessageListener.start();
		if (getApplication().getGameManager().isListenersRegistered()) {
			getApplication().getGameManager().getListGamesMessageListener()
					.start();
		} else {
			getApplication().getGameManager().updateGames();
			getApplication().getGameManager().getListGamesMessageListener()
					.start();
		}

		// creating the "All" ChatTab
		ChatTab newChatTab = new ChatTab(new AllLobbyUsers(), "All",
				getApplication());
		chatTabList.add(newChatTab);
		chatTabs.getTabs().add(newChatTab.getTab());
		newChatTab = new ChatTab(
				new Team(getApplication().getUser().getTeam()), "Team",
				getApplication());
		chatTabList.add(newChatTab);
		chatTabs.getTabs().add(newChatTab.getTab());

		// listening for chat messages and adding them to the right ChatTab
		getApplication().getTextCommunicationProtocol().getEventManager()
				.registerListener(chatReceiveMessageListener);
	}

	@Override
	public void onHide() {
		getApplication().getTextCommunicationProtocol().getEventManager()
				.unregisterListener(chatReceiveMessageListener);
		listPlayersMessageListener.stop();
		getApplication().getGameManager().getListGamesMessageListener().stop();
		getApplication().getTextCommunicationProtocol().getEventManager()
				.unregisterListener(listPlayersMessageListener);
		chatTabList.clear();
		chatTabs.getTabs().clear();
	}

	public void onFilterToggleAction() {
		filterPane.setVisible(!filterPane.isVisible());
		if (filterPane.isVisible()) {
			filterOpened = true;
		} else {
			filterOpened = false;
		}
	}

	public void filterOnKeyReleased() {
		updateFilteredData();

		if (filteredData.size() == 0) {
			unSelectGame();
		} else {
			for (Game g : filteredData) {
				if (!selectedGame.equals(g.getGameTitle())) {
					unSelectGame();
				}
			}
		}
	}

	public void filterFullGamesAction() {
		updateFilteredData();
	}

	public void filterStartedGamesAction() {
		updateFilteredData();
	}

	public void filterMapAction() {
		updateFilteredData();
	}

	private void updateFilteredData() {
		filteredData.clear();

		for (Game g : masterData) {
			if (matchesFilter(g)) {
				filteredData.add(g);
			}
		}

		// Must re-sort table after items changed
		reapplyTableSortOrder();
	}

	private void reapplyTableSortOrder() {
		ArrayList<TableColumn<Game, ?>> sortOrder = new ArrayList<>(
				gameList.getSortOrder());
		gameList.getSortOrder().clear();
		gameList.getSortOrder().addAll(sortOrder);

	}

	public void refreshGamesAction() {
		getApplication().getTextCommunicationProtocol().sendMessage(
				new ListGamesMessage());
	}

	private boolean matchesFilter(Game g) {

		boolean matchesFilter = false;

		if (filterTextField.getText().length() > 0) {

			if (g.getGameTitle().toLowerCase()
					.startsWith(filterTextField.getText().toLowerCase())) {
				matchesFilter = true;
			} else {
				return false;
			}
		}

		if (filterFullCheckBox.isSelected()) {
			if (g.getPlayersJoin() + g.getKiPlayers() < g.getMaxPlayers()) {
				matchesFilter = true;
			} else {
				return false;
			}
		} else {
			matchesFilter = true;
		}

		if (filterStartedCheckBox.isSelected()) {
			if (g.getStatus().equals("JOINING")) {
				matchesFilter = true;
			} else {
				return false;
			}
		} else {
			matchesFilter = true;
		}

		if (filterMapComboBox.getSelectionModel().getSelectedItem() != null
				&& filterMapComboBox.getSelectionModel().getSelectedItem()
						.getMapTitle().length() > 0
				&& !filterMapComboBox.getSelectionModel().getSelectedItem()
						.getMapTitle().equals("(no filter)")) {
			if (filterMapComboBox.getSelectionModel().getSelectedItem()
					.getMapTitle().equals(g.getMap().getMapTitle())) {
				matchesFilter = true;
			} else {
				return false;
			}
		} else {
			matchesFilter = true;
		}

		return matchesFilter;

	}

	@FXML
	public void onLogoutAction() {
		if (filterOpened) {
			onFilterToggleAction();
		}
		getApplication().getTextCommunicationProtocol().sendMessage(
				new LogoutMessage());
		getApplication().getTextCommunicationProtocol().setActive(false);
		getApplication().setCurrentState(
				getApplication().getStates().NOT_CONNECTED);
	}

	@FXML
	public void onSendAction() {
		String message = chatTextField.getText();
		message = message.trim();

		if (!message.isEmpty()) {
			Recipient recipient = null;
			for (ChatTab tab : chatTabList) {
				if (tab.getTab().isSelected()) {
					recipient = tab.getRecipient();
				}
			}
			getApplication().getTextCommunicationProtocol().sendMessage(
					new ChatSendMessage(recipient, message));

		}
		chatTextField.setText("");
	}

	@FXML
	public void onJoinAction() {
		if (filterOpened) {
			onFilterToggleAction();
		}
		// TODO: Join Game
	}

	@FXML
	public void onWatchAction() {
		if (filterOpened) {
			onFilterToggleAction();
		}
		// TODO: Watch Game
	}
	
	public void onMapManagerAction() {
		if (filterOpened) {
			onFilterToggleAction();
		}
		if (mapListStage != null) {
			mapListStage.toFront();
			return;
		}
		final MapListViewController controller = (MapListViewController) getApplication().loadView("maplistview.fxml");
		mapListStage = new Stage();
		mapListStage.setScene(new Scene((Parent) controller.wrapRoot()));
		mapListStage.setTitle("Map Manager");
		mapListStage.centerOnScreen();
		mapListStage.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> obj, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					mapListStage = null;
					controller.onHide();
				}
			}
		});

		mapListStage.show();
		controller.onShow();

	}

	@Override
	public void displayError(String error) {
		// TODO Auto-generated method stub
	}

	@FXML
	public void onCreateGameAction() {
		if (filterOpened) {
			onFilterToggleAction();
		}
		if (createGameStage != null) {
			createGameStage.toFront();
			return;
		}
		final CreateGameViewController controller = (CreateGameViewController) getApplication()
				.loadView("creategameview.fxml");
		createGameStage = new Stage();
		createGameStage.setScene(new Scene((Parent) controller.wrapRoot()));
		createGameStage.setTitle("Create game");
		createGameStage.initStyle(StageStyle.UNDECORATED);
		createGameStage.centerOnScreen();
		createGameStage.showingProperty().addListener(
				new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> obj,
							Boolean oldValue, Boolean newValue) {
						if (!newValue) {
							createGameStage = null;
							controller.onHide();
						}
					}
				});

		createGameStage.show();
		controller.onShow();

	}
			

	private final class ListPlayersMessageListener extends
			PollingNetworkEventListener<ListPlayersMessage> {
		private ListPlayersMessageListener(Class<ListPlayersMessage> type) {
			super(type, ListPlayersMessage.class, 1000,
					LobbyViewController.this.getApplication());
		}

		@Override
		public void onPollMessage(ListPlayersMessage message) {
			ObservableList<Player> data = FXCollections.observableArrayList();
			for (Player p : message.getPlayers()) {
				p.setOnline(true);

				boolean contains = false;

				for (Player tmp : getApplication().getPlayers()) {
					if (tmp.getName().equals(p.getName())) {
						if (!tmp.isOnline()) {
							tmp.setOnline(true);
							for (ChatTab t : chatTabList) {
								if (t.getTab().getText()
										.equalsIgnoreCase("All")) {
									t.getHistory().appendText(
											"User " + tmp.getName()
													+ " joined the Lobby.\n");
								}
								if (t.getTab().getText().equals(tmp.getName())) {
									t.getHistory().appendText(
											"User " + tmp.getName()
													+ " joined the Lobby.\n");
								}
							}
						}

						contains = true;
					}

					boolean pInSettlers = false;
					for (Player tmp2 : message.getPlayers()) {
						if (tmp2.getName().equals(tmp.getName())) {
							pInSettlers = true;
						}
					}

					if (!pInSettlers && tmp.isOnline()) {
						tmp.setOnline(false);
						for (ChatTab t : chatTabList) {
							if (t.getTab().getText().equalsIgnoreCase("All")) {
								t.getHistory().appendText(
										"User " + tmp.getName()
												+ " left the Lobby.\n");
							}
							if (t.getTab().getText().equals(tmp.getName())) {
								t.getHistory().appendText(
										"User " + tmp.getName()
												+ " joined the Lobby.\n");
							}
						}
					}
				}

				if (!contains) {
					getApplication().getPlayers().add(p);
					for (ChatTab t : chatTabList) {
						if (t.getTab().getText().equalsIgnoreCase("All")
								&& !init) {
							t.getHistory().appendText(
									"User " + p.getName()
											+ " joined the Lobby.\n");
						}
					}
				}

				data.add(p);
			}

			globalPlayerList.setItems(data);
			init = false;
		}
	}

	private final class ChatReceiveMessageListener extends
			NetworkEventListener<ChatReceiveMessage> {
		private ChatReceiveMessageListener(Class<ChatReceiveMessage> type) {
			super(type);
		}

		@Override
		public void onMessage(ChatReceiveMessage message) {
			Boolean consumed = false;
			for (ChatTab i : chatTabList) {
				if (i.getTab().getText().equalsIgnoreCase("All")
						&& message.getRecipient() == ChatReceiveMessage.ReceiveType.ALL) {
					i.getHistory().appendText(
							"<" + message.getSender() + "> "
									+ message.getMessage() + "\n");
					i.incrementUnread();
					consumed = true;
				} else if (i.getTab().getText()
						.equalsIgnoreCase(message.getSender())
						&& message.getRecipient() == ChatReceiveMessage.ReceiveType.YOU) {
					i.getHistory().appendText(
							"<" + message.getSender() + "> "
									+ message.getMessage() + "\n");
					i.incrementUnread();
					consumed = true;
				} else if (message.getRecipient().equals(i.getTab().getText())
						&& message.getSender().equals("ME")) {
					i.getHistory().appendText(
							"<" + getApplication().getUser().getName() + "> "
									+ message.getMessage() + "\n");
					i.incrementUnread();
					consumed = true;
				} else if (i.getTab().getText().equalsIgnoreCase("Team")
						&& message.getRecipient() == ChatReceiveMessage.ReceiveType.TEAM) {
					i.getHistory().appendText(
							"<" + message.getSender() + "> "
									+ message.getMessage() + "\n");
					i.incrementUnread();
					consumed = true;
				}
			}

			if (!consumed) {
				Player recipient = null;
				for (Player p : getApplication().getPlayers()) {
					if (p.getName().equals(message.getSender())) {
						recipient = p;
					}
				}
				final ChatTab newChatTab = new ChatTab(recipient,
						message.getSender(), getApplication());
				chatTabList.add(newChatTab);
				chatTabs.getTabs().add(newChatTab.getTab());
				newChatTab.getHistory().appendText(
						"<" + message.getSender() + "> " + message.getMessage()
								+ "\n");
				newChatTab.incrementUnread();
				newChatTab.getTab().setOnClosed(new EventHandler<Event>() {
					
					@Override
					public void handle(Event e) {
						chatTabList.remove(newChatTab);
					}
				});
			}
		}
	}

	/**
	 * Adding a underline to the first letter of an button
	 * 
	 * @param Button
	 *            @ author: ckoch
	 */
	private void addUnderlineToButton(Button btn) {
		HBox layout = new HBox(-1);

		String btnText = btn.getText();
		btn.setText("");

		Label shortcut = new Label(btnText.substring(0, 1));
		shortcut.setUnderline(true);

		Label newBtnText = new Label(btnText.substring(1, btnText.length()));

		layout.getChildren().setAll(shortcut, newBtnText);

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
			} else if (e.getCode().equals(KeyCode.C)) {
				cKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.F)) {
				fKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.J)) {
				jKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.L)) {
				lKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.R)) {
				rKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.S)) {
				sKeyPressed = true;
			} else if (e.getCode().equals(KeyCode.W)) {
				wKeyPressed = true;
			}
		} else if (e.getEventType().equals(KeyEvent.KEY_RELEASED)) {
			if (e.getCode().equals(KeyCode.ALT)) {
				altKeyPressed = false;
			}
			cKeyPressed = false;
			fKeyPressed = false;
			jKeyPressed = false;
			lKeyPressed = false;
			rKeyPressed = false;
			sKeyPressed = false;
			wKeyPressed = false;
		}

		if (e.getCode().equals(KeyCode.ENTER)) {
			onSendAction();
		} else if (e.getCode().equals(KeyCode.ESCAPE)) {
			unSelectGame();
		} else if (altKeyPressed && cKeyPressed) {
			altKeyPressed = false;
			cKeyPressed = false;
			onCreateGameAction();
		} else if (altKeyPressed && fKeyPressed) {
			onFilterToggleAction();
		} else if (altKeyPressed && jKeyPressed && !playerPaneSelected) {
			onJoinAction();
		} else if (altKeyPressed && lKeyPressed) {
			onLogoutAction();
		} else if (altKeyPressed && rKeyPressed) {
			refreshGamesAction();
		} else if (altKeyPressed && sKeyPressed) {
			onSendAction();
		} else if (altKeyPressed && wKeyPressed && !playerPaneSelected) {
			onWatchAction();
		}
	}

	/**
	 * Deselects the game in the gamelist and closes the game-tab
	 */
	private void unSelectGame() {
		gameList.getSelectionModel().clearSelection();
		// disables collapse property to playerpane if nothing is selected
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				playerPaneSelected = true;
				detailAccordion.setExpandedPane(playersPane);
				gamePane.setVisible(false);
				playersPane.setCollapsible(false);
				selectedGame = "";
			}
		});
	}
}
