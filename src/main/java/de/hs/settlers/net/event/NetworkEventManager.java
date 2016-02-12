package de.hs.settlers.net.event;

import org.spout.api.util.list.concurrent.ConcurrentList;

import de.hs.settlers.net.event.NetworkErrorListener.UnexpectedError;
import de.hs.settlers.net.message.ClientMessage;
import de.hs.settlers.net.message.ServerMessage;

public class NetworkEventManager {
	private ConcurrentList<NetworkEventListener<?>> successListeners = new ConcurrentList<>();
	private ConcurrentList<NetworkErrorListener<?>> errorListeners = new ConcurrentList<>();

	/**
	 * Registers a success event listener
	 * 
	 * @param listener
	 *            the listener to register
	 */
	public void registerListener(NetworkEventListener<?> listener) {
		synchronized (successListeners) {
			successListeners.addDelayed(listener);
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
			successListeners.removeDelayed(listener);
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
			errorListeners.addDelayed(listener);
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
			errorListeners.removeDelayed(listener);
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
			successListeners.sync();
			for (NetworkEventListener<?> listener : successListeners) {
				if (listener.getType().isAssignableFrom(type)) {
					try {
						listener.handle(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			successListeners.sync();
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
			errorListeners.sync();
			for (NetworkErrorListener<?> listener : errorListeners) {
				if (listener.getType().isAssignableFrom(type)) {
					try {
						listener.handle(message, error);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			errorListeners.sync();
		}
	}
}
