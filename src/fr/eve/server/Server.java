package fr.eve.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import fr.eve.client.ClientInterface;
import fr.eve.dao.EventsDAO;
import fr.rmi.ServerRMI;

/** The {@code Server} class is used to manage Eve servers.<br><br>
 * This class extends {@code ServerRMI} class.
 */
public class Server extends ServerRMI implements ServerInterface {
	
	public static void main(String args[]) {
		try {	
			Properties properties = new Properties();
			properties.load(new FileInputStream("eve.server.properties"));
			String name = properties.getProperty("name");
			int port = Integer.parseInt(properties.getProperty("port"));
			String initialFileName = properties.getProperty("initialFileName");
			String eventFileName = properties.getProperty("eventFileName");
			
			Server server = new Server(name, port, initialFileName, eventFileName);
			server.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final long serialVersionUID = -7054118432747739424L;
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private String initialFileName;
	private String eventFileName;
	
	private HashMap<String, ClientInterface> users;
	private List<String> events;
	
	/*************/
	/** BUILDER **/
	/*************/

	/** Builder of the {@code Server} class.
	 * @param name - Server name.
	 * @param port - Server port.
	 * @param initialFileName - The initial file that all the clients should use, {@code null} if there is no initial file.
	 * @param eventFileName - The event file in which store all the events.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 */
	public Server(String name, int port, String initialFileName, String eventFileName) throws ClassNotFoundException, IOException {
		super(name, port);
		this.initialFileName = initialFileName;
		this.eventFileName = eventFileName;
		
		users = new HashMap<String, ClientInterface>();
		events = new EventsDAO(eventFileName).find();
	}

	/********************/
	/** PUBLIC METHODS **/
	/********************/
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#connect(java.lang.String, int)
	 */
	public void connect(String url, int lastEventIndex) throws RemoteException, AlreadyConnectedException, MalformedURLException, NotBoundException {
		if(users.containsKey(url))
			throw new AlreadyConnectedException();
		
		synchronized(users) {
			users.put(url, (ClientInterface) Naming.lookup(url));
			
			if (lastEventIndex < events.size()) {
				log(url + " is now connected to the network with " + (events.size() - lastEventIndex) + " events late.");
				for(String event:events.subList(lastEventIndex, events.size()))
					users.get(url).notifyEvent(event);
			}
			else if (lastEventIndex > events.size()) {
				//error = Client in advance on the server
				log(url + " is now connected to the network but in advance on the server.");
			}
			else {
				log(url + " is now connected to the network and up to date.");
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#getInitialFile()
	 */
	public File getInitialFile() throws RemoteException{
		if(initialFileName == null)
			return null;
		else
			return new File(initialFileName);
	}

	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#notifyEvent(java.lang.String, java.lang.String)
	 */
	public void notifyEvent(String url, String event) throws RemoteException{
		synchronized(events){
			events.add(event);
		}
		for(String user:users.keySet()) {
			if(!user.equals(url)) {
				users.get(user).notifyEvent(event);
				log(event + " sent to " + user + ".");
			}
		}
		log(url + " added \"" + event + "\".");
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#disconnect(java.lang.String)
	 */
	public void disconnect(String url) throws RemoteException {
		synchronized(users) {
			users.remove(url);
			log(url + " disconnected.");
			if(users.isEmpty())
				try {
					synchronized(events) {
						new EventsDAO(this.eventFileName).update(events);
						log("Events saved.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
