package de.hs.settlers.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.ChatRoom;
import de.hs.settlers.model.Recipient;

public class ChatTab extends ApplicationHolder {
	Tab tab;
	TextArea history;
	ChatRoom chat;
	Label unreadLabel;
	int unread = 0;
	Recipient recipient;

	public ChatTab(Recipient recipient, String name, SettlersApplication app) {
		tab = new Tab();
		history = new TextArea();
		history.getStyleClass().add("ChatTab");
		history.setEditable(false);
		tab.setText(name);
		tab.setContent(history);
		unreadLabel = new Label("" + unread);
		unreadLabel.setStyle("border-radius: 0.5em !important; background: lightblue !important; color: white !important;");
		tab.setGraphic(unreadLabel);
		setApplication(app);
		this.recipient = recipient;

		if (name.equalsIgnoreCase("All") || name.equalsIgnoreCase("Team")) {
			tab.setClosable(false);
		}

		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				unread = 0;
				updateUnread();
			}
		});

	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	private void updateUnread() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				unreadLabel.setText(unread + "");
			}
		});
	}

	public TextArea getHistory() {
		return history;
	}

	public Tab getTab() {
		return tab;
	}

	public void incrementUnread() {
		if (!tab.isSelected()) {
			unread++;
			updateUnread();
		}
	}
}
