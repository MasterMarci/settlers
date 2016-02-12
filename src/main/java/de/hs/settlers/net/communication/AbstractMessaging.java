package de.hs.settlers.net.communication;

import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.net.TextCommunication;

public abstract class AbstractMessaging extends ApplicationHolder
{
	protected TextCommunication com;

	public TextCommunication getCommunication()
	{
		return this.com;
	}

	public void setCommunication(TextCommunication com)
	{
		if (this.com != com)
		{
			if (this.com != null)
			{
				this.com.removeMsgHandler(this);
			}
			this.com = com;
			if (this.com != null)
			{
				this.com.addMsgHandler(this);
			}
		}
	}

	/**
	 * @param line
	 * @return if the given line makes this handler responsible to handle it<br>
	 *         only one handler can handle a message
	 */
	public abstract boolean isResponsible(String message);

	/**
	 * Handle the given message
	 * 
	 * @param message
	 * @return if the message was handled
	 */
	public abstract boolean handleMessage(String message);
}
