package fr.eve.server;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.List;

/** The {@code ServerInterface} interface declare the remote methods that can be called by the clients.
 */
public interface ServerInterface extends java.rmi.Remote {
	
	/** Notify the server for a new user on the network.
	 * @return The client ip.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws AlreadyConnectedException if the user is already connected to the network.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public String connect() throws RemoteException, AlreadyConnectedException, ServerNotActiveException;

	/** Get the initial file name.
	 * @return Initial file name. {@code null} if there is no initial file.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public String getInitialFileName() throws RemoteException;
	
	/** Get the initial file data.
	 * @return Initial file data. {@code null} if there is no initial file.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public byte[] getInitialFile() throws RemoteException;
	
	/** Notify the client of an event.
	 * @param event - Event to notify.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public void notifyEvent(String event) throws RemoteException, ServerNotActiveException;

	/** Notify the client of an event.
	 * @param lastEvent - Event to notify.
	 * @return List of new events.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws InterruptedException if any thread interrupted the current thread.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public List<Event> getEvents(Event lastEvent) throws RemoteException, InterruptedException, ServerNotActiveException;

	
	/** Notify the server for a user disconnection on the network.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public void disconnect() throws RemoteException, ServerNotActiveException;
}
