package de.hs.settlers.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import org.junit.Assert;
import org.junit.Test;

import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.Game;
import de.hs.settlers.model.GameManager;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.model.map.MapManager;
import de.hs.settlers.model.Player;

public class LobbyFeatureTest {

	@Test
	public void testGameList() {
		SettlersApplication settApp;
		Map map1 = new Map();
		map1.setMapTitle("A_Map");
		Map map2 = new Map();
		map2.setMapTitle("Other_Map");

		settApp = new SettlersApplication();
		GameManager gameManager = new GameManager();

		MapManager mapManager = new MapManager();
		mapManager.getMaps().add(map1);
		mapManager.getMaps().add(map2);
		
		settApp.setMapManager(mapManager);
		settApp.setGameManager(gameManager);
		LobbyViewController controller = new LobbyViewController();
		settApp.setViewController(controller);
		controller.setApplication(settApp);
		mapManager.setApplication(settApp);

		// Initializing all GUI elements of the lobby
		controller.root = new VBox();
		controller.filterPane = new FlowPane();
		controller.detailAccordion = new Accordion();
		controller.settlersLogoWide = new ImageView();
		controller.playersPane = new TitledPane();
		controller.gamePane = new TitledPane();
		controller.filterToggleButton = new Button();
		controller.gameListLayout = new VBox();
		controller.gameList = new TableView<Game>();
		controller.logoutButton = new Button();
		controller.filterTextField = new TextField();
		controller.filterFullCheckBox = new CheckBox();
		controller.filterStartedCheckBox = new CheckBox();
		controller.filterMapComboBox = new ComboBox<Map>();
		controller.globalPlayerList = new ListView<Player>();
		controller.sendButton = new Button();
		controller.chatTextField = new TextField();
		controller.chatTabs = new TabPane();
		controller.mapColumn = new TableColumn<Game, String>();
		controller.gameTitleColumn = new TableColumn<Game, String>();
		controller.amountOfPlayersColumn = new TableColumn<Game, String>();
		controller.gamePreview = new Rectangle();
		controller.statusColumn = new TableColumn<>();
		controller.logoutButton = new Button();
		controller.filterToggleButton = new Button();
		controller.refreshButton = new Button();
		controller.sendButton = new Button();
		controller.createGameButton = new Button();
		controller.joinGameButton = new Button();
		controller.watchGameButton = new Button();
		controller.logoutButton.setText("Logout");
		controller.filterToggleButton.setText("Toggle");
		controller.refreshButton.setText("Refresh");
		controller.createGameButton.setText("Create Game");
		controller.sendButton.setText("Send");
		controller.joinGameButton.setText("Join Game");
		controller.watchGameButton.setText("Watch Game");
		
		
		Assert.assertEquals(0, controller.getFilteredData().size());
		Assert.assertEquals(0, controller.getMasterData().size());
		
		Game game1 = new Game().withGameTitel("MyGame").withMap(map1)
				.withMaxPlayers(4);
		
		Game game2 = new Game().withGameTitel("MyOtherGame").withMap(map1)
				.withMaxPlayers(4);
		
		
		controller.init();
		gameManager.getGames().add(game1);
		
		Assert.assertEquals(1, controller.getMasterData().size());
		Assert.assertEquals(1, controller.gameList.getItems().size());
		
		gameManager.getGames().add(game2);
		
		Assert.assertTrue(gameManager.getGames().contains(game1) && gameManager.getGames().contains(game2));
		Assert.assertTrue(!gameManager.isListenersRegistered());
		Assert.assertEquals(null, gameManager.getListGamesMessageListener());
		Assert.assertEquals(game2, gameManager.getGameWithName("MyOtherGame"));
		
		
		
		

		// gameManager.getGames()
	}

	@Test
	public void testFilters() {

		testFilterOnKeyTyped();
		testFilterFullGamesAction();
		testFilterStartedGamesAction();
		testNoFilterOnKeyTyped();
		testNoFilterFullGamesAction();
		testNoFilterStartedGamesAction();
		testFilterMapAction();

	}

	public void testFilterMapAction() {
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Map map1 = new Map();
		map1.setMapTitle("map1");
		Map map2 = new Map();
		map2.setMapTitle("map2");
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING")
				.withMap(map1);

		g1.setPlayersJoin(0);
		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("OTHER").withMap(map2);

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING").withMap(map1);

		g3.setPlayersJoin(3);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameTitleColumn = new TableColumn<Game, String>();
		con.mapColumn = new TableColumn<Game, String>();
		con.amountOfPlayersColumn = new TableColumn<Game, String>();

		con.gameList = new TableView<Game>();
		con.gameList.getColumns().add(con.gameTitleColumn);
		con.gameList.getColumns().add(con.amountOfPlayersColumn);
		con.gameList.getColumns().add(con.mapColumn);
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();
		con.filterMapComboBox = new ComboBox<Map>();
		ListCell<String> lC = new ListCell<String>();
		lC.setText("map1");
		ArrayList<Map> mapList = new ArrayList<Map>();
		mapList.add(map1);
		mapList.add(map2);
		con.filterMapComboBox.setItems(FXCollections
				.observableArrayList(mapList));
		con.filterMapComboBox.getSelectionModel().select(map1);

		con.root = new VBox();
		con.filterPane = new FlowPane();
		con.detailAccordion = new Accordion();
		con.getMasterData().addAll(main.getGameManager().getGames());

		con.filterTextField.setText("");

		con.filterMapAction();

		Assert.assertEquals(2, con.getFilteredData().size());
		Assert.assertEquals(true, con.getFilteredData().contains(g1));
		Assert.assertEquals(true, con.getFilteredData().contains(g3));

	}

	public void testNoFilterStartedGamesAction() {

		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		g1.setPlayersJoin(0);

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("OTHER");

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING");

		g3.setPlayersJoin(3);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameList = new TableView<Game>();
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();

		con.filterStartedCheckBox.setSelected(false);

		con.gameList.getItems().add(g1);
		con.gameList.getItems().add(g2);
		con.gameList.getItems().add(g3);

		con.filterStartedGamesAction();
		Assert.assertEquals(3, con.gameList.getItems().size());
	}

	public void testNoFilterFullGamesAction() {
		// initialize variables
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		g1.setPlayersJoin(0);

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("JOINING");

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING");

		g3.setPlayersJoin(3);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameList = new TableView<Game>();
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();

		con.filterFullCheckBox.setSelected(false);

		con.gameList.getItems().add(g1);
		con.gameList.getItems().add(g2);
		con.gameList.getItems().add(g3);

		con.filterFullGamesAction();

		Assert.assertEquals(3, con.gameList.getItems().size());
	}

	public void testNoFilterOnKeyTyped() {
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING");

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameList = new TableView<Game>();
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();

		con.gameList.getItems().add(g1);
		con.gameList.getItems().add(g2);
		con.gameList.getItems().add(g3);
		con.filterTextField.setText("");

		con.filterOnKeyReleased();

		Assert.assertEquals(3, con.gameList.getItems().size());

	}

	public void testFilterStartedGamesAction() {
		// initialize variables
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		g1.setPlayersJoin(0);

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("OTHER");

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING");

		g3.setPlayersJoin(3);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameTitleColumn = new TableColumn<Game, String>();
		con.mapColumn = new TableColumn<Game, String>();
		con.amountOfPlayersColumn = new TableColumn<Game, String>();

		con.gameList = new TableView<Game>();
		con.gameList.getColumns().add(con.gameTitleColumn);
		con.gameList.getColumns().add(con.amountOfPlayersColumn);
		con.gameList.getColumns().add(con.mapColumn);
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();
		con.filterMapComboBox = new ComboBox<Map>();
		ListCell<String> lC = new ListCell<String>();
		lC.setText("");
		con.root = new VBox();
		con.filterPane = new FlowPane();
		con.detailAccordion = new Accordion();

		con.filterTextField.setText("");
		con.filterStartedCheckBox.setSelected(true);
		con.getMasterData().addAll(main.getGameManager().getGames());

		con.filterStartedGamesAction();
		Assert.assertEquals(2, con.getFilteredData().size());
		Assert.assertEquals(true, con.getFilteredData().contains(g1));
		Assert.assertEquals(true, con.getFilteredData().contains(g3));

	}

	public void testFilterFullGamesAction() {

		// initialize variables
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING");

		g1.setPlayersJoin(0);

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("JOINING");

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING");

		g3.setPlayersJoin(3);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameTitleColumn = new TableColumn<Game, String>();
		con.mapColumn = new TableColumn<Game, String>();
		con.amountOfPlayersColumn = new TableColumn<Game, String>();

		con.gameList = new TableView<Game>();
		con.gameList.getColumns().add(con.gameTitleColumn);
		con.gameList.getColumns().add(con.amountOfPlayersColumn);
		con.gameList.getColumns().add(con.mapColumn);
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();
		con.filterMapComboBox = new ComboBox<Map>();
		ListCell<String> lC = new ListCell<String>();
		lC.setText("");
		con.root = new VBox();
		con.filterPane = new FlowPane();
		con.detailAccordion = new Accordion();

		con.filterTextField.setText("");
		con.filterFullCheckBox.setSelected(true);

		con.getMasterData().addAll(main.getGameManager().getGames());

		con.filterFullGamesAction();

		Assert.assertEquals(2, con.getFilteredData().size());
		Assert.assertEquals(true, con.getFilteredData().contains(g1));
		Assert.assertEquals(true, con.getFilteredData().contains(g2));

	}

	public void testFilterOnKeyTyped() {
		SettlersApplication main = new SettlersApplication();
		main.setGameManager(new GameManager());
		Map map1 = new Map();
		map1.setMapTitle("map1");
		Map map2 = new Map();
		map2.setMapTitle("map2");
		Game g1 = new Game().withGameTitel("My Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withKiPlayers(1).withMaxPlayers(3).withStatus("JOINING")
				.withMap(map1);

		g1.setPlayersJoin(0);

		Game g2 = new Game().withGameTitel("My other Game")
				.withJoinPlayers(new Player()).withJoinPlayers(new Player())
				.withMaxPlayers(3).withStatus("OTHER").withMap(map1);

		g2.setPlayersJoin(1);

		Game g3 = new Game().withGameTitel("No Game").withMaxPlayers(3)
				.withStatus("JOINING").withMap(map1);

		main.getGameManager().getGames().add(g1);
		main.getGameManager().getGames().add(g2);
		main.getGameManager().getGames().add(g3);

		LobbyViewController con = new LobbyViewController();
		con.setApplication(main);

		con.gameTitleColumn = new TableColumn<Game, String>();
		con.mapColumn = new TableColumn<Game, String>();
		con.amountOfPlayersColumn = new TableColumn<Game, String>();

		con.gameList = new TableView<Game>();
		con.gameList.getColumns().add(con.gameTitleColumn);
		con.gameList.getColumns().add(con.amountOfPlayersColumn);
		con.gameList.getColumns().add(con.mapColumn);
		con.filterTextField = new TextField();
		con.filterFullCheckBox = new CheckBox();
		con.filterStartedCheckBox = new CheckBox();
		con.filterMapComboBox = new ComboBox<Map>();
		ListCell<String> lC = new ListCell<String>();
		lC.setText("");
		// con.filterMapComboBox.setButtonCell(lC);
		con.root = new VBox();
		con.filterPane = new FlowPane();
		con.detailAccordion = new Accordion();
		con.getMasterData().addAll(main.getGameManager().getGames());

		con.filterTextField.setText("My");

		con.filterOnKeyReleased();

		Assert.assertEquals(2, con.getFilteredData().size());
		Assert.assertEquals(true, con.getFilteredData().contains(g1));
	}

}
