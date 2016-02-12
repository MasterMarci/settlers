package de.hs.settlers.state;

import de.hs.settlers.SettlersApplication;

public class NotConnectedState extends AbstractState {
	protected NotConnectedState() {
		super("Not Connected");
	}

	@Override
	public void start() {
		
	}

	@Override
	public void displayError(String err)
	{
		SettlersApplication.LOGGER.severe("Error: " + err);
		getApplication().getViewController().displayError(err);
	}
}
