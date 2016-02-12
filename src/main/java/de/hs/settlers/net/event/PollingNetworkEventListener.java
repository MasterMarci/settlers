package de.hs.settlers.net.event;

import java.util.TimerTask;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.hs.settlers.IApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.message.ClientMessage;
import de.hs.settlers.net.message.ServerMessage;

public abstract class PollingNetworkEventListener<T extends ServerMessage> extends NetworkEventListener<T> implements IApplicationHolder {

	private Class<? extends ClientMessage> requestType;
	private int interval;
	private TimerTask task;
	private SimpleBooleanProperty running = new SimpleBooleanProperty(false);
	private SimpleObjectProperty<SettlersApplication> app = new SimpleObjectProperty<>();

	{
		running.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obj, Boolean o, Boolean n) {
				if (n) {
					task = makeTask();
					getApplication().getGlobalTimer().schedule(task, interval);
				} else {
					task.cancel();
					task = null;
				}
			}
		});
	}

	/**
	 * Constructs a new Polling event listener
	 * 
	 * @param type
	 *            type of the server message
	 * @param requestType
	 *            type of the client message
	 * @param interval
	 *            time to wait in milliseconds until refreshing
	 * @param timer
	 *            the timer to register to
	 */
	public PollingNetworkEventListener(Class<T> type, Class<? extends ClientMessage> requestType, int interval, SettlersApplication app) {
		super(type);
		setApplication(app);
		this.requestType = requestType;
		this.interval = interval;
	}

	private TimerTask makeTask() {
		return new TimerTask() {

			@Override
			public void run() {
				getApplication().getTextCommunicationProtocol().sendMessage(getMessage());
			}
		};
	}

	@Override
	public void onMessage(T message) {
		if (isRunning()) {
			this.task = makeTask();
			getApplication().getGlobalTimer().schedule(task, interval);
		}
		onPollMessage(message);
	}

	public abstract void onPollMessage(T message);

	public ClientMessage getMessage() {
		try {
			return requestType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void start() {
		running.set(true);
	}

	public void stop() {
		running.set(false);
	}

	public boolean isRunning() {
		return running.get();
	}

	public void setRunning(boolean running) {
		this.running.set(running);
	}

	public BooleanProperty runningProperty() {
		return running;
	}

	@Override
	public SettlersApplication getApplication() {
		return app.get();
	}

	@Override
	public void setApplication(SettlersApplication application) {
		this.app.set(application);
	}

	@Override
	public ObjectProperty<SettlersApplication> applicationProperty() {
		return app;
	}
}
