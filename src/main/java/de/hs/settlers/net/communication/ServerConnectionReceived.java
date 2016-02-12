package de.hs.settlers.net.communication;

import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hs.settlers.net.TextCommunication;
import de.hs.settlers.net.message.text.NoopMessage;

public class ServerConnectionReceived extends AbstractMessaging
{
	private static final Pattern WELCOME_MESSAGE_PATTERN = Pattern.compile("^.* Timeout set to ([0-9]+)ms$");

	@Override
	public boolean isResponsible(String line)
	{
		if (line.startsWith("SE1 SettlersOfCatan-Server "))
			return true;
		else
			return false;
	}

	@Override
	public boolean handleMessage(String line)
	{
		Matcher matcher = WELCOME_MESSAGE_PATTERN.matcher(line);
		if (matcher.matches()) {
			if (matcher.groupCount() >= 1) {
				int timeout = Integer.parseInt(matcher.group(1));

				timeout -= 5000;
				getApplication().getGlobalTimer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (getCommunication().isActive()) {
							getCommunication().sendMessage(new NoopMessage());
						}
					}
				}, timeout, timeout);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						getApplication().getGlobalTimer().cancel();
					}
				});
			}
		}
		((TextCommunication) this.getCommunication()).setReady(true);
		((TextCommunication) this.getCommunication()).sendNextMessage();

		// Change current state
		getCommunication().getApplication().setCurrentState(getApplication().getStates().CONNECTED);

		return true;
	}

}
