package de.hs.settlers.model;

import java.util.LinkedList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.util.AssociationUtils;

public class ChatRoom extends ApplicationHolder {

	private SimpleStringProperty chatWith = new SimpleStringProperty();
	private ObservableList<String> messages = FXCollections.observableList(new LinkedList<String>());
	private SimpleIntegerProperty length = new SimpleIntegerProperty(500);

	{
		AssociationUtils.iInOne(this, applicationProperty(), "chats", SettlersApplication.class);
	}

	public String getChatWith() {
		return this.chatWith.get();
	}

	public int getLength() {
		return this.length.get();
	}

	public ObservableList<String> getMessages() {
		return this.messages;
	}

	public void setChatWith(String value) {
		this.chatWith.set(value);
	}

	public StringProperty chatWithProperty() {
		return chatWith;
	}

	public void setLength(int value) {
		this.length.set(value);
	}

	public IntegerProperty lengthProperty() {
		return length;
	}
}
