package fr.eve.server;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/** The {@code ServerInterface} interface declare the remote methods that can be called by the clients.
 */
public interface ServerInterface extends java.rmi.Remote {
	
	/** Notify the server for a new user on the network.
	 * @param url - Client url to notify the client for an event.
	 * @param lastEventIndex - The index of the last event that happen for the client.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws AlreadyConnectedException if the user is already connected to the network.
	 * @throws MalformedURLException if the name is not an appropriately formatted URL.
	 * @throws NotBoundException if name is not currently bound.
	 */
	public void connect(String url, int lastEventIndex) throws RemoteException, AlreadyConnectedException, MalformedURLException, NotBoundException;
	
	/** Get the initial file.
	 * @return Initial file.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public File getInitialFile() throws RemoteException;
	

	/** Notify the client of an event.
	 * @param url - Client url.
	 * @param event - Event to notify.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public void notifyEvent(String url, String event) throws RemoteException;

	/** Notify the server for a user disconnection on the network.
	 * @param url - Client url.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public void disconnect(String url) throws RemoteException;
}
