package de.hs.settlers.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;

public class ClientCommunication extends ApplicationHolder {
	AbstractCommunication<?> com;
	BufferedReader reader;
	BufferedWriter writer;
	private boolean active = false;
	private ReadingThread readingThread = null;

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
		if (this.com != null) {
			this.com.setWriter(writer);
		}
	}

	public AbstractCommunication<?> getCommunication() {
		return com;
	}

	public void setCommunication(AbstractCommunication<?> com) {
		com.setApplication(getApplication());
		if (this.com != null) {
			this.com.setActive(false);
			com.setWriter(this.com.getWriter());
		} else {
			com.setWriter(getWriter());
		}
		this.com = com;
		this.com.setActive(true);
	}

	public void setActive(boolean active) {
		if (!this.active && active) {
			this.active = active;
			readingThread = new ReadingThread();
			readingThread.start();
		} else if (this.active && !active) {
			this.active = active;
			readingThread.interrupt();
		}
	}

	private class ReadingThread extends Thread {
		@Override
		public void run() {
			while (active) {
				try {
					String line;
					synchronized (reader) {
						line = reader.readLine();
						if (line != null) {
							line = StringEscapeUtils.unescapeJava(line.trim());
						} else {
							break;
						}
					}
					SettlersApplication.NET.info("< " + line);
					com.readLine(line);
				} catch (IOException e) {
					getApplication().setCurrentState(getApplication().getStates().NOT_CONNECTED);
					break;
				} catch (Exception e)
				{
					// Change current state
					getApplication().setCurrentState(getApplication().getStates().NOT_CONNECTED);
					e.printStackTrace();
					break;
				}
			}
		}
	}
}
