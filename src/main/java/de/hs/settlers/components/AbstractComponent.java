package de.hs.settlers.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.state.AbstractState;

public abstract class AbstractComponent extends ApplicationHolder {
	private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper("untitled component");
	private SimpleBooleanProperty active = new SimpleBooleanProperty(false);
	
	public AbstractComponent(String name, SettlersApplication app) {
		this.name.set(name);
		setApplication(app);
		getApplication().currentStateProperty().addListener(new ChangeListener<AbstractState>() {
			@Override
			public void changed(ObservableValue<? extends AbstractState> obj, AbstractState ov, AbstractState nv) {
				if (isActive()) {
					onStateChanged(ov, nv);
				}
			}
		});
	}
	
	public ObservableStringValue nameProperty() {
		return name.getReadOnlyProperty();
	}
	
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * A component only acts when it is active. <br>
	 * onStateChanged() won't be called unless the component is active
	 * @return the active property
	 */
	public BooleanProperty activeProperty() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active.set(active);
	}
	
	public boolean isActive() {
		return this.active.get();
	}
	
	/**
	 * Called when the application's state changes. <br>
	 * Convenience callback for getApplication().currentStateProperty().addChangeListener(...)<br>
	 * Won't be called unless active
	 * @param oldState
	 * @param newState
	 */
	public void onStateChanged(AbstractState oldState, AbstractState newState) {}
	
	/**
	 * Called once to boot the system
	 */
	public void boot() {}
	
	/**
	 * Called once to halt the system
	 */
	public void shutdown() {}
}
