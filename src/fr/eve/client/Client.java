package fr.eve.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

import fr.eve.dao.EventDAO;
import fr.eve.server.AlreadyConnectedException;
import fr.eve.server.Event;
import fr.eve.server.ServerInterface;
import fr.rmi.ServiceRMI;

/** The {@code Client} class is used to manage Eve clients.
 */
public class Client extends Thread{
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private String eventFileName;
	private EventListener eventListener;
	private Event lastEvent;
	private ServerInterface server;
	
	private boolean stop;
	private String ip;

	/*************/
	/** BUILDER **/
	/*************/

	/** Builder of the {@code Client} class.
	 * @param eventFileName - The event file in which store all the events.
	 * @param eventListener - Method called when a new event occurs.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 */
	public Client(String eventFileName, EventListener eventListener) throws ClassNotFoundException, IOException {
		System.setProperty("java.security.policy", "file:file.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		this.eventFileName = eventFileName;
		this.eventListener = eventListener;
		this.lastEvent = new EventDAO(eventFileName).find();
		this.stop = true;
	}

	/*************/
	/** GETTERS **/
	/*************/

	/** Get the event file name.
	 * @return Event file name.
	 */
	public String getEventFileName() {
		return eventFileName;
	}
	
	/** Get the event listener.
	 * @return Event listener.
	 */
	public EventListener getEventListener() {
		return eventListener;
	}

	/** Get the server interface.
	 * @return Server interface.
	 */
	public ServerInterface getServer() {
		return server;
	}
	
	/** Get all the events.
	 * @return Events list.
	 */
	public Event getLastEvent() {
		return lastEvent;
	}

	/********************/
	/** PUBLIC METHODS **/
	/********************/

	/** Connection to a server.
	 * @param name - Server name.
	 * @param ip - Server ip.
	 * @param port - Server port.
	 * @throws NotBoundException if name is not currently bound.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws MalformedURLException if the name is not an appropriately formatted URL.
	 * @throws AlreadyConnectedException if the user is already connected to the network.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public void connect(String name, String ip, int port) throws MalformedURLException, RemoteException, NotBoundException, AlreadyConnectedException, ServerNotActiveException {
		server = (ServerInterface) Naming.lookup(ServiceRMI.getUrl(name, ip, port));	//Create server object
		this.ip = server.connect();														//Notify connection to the server and get the ip
		stop = false;
	}
	
	/** Get the initial file.
	 * @return Initial file.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 */
	public File getInitialFile() throws RemoteException{
		return server.getInitialFile();
	}
	
	/** Notify the server of an event.
	 * @param event - Event to notify.
	 * @throws NotConnectedException - if you are not connected to the network yet. Call the {@code connect} method first.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public void notifyEvent(String event) throws NotConnectedException, RemoteException, ServerNotActiveException {
		if(server == null)
			throw new NotConnectedException();
		
		server.notifyEvent(event);
	}
	
	/** Disconnection from the server.
	 * @throws NotConnectedException if you are not connected to the network yet. Call the {@code connect} method first.
	 * @throws IOException if an I/O error occurs.
	 * @throws NotBoundException if name is not currently bound.
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 */
	public void disconnect() throws NotConnectedException, IOException, NotBoundException, ServerNotActiveException {
		if(server == null)
			throw new NotConnectedException();

		stop = true;
		server.disconnect();			//Notify disconnection to the server
		new EventDAO(eventFileName).update(lastEvent);	//Save the events
	}
	
	/*********/
	/** RUN **/
	/*********/
	
	public void run() {		
		try {
			while(!stop) {
				for(Event event:server.getEvents(lastEvent)){
					lastEvent = event;
					if(!event.getAuthor().equals(this.ip))
						eventListener.eventReceived(event.getEvent());
				}
			}
		} catch (RemoteException | InterruptedException | ServerNotActiveException e) {
			e.printStackTrace();
		}
	}
}
