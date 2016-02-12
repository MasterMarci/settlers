package de.hs.settlers.net;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.dynamic.DynamicModel;
import de.hs.settlers.model.dynamic.DynamicModelObject;
import de.hs.settlers.model.dynamic.DynamicModelUtils;
import de.hs.settlers.net.message.ClientMessage;

public class JsonCommunication extends AbstractCommunication<JsonElement> {
	private JsonParser parser = new JsonParser();
	private Gson gson = new Gson();
	private DynamicModel model = new DynamicModel();
	private static final Pattern OBJECT_ADDRESS_PATTERN = Pattern.compile("^([A-Za-z]+)@([0-9a-f]+)$");
	private static final Pattern DOUBLE_VALUE_PATTERN = Pattern.compile("^[0-9]+\\.[0-9]+$");
	private static final Pattern INTEGER_VALUE_PATTERN = Pattern.compile("^[0-9]+$");
	private static final Pattern BOOLEAN_VALUE_PATTERN = Pattern.compile("^true|false$");
	private boolean testMode = false;

	@Override
	public void readLine(String line) {
		PropertyChanger propertyChanger = new PropertyChanger(line);
		if (!isTestMode()) {
			Platform.runLater(propertyChanger);
		} else {
			propertyChanger.run();
		}
	}

	@Override
	public void sendMessage(ClientMessage<JsonElement> message) {
		try {
			synchronized (getWriter()) {
				writeLine(gson.toJson(message.getData()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeMessage(ClientMessage<JsonElement> message) {
		throw new UnsupportedOperationException("Use sendMessage instead");
	}

	private class PropertyChanger implements Runnable {
		private String line;

		public PropertyChanger(String line) {
			super();
			this.line = line;
		}

		@Override
		public void run() {
			JsonElement element = parser.parse(line);
			DynamicModelObject object = DynamicModelUtils.getObjectByAdress(element.getAsJsonObject().get("@src").getAsString(), model);
			String propertyName = element.getAsJsonObject().get("@prop").getAsString();
			JsonElement nv = element.getAsJsonObject().get("@nv");
			JsonElement ov = element.getAsJsonObject().get("@ov");
			Object value = null;
			boolean isList = object.getCollection().getAsListProperties().contains(propertyName);
			if (nv != null) {
				String raw = nv.getAsString();
				if (OBJECT_ADDRESS_PATTERN.matcher(raw).matches()) {
					value = DynamicModelUtils.getObjectByAdress(nv.getAsString(), model);
				} else if (DOUBLE_VALUE_PATTERN.matcher(raw).matches()) {
					value = nv.getAsDouble();
				} else if (INTEGER_VALUE_PATTERN.matcher(raw).matches()) {
					value = nv.getAsInt();
				} else if (BOOLEAN_VALUE_PATTERN.matcher(raw).matches()) {
					value = nv.getAsBoolean();
				} else {
					value = raw;
				}
			} else if (ov != null) { // is a deletion
				value = null;
				if (isList) {
					value = model.getObject(ov.getAsString());
				}
			}
			if (!isList) {
				Property<Object> prop = (Property<Object>) object.getProperties().get(propertyName);
				if (prop != null) {
					if (prop.getValue() != null && (value == null || prop.getValue().getClass().isAssignableFrom(value.getClass()))) {
						prop.setValue(value);
					} else {
						SettlersApplication.NET.warning("Received invalid property update - types don't match.");
					}
				} else {
					if (value == null) {
						return;
					}
					((Property<Object>) object.getProperty(propertyName, value.getClass())).setValue(value);
				}
			} else {
				ListProperty<DynamicModelObject> list = (ListProperty<DynamicModelObject>) object.getProperty(propertyName, List.class);
				if (nv != null) {
					list.add((DynamicModelObject) value);
				} else if (ov != null) {
					list.remove(value);
				}
			}
		}
	}

	public DynamicModel getModel() {
		return model;
	}
	
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
	public boolean isTestMode() {
		return testMode;
	}
}
