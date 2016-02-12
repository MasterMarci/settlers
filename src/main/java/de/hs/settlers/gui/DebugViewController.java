package de.hs.settlers.gui;

import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.message.text.GenericMessage;
import de.hs.settlers.state.AbstractState;

public class DebugViewController extends ViewController
{
	@FXML
	VBox root;

	@FXML
	TabPane tabs;

	@FXML
	TextField command;

	private HashSet<LoggerTab> loggerTabs = new HashSet<LoggerTab>(3);

	protected DynamicModelDebugTab currentModelTab;

	@Override
	public void init() {
		addLogger(SettlersApplication.LOGGER, "Misc");
		addLogger(SettlersApplication.CHAT, "Chat");
		addLogger(SettlersApplication.NET, "Network");
		addLogger(Logger.getGlobal(), "Global (libs)");
		addLogger(Logger.getAnonymousLogger(), "Anonymous");

		getApplication().currentStateProperty().addListener(new ChangeListener<AbstractState>() {
			@Override
			public void changed(ObservableValue<? extends AbstractState> obj, AbstractState ov, AbstractState nv) {
				if (nv != null && nv.equals(getApplication().getStates().PLAYING)) {
					currentModelTab = new DynamicModelDebugTab(getApplication().getJsonCommunicationProtocol().getModel());
					tabs.getTabs().add(currentModelTab);
				} else if (ov != null && ov.equals(getApplication().getStates().PLAYING)) {
					tabs.getTabs().remove(currentModelTab);
					currentModelTab = null;
				}
			}
		});
	}

	@Override
	public Node getRootNode()
	{
		return root;
	}

	public void onSendCommandAction() {
		String message = command.getText();
		if (!message.trim().isEmpty()) {
			getApplication().getTextCommunicationProtocol().sendMessage(new GenericMessage(command.getText()));
		}
		command.clear();
	}

	@Override
	public void displayError(String error)
	{
		// TODO Auto-generated method stub

	}

	public void addLogger(Logger logger, String name) {
		if (loggerTabs.contains(logger)) {
			return;
		}
		LoggerTab tab = new LoggerTab(logger, name);

		tabs.getTabs().add(tab.getTab());

		loggerTabs.add(tab);
	}

	private class LoggerTab {
		Tab tab;
		TextArea history;
		Logger logger;
		Label unreadLabel;
		int unread = 0;

		public LoggerTab(Logger logger, String name) {
			super();
			this.logger = logger;
			tab = new Tab();
			history = new TextArea();
			history.setEditable(false);
			tab.setText(name);
			tab.setContent(history);
			unreadLabel = new Label("" + unread);
			unreadLabel.setStyle("border-radius: 0.5em !important; background: lightblue !important; color: white !important;");
			tab.setGraphic(unreadLabel);

			tab.setOnSelectionChanged(new EventHandler<Event>() {
				@Override
				public void handle(Event arg0) {
					unread = 0;
					updateUnread();
				}
			});

			logger.addHandler(new Handler() {
				@Override
				public void publish(final LogRecord record) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								history.appendText(record.getMessage() + "\n");
								unread++;
								updateUnread();
								// TODO more info perhaps
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}

				@Override
				public void flush() {

				}

				@Override
				public void close() throws SecurityException {

				}
			});
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

		public Logger getLogger() {
			return logger;
		}

		@Override
		public int hashCode() {
			return logger.hashCode();
		}
	}
}
