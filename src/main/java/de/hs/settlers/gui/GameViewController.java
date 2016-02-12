package de.hs.settlers.gui;

import java.util.LinkedList;

import com.google.gson.JsonElement;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import de.hs.settlers.model.AllLobbyUsers;
import de.hs.settlers.model.Recipient;
import de.hs.settlers.model.Team;
import de.hs.settlers.model.map.Map;
import de.hs.settlers.model.map.MapField;
import de.hs.settlers.net.message.json.JsonSendChatMessage;

public class GameViewController extends ViewController implements EventHandler<KeyEvent>{
	private @FXML AnchorPane root;
	private @FXML VBox chatBox;
	private @FXML TabPane chatTabs;
	private @FXML TextField chatTextField;
	private @FXML Button chatSendButton;
	private LinkedList<ChatTab> chatTabList = new LinkedList<>();
	private @FXML AnchorPane mapViewPane;
	
	SimpleDoubleProperty panX = new SimpleDoubleProperty(0d);
	SimpleDoubleProperty panY = new SimpleDoubleProperty(0d);

	@Override
	public void init() {
		root.sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> arg0, Scene arg1, Scene nv) {
				nv.getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHIFT_ANY), new KeyPanner(-10, 0));
				nv.getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHIFT_ANY), new KeyPanner(10, 0));
				nv.getAccelerators().put(new KeyCodeCombination(KeyCode.UP, KeyCombination.SHIFT_ANY), new KeyPanner(0, -10));
				nv.getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.SHIFT_ANY), new KeyPanner(0, 10));
			}
		});
		getApplication().getPrimaryStage().addEventHandler(KeyEvent.ANY, this);
	}
	
	@Override
	public Node getRootNode() {
		return root;
	}

	@Override
	public void displayError(String error) {
		// TODO Auto-generated method stub

	}
	
	Map map;

	public void setMap(Map map) {
		this.map = map;
		for (MapField field : map.getFields()) {
			mapViewPane.getChildren().add(new MapFieldController(field, this, 100d));
		}
	}
	
	private boolean dragInit = false;
	private double lastX = 0, lastY = 0;
	
	public void onFieldPanning(MouseEvent event) {
		if (dragInit) {
			double dx = event.getScreenX() - lastX;
			double dy = event.getScreenY() - lastY;
			
			panX.set(panX.get() + dx);
			panY.set(panY.get() + dy);
		}
		lastX = event.getScreenX();
		lastY = event.getScreenY();
		dragInit = true;
	}
	
	public void onFieldPanFinish() {
		dragInit = false;
	}
	
	public void onFieldScroll(ScrollEvent event) {
		panX.set(panX.get() + event.getDeltaX());
		panY.set(panY.get() + event.getDeltaY());
	}
	
	private class KeyPanner implements Runnable {
		private double x, y;
		
		public KeyPanner(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			panX.set(panX.get() + x);
			panY.set(panY.get() + y);
		}
		
	}
	
	@Override
	public void handle(KeyEvent arg0) {
		if(arg0.getCode().equals(KeyCode.ENTER)) {
			if(chatTextField.isFocused()) {
				onSendAction();
				
			} else {
				chatTextField.requestFocus();
			}
		}
		
	}
	
	@Override
	public void onShow() {
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
//				getApplication().getJsonCommunicationProtocol().getModel().getCollection("ALL_MESSAGE").getChildren().addListener(new ListChangeListener<TreeItem<String>>() {
//
//					
//					@Override
//					public void onChanged(
//							javafx.collections.ListChangeListener.Change<? extends TreeItem<String>> arg0) {
//						if(arg0.wasAdded()) {
//							for(TreeItem<String> s:arg0.getList()) {
//								System.out.println(s.getValue());
//								printAllChildren(s);
//							}
//						}
//						
//					}
//				});
				

	}
	
	protected void printAllChildren(TreeItem<String> s) {
		System.out.println(s.getValue());
		for(TreeItem<String> i:s.getChildren()) {
			printAllChildren(i);
		}
		
	}

	@FXML
	public void onSendAction() {
		if (chatTextField.getText().length() > 0) {
			String message = chatTextField.getText();
			message = message.trim();

			if (!message.isEmpty()) {
				Recipient recipient = null;
				for (ChatTab tab : chatTabList) {
					if (tab.getTab().isSelected()) {
						recipient = tab.getRecipient();
					}
				}
				getApplication().getJsonCommunicationProtocol().sendMessage(
						new JsonSendChatMessage<JsonElement>(recipient, message));

			}
			chatTextField.setText("");
			
		}
	}
	
	@Override
	public void onHide() {
		chatTabList.clear();
		chatTabs.getTabs().clear();
		
	}
}
