package de.hs.settlers.state;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import de.hs.settlers.ApplicationHolder;

public abstract class AbstractState extends ApplicationHolder
{
	private ObservableStringValue name;

	protected AbstractState(String name) {
		this.name = new SimpleStringProperty(name);
	}

	/**
	 * @return the name of the state
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @return the name property
	 */
	public ObservableStringValue nameProperty() {
		return name;
	}

	/**
	 * Called when an error occurs in the network protocol or elsewhere
	 * 
	 * @param err
	 *            the error message
	 */
	public abstract void displayError(String err);

	/**
	 * Called when the state is activated
	 */
	public void start() {
	}

	/**
	 * Called when the state is instantiated. Only called once per lifetime
	 */
	public void init() {
	}

	/**
	 * Called when the state is disabled
	 */
	public void end() {
	}
}
