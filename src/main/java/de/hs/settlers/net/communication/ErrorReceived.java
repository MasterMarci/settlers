package de.hs.settlers.net.communication;

import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.TextCommunication;
import de.hs.settlers.net.message.ClientMessage;

public class ErrorReceived extends AbstractMessaging
{

	@Override
	public boolean isResponsible(String line)
	{
		if (line.startsWith("ERROR: "))
			return true;
		else
			return false;
	}

	@Override
	public boolean handleMessage(String line)
	{
		this.getCommunication().getApplication().getCurrentState().displayError(line);
		SettlersApplication.LOGGER.severe("Received " + line);
		ClientMessage<String> responsible = ((TextCommunication) this.getCommunication()).getRequests().poll();
		SettlersApplication.LOGGER.severe(responsible.getData());
		for (String line2 : ((TextCommunication) this.getCommunication()).getCurrentMessageLines()) {
			SettlersApplication.LOGGER.severe(line2);
		}
		this.getCommunication().getEventManager().callError(responsible, line.replaceFirst("ERROR: ", ""));
		((TextCommunication) this.getCommunication()).getCurrentMessageLines().clear();
		((TextCommunication) this.getCommunication()).sendNextMessage();
		return true;
	}
}
