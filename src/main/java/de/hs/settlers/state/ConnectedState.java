package de.hs.settlers.state;

import javafx.application.Platform;

public class ConnectedState extends AbstractState {
	protected ConnectedState() {
		super("Connected");
	}

	@Override
	public void start()
	{
		
	}

	@Override
	public void displayError(final String err)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getApplication().getViewController().displayError(err.replaceFirst("ERROR: ", ""));
			}
		});
	}

}
