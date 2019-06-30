package fr.eve.client;

/** The {@code EventListener} interface declare the method called by the server when a new event occurs.
 */
public interface EventListener {
	
	
	/** New event happened on the network.
	 * @param event - New event.
	 */
	public void eventReceived(String event);
}
