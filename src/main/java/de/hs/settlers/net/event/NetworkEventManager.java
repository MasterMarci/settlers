package de.hs.settlers.net.event;

import de.hs.settlers.net.event.NetworkErrorListener.UnexpectedError;
import de.hs.settlers.net.message.ClientMessage;
import de.hs.settlers.net.message.ServerMessage;

import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkEventManager {
	private CopyOnWriteArrayList<NetworkEventListener<?>> successListeners = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<NetworkErrorListener<?>> errorListeners = new CopyOnWriteArrayList<>();

	/**
	 * Registers a success event listener
	 * 
	 * @param listener
	 *            the listener to register
	 */
	public void registerListener(NetworkEventListener<?> listener) {
		synchronized (successListeners) {
			successListeners.add(listener);
		}
	}

	/**
	 * Removes the given listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void unregisterListener(NetworkEventListener<?> listener) {
		synchronized (successListeners) {
			successListeners.remove(listener);
		}
	}

	/**
	 * Registers an error event listener
	 * 
	 * @param listener
	 *            the listener to register
	 */
	public void registerListener(NetworkErrorListener<?> listener) {
		synchronized (errorListeners) {
			errorListeners.add(listener);
		}
	}

	/**
	 * Removes the given listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void unregisterListener(NetworkErrorListener<?> listener) {
		synchronized (errorListeners) {
			errorListeners.remove(listener);
		}
	}

	/**
	 * Redirects a success message to the listeners
	 * 
	 * @param message
	 *            the message to deliver
	 */
	public void callMessage(ServerMessage message) {
		Class<?> type = message.getClass();

		synchronized (successListeners) {
			for (NetworkEventListener<?> listener : successListeners) {
				if (listener.getType().isAssignableFrom(type)) {
					try {
						listener.handle(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Redirects an error message to the listeners
	 * 
	 * @param message
	 *            the message that was responsible for the error, null if
	 *            unexpected
	 * @param error
	 *            the error string from the server
	 */
	public void callError(ClientMessage message, String error) {
		Class<?> type = message != null ? message.getClass() : UnexpectedError.class;

		synchronized (errorListeners) {
			for (NetworkErrorListener<?> listener : errorListeners) {
				if (listener.getType().isAssignableFrom(type)) {
					try {
						listener.handle(message, error);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
