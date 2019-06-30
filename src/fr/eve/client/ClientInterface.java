package fr.eve.client;

import java.rmi.RemoteException;

/** The {@code ClientInterface} interface declare the remote methods that can be called by the server.
 */
public interface ClientInterface extends java.rmi.Remote {
	
	/** Notify the client of an event.
	 * @param event - Event to notify.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public void notifyEvent(String event) throws RemoteException;
}
