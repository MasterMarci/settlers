package de.hs.settlers.net.communication;

import javafx.application.Platform;
import de.hs.settlers.net.NonAnswerServerMessageFilter;
import de.hs.settlers.net.TextCommunication;
import de.hs.settlers.net.message.ServerMessage;

public class UnknownReceived extends AbstractMessaging
{

	@Override
	public boolean isResponsible(String line)
	{
		return true;
	}

	@Override
	public boolean handleMessage(String line)
	{
		boolean found = false;
		if (((TextCommunication) this.getCommunication()).getCurrentMessageLines().size() == 0 || ((TextCommunication) this.getCommunication()).getRequests().isEmpty()) {
			for (NonAnswerServerMessageFilter filter : ((TextCommunication) this.getCommunication()).getFilters()) {
				if (filter.matches(line)) {
					try {
						final ServerMessage<String> message = filter.getType().newInstance();
						message.setData(line);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								getCommunication().getEventManager().callMessage(message);
							}
						});
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					found = true;
					break;
				}
			}
		}
		if (!found) {
			((TextCommunication) this.getCommunication()).getCurrentMessageLines().add(line);
		}

		return true;
	}

}
