package de.hs.settlers.net;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.hs.settlers.net.communication.AbstractMessaging;
import de.hs.settlers.net.message.ClientMessage;
import de.hs.settlers.net.message.text.ClientTextMessage;
import de.hs.settlers.net.message.text.ServerTextMessage;

public class TextCommunication extends AbstractCommunication<String> {
	private LinkedList<NonAnswerServerMessageFilter> filters = new LinkedList<NonAnswerServerMessageFilter>();
	private LinkedList<String> currentMessageLines = new LinkedList<String>();
	private ConcurrentLinkedQueue<ClientMessage<String>> requests = new ConcurrentLinkedQueue<>();
	private boolean ready = false;
	private LinkedHashSet<AbstractMessaging> msgHandlers = new LinkedHashSet<AbstractMessaging>();

	public TextCommunication() {

	}

	public boolean getReady()
	{
		return this.ready;
	}

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	public LinkedList<NonAnswerServerMessageFilter> getFilters()
	{
		return filters;
	}

	public ConcurrentLinkedQueue<ClientMessage<String>> getRequests()
	{
		return requests;
	}

	public LinkedList<String> getCurrentMessageLines()
	{
		return currentMessageLines;
	}

	@Override
	protected void readLine(String line) {
		synchronized (requests) {
			// chain of responsibility
			for (AbstractMessaging msg : this.getMsgHandlers())
			{
				if (msg.isResponsible(line))
				{
					msg.handleMessage(line);
					break;
				}
			}
		}
	}

	@Override
	public void sendMessage(ClientMessage<String> message) {
		sendMessage((ClientTextMessage) message);
	}

	public void sendMessage(ClientTextMessage message) {
		synchronized (requests) {
			if (message.getAnswerType() != null) {
				try {
					ServerTextMessage answer = message.getAnswerType().newInstance();
					answer.setAnswerTo(message);
					message.setAnswer(answer);
					requests.add(message);
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO
					System.out.println("Error while creating an answer object. Did you add a default constructor to type " + message.getAnswerType().getName() + "?");
					e.printStackTrace();
				}
			}

			if (ready) {
				sendNextMessage();
			}
		}
	}

	@Override
	public void writeMessage(ClientMessage<String> message) {
		synchronized (this) {
			if (!message.isSent()) {
				for (String line : message.getData().split("\n")) {
					try
					{
						writeLine(line);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				message.setSent();
			}
		}
	}

	public void sendNextMessage() {
		synchronized (this) {
			ClientMessage<String> nextMessage = requests.peek();
			if (nextMessage != null) {
				if (nextMessage.isSent()) {
					return; // We're still waiting for an answer for this
							// message
				}
				writeMessage(nextMessage);
			}
		}
	}

	public boolean addFilter(NonAnswerServerMessageFilter e) {
		return filters.add(e);
	}

	public boolean removeFilter(Object o) {
		return filters.remove(o);
	}

	public LinkedHashSet<AbstractMessaging> getMsgHandlers() {
		return msgHandlers;
	}

	public void addMsgHandler(AbstractMessaging msgHandler) {
		if (this.getMsgHandlers().add(msgHandler))
		{
			msgHandler.setCommunication(this);
			msgHandler.setApplication(getApplication());
		}
	}

	public void removeMsgHandler(AbstractMessaging msgHandler) {
		if (this.getMsgHandlers().remove(msgHandler))
		{
			msgHandler.setCommunication(null);
		}
	}
}
