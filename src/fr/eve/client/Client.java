package fr.eve.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.List;

import fr.eve.dao.EventDAO;
import fr.eve.rmi.RMIClient;
import fr.eve.server.AlreadyConnectedException;
import fr.eve.server.AlreadyDisconnectedException;
import fr.eve.server.Event;
import fr.eve.server.ServerInterface;

/** The {@code Client} class is used to manage Eve clients.
 */
public class Client{
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private String eventFileName;
	private EventListener eventListener;
	private Event lastEvent;
	private ServerInterface server;
	
	private boolean connected;
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
		this.eventFileName = eventFileName;
		this.eventListener = eventListener;
		this.lastEvent = new EventDAO(eventFileName).find();
		this.connected = false;
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
		server = (ServerInterface) RMIClient.getInterface(ip, port, name);	//Get server object
	}
	
	/** Get the initial file.
	 * @param directoryName - Destination folder.
	 * @return Initial file.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws NotConnectedException if you are not connected to the network yet. Call the {@code connect} method first.
	 */
	public File getInitialFile(String directoryName) throws RemoteException, NotConnectedException{
		if(server == null)
			throw new NotConnectedException();
		
		File directory = new File(directoryName);
		if(!directory.exists())
			directory.mkdirs();
		
		List<String> fileNames = server.getInitialFiles();
		if(fileNames.isEmpty())
			return null;
		
		try {
			for(String fileName:fileNames) {
				byte[] data = server.getInitialFile(fileName);
				File file = new File(directoryName, fileName);
				if(file.exists())
					file.delete();
				file.getParentFile().mkdirs();
				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
				output.write(data,0,data.length);
				output.flush();
				output.close();
				file.createNewFile();
			}
		} catch (IOException e) {
			return null;
		}

		File parent = new File(fileNames.get(0));
		while(parent.getParentFile() != null) {
			parent = parent.getParentFile();
		}
		return new File(directoryName, parent.getPath());
	}
	
	/** Notify the server of an event.
	 * @param event - Event to notify.
	 * @throws NotConnectedException if you are not connected to the network yet. Call the {@code connect} method first.
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
	 * @throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
	 * @throws AlreadyDisconnectedException if the user is already disconnected from the network.
	 */
	public void disconnect() throws NotConnectedException, IOException, ServerNotActiveException, AlreadyDisconnectedException {
		if(server == null)
			throw new NotConnectedException();

		connected = false;
		server.disconnect();			//Notify disconnection to the server
		server = null;
		new EventDAO(eventFileName).update(lastEvent);	//Save the events
	}
	
	public void join() throws RemoteException, AlreadyConnectedException, ServerNotActiveException {
		this.ip = this.server.connect();
		connected = true;
		Thread listener = new Thread(new Runnable() {
			public void run() {
				while(connected) {
					try {
						for(Event event:server.getEvents(lastEvent)){
							lastEvent = event;
							if(!event.getAuthor().equals(ip))
								eventListener.eventReceived(event.getEvent());
						}
					} catch (RemoteException | InterruptedException | ServerNotActiveException e) {
						e.printStackTrace();
					}
				}
			}
		});
		listener.start();
	}
}
