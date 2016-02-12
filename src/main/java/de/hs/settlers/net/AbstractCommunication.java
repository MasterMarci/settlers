package de.hs.settlers.net;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.event.NetworkEventManager;
import de.hs.settlers.net.message.ClientMessage;

public abstract class AbstractCommunication<PARSETYPE> extends ApplicationHolder {
	private BufferedWriter writer;

	private boolean active = false;
	private NetworkEventManager eventManager = new NetworkEventManager();

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (!active && this.active) { // deactivation
			synchronized (writer) {
				active = false;
			}
		} else if (active && !this.active) {
			active = true;
		}
		this.active = active;
	}

	protected abstract void readLine(String line);

	protected void writeLine(String line) throws IOException {
		SettlersApplication.NET.info("> " + line);
		try {
			writer.write(StringEscapeUtils.escapeJava(line) + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void sendMessage(ClientMessage<PARSETYPE> message);

	public abstract void writeMessage(ClientMessage<PARSETYPE> message);

	public NetworkEventManager getEventManager() {
		return eventManager;
	}
}
