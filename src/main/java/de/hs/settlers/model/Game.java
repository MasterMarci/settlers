package de.hs.settlers.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.util.AssociationUtils;

public class Game
{

	private SimpleStringProperty gameTitle = new SimpleStringProperty();
	private SimpleIntegerProperty playersJoin = new SimpleIntegerProperty();
	private SimpleIntegerProperty kiPlayers = new SimpleIntegerProperty();
	private SimpleIntegerProperty maxPlayers = new SimpleIntegerProperty();
	private SimpleBooleanProperty testGame = new SimpleBooleanProperty();
	private SimpleStringProperty status = new SimpleStringProperty();
	private SimpleObjectProperty<Map> map = new SimpleObjectProperty<>();
	private SimpleStringProperty mapTitle = new SimpleStringProperty();
	private SimpleStringProperty playerInCanJoin = new SimpleStringProperty();
	private SimpleObjectProperty<GameManager> gameManager = new SimpleObjectProperty<>();
	private SimpleObjectProperty<ImageView> statusImage = new SimpleObjectProperty<>();
	
	private static final String greenIcon = new String("file://" + System.getProperty("user.dir")+ "/src/main/resources/images/greenIcon.png");
	private static final String redIcon = new String("file://" + System.getProperty("user.dir")+ "/src/main/resources/images/redIcon.png");
	
	{
		AssociationUtils.iInOne(this,gameManager,"games",GameManager.class);
	}
	
	public void setGameManager(GameManager gameManager) {
		this.gameManager.set(gameManager);
	}
	
	public GameManager getGameManager() {
		return this.gameManager.get();
	}
	
	public SimpleObjectProperty<GameManager> gameManagerProperty() {
		return this.gameManager;
	}
	
	public SimpleStringProperty mapTitleProperty() {
		return this.mapTitle;
	}

	public String getPlayerInCanJoin() {
		playerInCanJoin.set((getPlayersJoin()+ getKiPlayers()) + "/" + getMaxPlayers());
		return playerInCanJoin.get();
	}

	{
		AssociationUtils.iInOne(this, map, "games", Map.class);
	}

	// game Titel
	public void setGameTitle(String value) {
		this.gameTitle.set(value);
	}

	public String getGameTitle() {
		return this.gameTitle.get();
	}

	public StringProperty gameTitleProperty() {
		return gameTitle;
	}

	// players Join
	public void setPlayersJoin(int value) {
		this.playersJoin.set(value);
	}

	public int getPlayersJoin() {
		return this.playersJoin.get();
	}

	public IntegerProperty playersJoinProperty() {
		return playersJoin;
	}

	// ki Players
	public void setKiPlayers(int value) {
		this.kiPlayers.set(value);
	}

	public int getKiPlayers() {
		return this.kiPlayers.get();
	}

	public IntegerProperty kiPlayersProperty() {
		return kiPlayers;
	}

	// max Players
	public void setMaxPlayers(int value) {
		this.maxPlayers.set(value);
	}

	public int getMaxPlayers() {
		return this.maxPlayers.get();
	}

	public IntegerProperty maxPlayersProperty() {
		return maxPlayers;
	}

	// test Game
	public void setTestGame(boolean value) {
		this.testGame.set(value);
	}

	public boolean isTestGame() {
		return this.testGame.get();
	}

	public BooleanProperty gameTestGameProperty() {
		return testGame;
	}

	// status
	public void setStatus(String value) {
		this.status.set(value);
	}

	public String getStatus() {
		return this.status.get();
	}
	
	public ImageView getStatusImage() {
		if(this.getStatus().equals("JOINING")) {
			statusImage.set(new ImageView(new Image(greenIcon)));
			} else {
				statusImage.set(new ImageView(new Image(redIcon)));
			}
		return statusImage.get();
	}
	
	public SimpleObjectProperty<ImageView> statusImageProperty() {
		if(this.getStatus().equals("JOINING")) {
			statusImage.set(new ImageView(new Image(greenIcon)));
			} else {
				statusImage.set(new ImageView(new Image(redIcon)));
			}
		return statusImage;
	}

	public StringProperty statusProperty() {
		return status;
	}

	// map
	public void setMap(Map map) {
		this.map.setValue(map);
		this.mapTitle.set(map.getMapTitle());
	}

	public Map getMap() {
		return map.get();
	}

	public SimpleObjectProperty<Map> mapProperty() {
		return map;
	}

	public Game withGameTitel(String string) {
		setGameTitle(string);
		return this;
	}

	public Game withJoinPlayers(Player player) {
		
		//TODO add local player to the playerlist
		// if there is a playerList how can it be accessed?
		return this;
	}

	public Game withKiPlayers(int i) {
		setKiPlayers(i);
		return this;
	}

	public Game withMaxPlayers(int i) {
		setMaxPlayers(i);
		return this;
	}

	public Game withStatus(String string) {
		setStatus(string);
		return this;
	}

	public Game withMap(Map withMap) {
		setMap(withMap);
		return this;
	}
	
	public String getMapTitle() {
		return getMap().getMapTitle();
	}
}
