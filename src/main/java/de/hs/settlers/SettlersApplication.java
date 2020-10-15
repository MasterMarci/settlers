package de.hs.settlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.Logger;

import de.hs.settlers.configuration.Configuration;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import de.hs.settlers.configuration.ConfigurationException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.hs.settlers.components.AbstractComponent;
import de.hs.settlers.components.GuiComponent;
import de.hs.settlers.components.NetComponent;
import de.hs.settlers.gui.GameViewController;
import de.hs.settlers.gui.ViewController;
import de.hs.settlers.model.ChatRoom;
import de.hs.settlers.model.GameManager;
import de.hs.settlers.model.Player;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.model.map.MapManager;
import de.hs.settlers.net.ClientCommunication;
import de.hs.settlers.net.JsonCommunication;
import de.hs.settlers.net.TextCommunication;
import de.hs.settlers.state.AbstractState;
import de.hs.settlers.state.States;
import de.hs.settlers.util.AssociationUtils;

public class SettlersApplication extends Application {
	static String[] args;

	@Parameter(names = { "-d", "--debug" })
	public boolean debug = false;
	@Parameter(names = { "--disable"})
	public List<String> disabledComponents = new ArrayList<>();
	@Parameter(names = {"--renderMap"})
	public String renderMapFile = null;

	private FXMLLoader fxmlLoader;

	private Timer globalTimer = new Timer();

	private Stage primaryStage;

	private States states;
	
	private ObservableList<AbstractComponent> components = FXCollections.observableArrayList();
	
	{
		components.addListener(new ListChangeListener<AbstractComponent> () {

			@Override
			public void onChanged(ListChangeListener.Change<? extends AbstractComponent> change) {
				while(change.next()) {
					if (change.wasAdded()) {
						for (AbstractComponent added:change.getAddedSubList()) {
							if (!disabledComponents.contains(added.getName())) {
								added.setActive(true);
							} else {
								added.setActive(false);
							}
						}
					}
					if (change.wasRemoved()) {
						for (AbstractComponent removed:change.getRemoved()) {
							removed.setActive(false);
						}
					}
				}
			}
			
		});
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	private Configuration configuration;

	private TextCommunication textCommunicationProtocol;
	private JsonCommunication jsonCommunicationProtocol;
	private ClientCommunication clientCommunication;

	private ViewController viewController = null;

	public void setViewController(ViewController viewController) {
		this.viewController = viewController;
	}

	private ObjectProperty<AbstractState> currentState = new SimpleObjectProperty<>();

	{
		currentState.addListener(new ChangeListener<AbstractState>() {
			@Override
			public void changed(ObservableValue<? extends AbstractState> obj, AbstractState oldValue, AbstractState newValue) {
				StringBuilder changeMessage = new StringBuilder();
				changeMessage.append("State changed ");
				if (oldValue != null) {
					changeMessage.append("from ").append(oldValue.getName()).append(' ');
					try {
						oldValue.end();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (newValue != null) {
					changeMessage.append("to ").append(newValue.getName());
					try {
						newValue.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				LOGGER.info(changeMessage.toString());
			}
		});
	}

	private MapManager mapManager;

	private Player user;

	public static final Logger LOGGER = Logger.getLogger("Settlers");
	public static final Logger CHAT = Logger.getLogger("SettlersChat");
	public static final Logger NET = Logger.getLogger("SettlersNet");

	private final HashMap<String, ViewController> preloadedViews = new HashMap<>();

	static {
		removeLoggerHandlers(CHAT);
		removeLoggerHandlers(NET);
	}

	public static void main(String[] args) {
		SettlersApplication.args = new String[args.length];
		System.arraycopy(args, 0, SettlersApplication.args, 0, args.length);
		launch(args);

		// TODO init logger with lighter format
	}

	private static void removeLoggerHandlers(Logger logger) {
		logger.setUseParentHandlers(false);
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
	}

	public SettlersApplication() {

	}

	@Override
	public void start(Stage stage) throws Exception {
		states = new States(this);
		JCommander commander = new JCommander(this);
		commander.parse(args);
		getComponents().addAll(new GuiComponent(this), new NetComponent(this));
		primaryStage = stage;
		for (AbstractComponent c : getComponents()) {
			if (c.isActive()) {
				c.boot();
			}
		}
		configuration = new Configuration();
		configuration.load();
		mapManager = new MapManager();
		mapManager.setApplication(this);
		gameManager = new GameManager();
		gameManager.setApplication(this);

		// Displaying UI and initializing communication is made via
		// state
		// change.
		// So it should be possible to use different communication
		// protocols and
		// different behavior
		// in view. Changes to other views need not handled in views.
		if (renderMapFile == null) {
			setCurrentState(getStates().NOT_CONNECTED);
		} else {
			try {
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new FileReader(renderMapFile));
				String line = null;
				while(((line = reader.readLine()) != null)) {
					builder.append(line).append("\n");
				}
				reader.close();
				Map map = new Map();
				map.setMapData(builder.toString());
				map.parseMap();
				showView(loadView("gameview.fxml"));
				((GameViewController) getViewController()).setMap(map);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setPrimaryPane(AnchorPane primaryPane) {
		this.primaryPane = primaryPane;
	}

	@Override
	public void stop() throws Exception {
		try {
			configuration.save();
			for (AbstractComponent c : getComponents()) {
				if (c.isActive()) {
					c.shutdown();
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		super.stop();
		System.exit(0);
	}

	public ViewController getViewController() {
		return viewController;
	}

	public TextCommunication getTextCommunicationProtocol() {
		return textCommunicationProtocol;
	}

	public JsonCommunication getJsonCommunicationProtocol() {
		return jsonCommunicationProtocol;
	}

	public Timer getGlobalTimer() {
		return globalTimer;
	}

	public void setGlobalTimer(Timer globalTimer) {
		this.globalTimer = globalTimer;
	}

	public void setTextCommunicationProtocol(
			TextCommunication textCommunicationProtocol) {
		this.textCommunicationProtocol = textCommunicationProtocol;
		this.textCommunicationProtocol.setApplication(this);
	}

	public void setJsonCommunicationProtocol(JsonCommunication jsonCommunicationProtocol) {
		this.jsonCommunicationProtocol = jsonCommunicationProtocol;
		this.jsonCommunicationProtocol.setApplication(this);
	}

	public States getStates() {
		return states;
	}

	public AbstractState getCurrentState() {
		return this.currentState.get();
	}

	public void setCurrentState(final AbstractState currentState) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				SettlersApplication.this.currentState.set(currentState);
			}
		});
	}

	public ObjectProperty<AbstractState> currentStateProperty() {
		return this.currentState;
	}

	/**
	 * Loads the view specified and returns the controller <br> This will always
	 * create a new instance, so you might end up with multiple instances of the
	 * same view. Be careful
	 * 
	 * @param file
	 *            the view to load (myview.fxml) <br> the file is loaded from
	 *            /views/
	 * @return the loaded controller
	 */
	public ViewController loadView(String file) {
		ViewController controller = preloadedViews.get(file);
		if (controller != null) {
			return controller;
		}
		fxmlLoader = new FXMLLoader(SettlersApplication.class
				.getResource("/views/" + file));;

		Parent result;
		try {
			result = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		controller = fxmlLoader.getController();
		controller.setApplication(this);
		controller.init();
		return controller;
	}

	public void preloadView(String file) {
		ViewController loaded = loadView(file);
		if (loaded != null) {
			loaded.setPreloaded(true);
			preloadedViews.put(file, loaded);
		} else {
			LOGGER.warning("File to preload '" + file + "' not loaded correctly.");
		}
	}

	private AnchorPane primaryPane;
	private static final Duration VIEW_FADE_DURATION = Duration.millis(750);

	/**
	 * Replaces the content of the mainStage with the given
	 * {@link ViewController}
	 * 
	 * @param controller
	 *            the {@link ViewController} to replace the current
	 *            {@link ViewController}
	 */
	public void showView(ViewController controller) {
		final ViewController oldController = getViewController();
		final ViewController newController = controller;

		if (oldController != null) {
			FadeTransition fadeOut = new FadeTransition(VIEW_FADE_DURATION, oldController.getRootNode());
			fadeOut.setFromValue(1d);
			fadeOut.setToValue(0d);
			fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					primaryPane.getChildren().remove(oldController.getRootNode());
					try {
						oldController.onHide();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			fadeOut.play();
		}
		AnchorPane.setLeftAnchor(newController.getRootNode(), 0d);
		AnchorPane.setTopAnchor(newController.getRootNode(), 0d);
		AnchorPane.setRightAnchor(newController.getRootNode(), 0d);
		AnchorPane.setBottomAnchor(newController.getRootNode(), 0d);

		primaryPane.getChildren().add(newController.getRootNode());
		newController.getRootNode().setOpacity(0d);
		FadeTransition fadeIn = new FadeTransition(VIEW_FADE_DURATION, newController.getRootNode());
		fadeIn.setFromValue(0d);
		fadeIn.setToValue(1d);
		fadeIn.play();

		try {
			newController.onShow();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setViewController(newController);
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Data Model Association with other Data Model Classes
	 */
	private SimpleStringProperty myID = new SimpleStringProperty();

	public void setMyID(String value) {
		this.myID.set(value);
	}

	public SettlersApplication withMyID(String value) {
		setMyID(value);
		return this;
	}

	public String getMyID() {
		return this.myID.get();
	}

	public StringProperty myIdProperty() {
		return myID;
	}

	public void createGame() {
	}

	public void uploadMap() {
	}

	public void downloadMap() {
	}

	public void joinGame(boolean visitor) {
	}

	/**
	 * <pre> 0..1 players 0..n SettlersApplication -------------------------
	 * Player application players </pre>
	 */
	private ObservableSet<Player> players = FXCollections.observableSet(new LinkedHashSet<Player>());

	{
		AssociationUtils.manyInMe(this, players, "application", Player.class);
	}

	public ObservableSet<Player> getPlayers() {
		return players;
	}

	/**
	 * <pre> 0..1 games 0..n SettlersApplication ------------------------- Game
	 * application existGames </pre>
	 */

	private GameManager gameManager;

	public GameManager getGameManager() {
		return gameManager;
	}

	/**
	 * <pre> 0..1 chats 0..n SettlersApplication -------------------------
	 * ChatRoom application chats </pre>
	 */

	private ObservableSet<ChatRoom> chats = FXCollections.observableSet(new LinkedHashSet<ChatRoom>());

	{
		AssociationUtils.manyInMe(this, chats, "application", ChatRoom.class);
	}

	public ObservableSet<ChatRoom> getChats() {
		return chats;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public void setGameManager(GameManager gM) {
		gameManager = gM;
	}

	public Player getUser() {
		return user;
	}

	public void setUser(Player user) {
		this.user = user;
	}

	public void setMapManager(MapManager mapManager2) {
		mapManager = mapManager2;
	}

	public ClientCommunication getClientCommunication() {
		return clientCommunication;
	}

	public void setClientCommunication(ClientCommunication clientCommunication) {
		this.clientCommunication = clientCommunication;
		this.clientCommunication.setApplication(this);
	}
	
	public ObservableList<AbstractComponent> getComponents() {
		return components;
	}
}