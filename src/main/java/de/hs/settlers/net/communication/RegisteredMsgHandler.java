package de.hs.settlers.net.communication;

import de.hs.settlers.net.TextCommunication;

public class RegisteredMsgHandler
{
	public static void registerMsgHandlers(TextCommunication com)
	{
		com.addMsgHandler(new ServerConnectionReceived());
		com.addMsgHandler(new SuccessReceived());
		com.addMsgHandler(new LogoutReceived());
		com.addMsgHandler(new ErrorReceived());
		com.addMsgHandler(new UnknownReceived());
	}
}
