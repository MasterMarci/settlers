package de.hs.settlers.net.communication;

import de.hs.settlers.net.TextCommunication;

public class LogoutReceived extends AbstractMessaging
{

	@Override
	public boolean isResponsible(String line)
	{
		// TODO this is language dependant, should be handled differently
		if (line.startsWith("Verbindung zu Host verloren"))
			return true;
		else
			return false;
	}

	@Override
	public boolean handleMessage(String line)
	{
		((TextCommunication) this.getCommunication()).setReady(false);
		((TextCommunication) this.getCommunication()).getCurrentMessageLines().clear();

		// Change current state
		getCommunication().getApplication().setCurrentState(getApplication().getStates().NOT_CONNECTED);

		return true;
	}
}
