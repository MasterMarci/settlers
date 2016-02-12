package de.hs.settlers.net.message.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.hs.settlers.model.Recipient;
import de.hs.settlers.net.message.ClientMessage;

public class JsonSendChatMessage<JsonElement> extends BasicClientJsonMessage {

	JsonObject data;
	Recipient recipient;
	String message;
	
	public JsonSendChatMessage(Recipient recipient,String message) {
		this.recipient = recipient;
		this.message = message;
	}
	
	
	@Override
	public com.google.gson.JsonElement getData() {
		JsonObject msg = new JsonObject();
		JsonObject msg2 = new JsonObject();
		JsonObject msg3 = new JsonObject();
		JsonObject msg4 = new JsonObject();
		msg2.addProperty("key", "message");
		msg2.addProperty("value", message);
		msg.add("entry", msg2);
		msg3.addProperty("key", "audience");
		msg4.addProperty("key", "recipient");
		
		JsonObject messageBody = new JsonObject();
		messageBody.addProperty("action", "MESSAGE");
		
		if(recipient.getRecipientString().contains("ALL")) {
			msg3.addProperty("value", "ALL");
		} else if(recipient.getRecipientString().contains("USER")) {
			msg3.addProperty("value","USER");
		} else {
			msg3.addProperty("value","TEAM");
		}
		String[] array = recipient.getRecipientString().split(" ");
		if(array.length > 1)
			msg4.addProperty("value", array[1]);
		else
			msg4.addProperty("value", "");
		msg.add("entry", msg3);
		messageBody.add("properties", msg);
		return messageBody;
	}
}
