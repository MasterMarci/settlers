package de.hs.settlers.net.communication;

import javafx.application.Platform;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.message.text.ClientTextMessage;
import de.hs.settlers.util.StringUtils;

public class SuccessReceived extends AbstractMessaging {

	@Override
	public boolean isResponsible(String line)
	{
		if (line.equalsIgnoreCase("OK"))
			return true;
		else
			return false;
	}

	@Override
	public boolean handleMessage(String line)
	{
		final ClientTextMessage lastRequest = (ClientTextMessage) this.getCommunication().getRequests().poll();
		if (lastRequest != null) {
			try {
				lastRequest.getAnswer().setData(StringUtils.glue(this.getCommunication().getCurrentMessageLines(), "/n"));
			} catch (Exception e) {
				// TODO log
				e.printStackTrace();
			}

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					getCommunication().getEventManager().callMessage(lastRequest.getAnswer());
				}
			});
		} else {
			SettlersApplication.NET.severe("Received reply to no request.");
			for (String l : this.getCommunication().getCurrentMessageLines()) {
				SettlersApplication.NET.severe(l);
			}
		}
		this.getCommunication().getCurrentMessageLines().clear();
		this.getCommunication().sendNextMessage();

		return true;
	}
}
