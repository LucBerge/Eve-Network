package fr.eve.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import fr.eve.dao.EventsDAO;
import fr.eve.server.AlreadyConnectedException;
import fr.eve.server.ServerInterface;
import fr.rmi.ServiceRMI;

/** The {@code Client} class is used to manage Eve clients.
 */
public class Client {
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private String eventFileName;
	private Listener listener;
	private ServerInterface server;

	/*************/
	/** BUILDER **/
	/*************/

	/** Builder of the {@code Client} class.
	 * @param name - Client name.
	 * @param port - Client port.
	 * @param eventFileName - The event file in which store all the events.
	 * @param eventListener - Method called when a new event occurs.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 */
	public Client(String name, int port, String eventFileName, EventListener eventListener) throws ClassNotFoundException, IOException {
		System.setProperty("java.security.policy", "file:file.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		this.eventFileName = eventFileName;
		this.listener = new Listener(name, port, eventFileName, eventListener);
	}

	/*************/
	/** GETTERS **/
	/*************/

	/** Get the client listener.
	 * @return Client listener.
	 */
	public Listener getListener() {
		return listener;
	}

	/** Get the server interface.
	 * @return Server interface.
	 */
	public ServerInterface getServer() {
		return server;
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
	 */
	public void connect(String name, String ip, int port) throws MalformedURLException, RemoteException, NotBoundException, AlreadyConnectedException {
		this.server = (ServerInterface) Naming.lookup(ServiceRMI.getUrl(name, ip, port));	//Create server object
		listener.open();																	//Open the listener
		this.server.connect(listener.getUrl(), listener.getEvents().size());				//Notify connection to the server
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
	 */
	public void notifyEvent(String event) throws NotConnectedException, RemoteException {
		if(server == null)
			throw new NotConnectedException();
		
		listener.addEvent(event);			//Add event localy
		server.notifyEvent(listener.getUrl(), event);			//Notify others
	}
	
	/** Disconnection from the server.
	 * @throws NotConnectedException if you are not connected to the network yet. Call the {@code connect} method first.
	 * @throws IOException if an I/O error occurs.
	 * @throws NotBoundException if name is not currently bound.
	 */
	public void disconnect() throws NotConnectedException, IOException, NotBoundException {
		if(server == null)
			throw new NotConnectedException();
		
		server.disconnect(listener.getUrl());			//Notify disconnection to the server
		listener.close();								//Close the listener
		new EventsDAO(eventFileName).update(listener.getEvents());	//Save the events
		server = null;
	}
}
