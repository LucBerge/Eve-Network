package fr.eve.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import fr.eve.dao.EventsDAO;
import fr.eve.example.Example;
import fr.rmi.ServerRMI;

/** The {@code Server} class is used to manage Eve servers.<br><br>
 * This class extends {@code ServerRMI} class.
 */
public class Server extends ServerRMI implements ServerInterface {
	
	public static void main(String args[]) {
		try {	
			if(args.length != 0)
				Example.parse(args);
			else {
				Properties properties = new Properties();
				properties.load(new FileInputStream("server.properties"));
				String name = properties.getProperty("name");
				int port = Integer.parseInt(properties.getProperty("port"));
				String initialFileName = properties.getProperty("initialFileName");
				String eventFileName = properties.getProperty("eventFileName");
				
				Server server = new Server(name, port, initialFileName, eventFileName);
				server.open();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final long serialVersionUID = -7054118432747739424L;
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private List<String> initialFiles;
	private String eventFileName;
	
	private HashMap<String, String> users;
	private List<Event> events;
	
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
		this.eventFileName = eventFileName;
		this.initialFiles = new ArrayList<String>();
		
		users = new HashMap<String, String>();
		events = new EventsDAO(eventFileName).find();

		if(!initialFileName.isEmpty()) {
			File initialFile = new File(initialFileName);
			if(initialFile.exists()) {
				if(initialFile.isFile())
					this.initialFiles.add(initialFileName);
				else {
					this.initialFiles.addAll(getFilesInDirectory(initialFile));
				}
			}
		}
	}

	/*********************/
	/** PRIVATE METHODS **/
	/*********************/
	
	private List<String> getFilesInDirectory(File directory){
		List<String> files = new ArrayList<String>();
		for(File file:directory.listFiles()) {
			if(file.isFile())
				files.add(file.getPath());
			else
				files.addAll(getFilesInDirectory(file));
		}
		return files;
	}
	
	/********************/
	/** PUBLIC METHODS **/
	/********************/
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#connect(java.lang.String, int)
	 */
	public String connect() throws RemoteException, AlreadyConnectedException, ServerNotActiveException {
		String ip = getClientHost();
		synchronized(users) {
			if(users.containsKey(ip))
				throw new AlreadyConnectedException();

			users.put(ip, ip);
		}
		log(ip + " joined the network.");
		return ip;
	}

	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#getInitialFileName()
	 */
	public List<String> getInitialFiles() throws RemoteException{
		return this.initialFiles;
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#getInitialFile()
	 */
	public byte[] getInitialFile(String fileName) throws RemoteException{
		if(!this.initialFiles.contains(fileName))
			return null;
		File initialFile = new File(fileName);
		try {
			byte buffer[] = new byte[(int) initialFile.length()];
			BufferedInputStream input;
			input = new	BufferedInputStream(new FileInputStream(initialFile));
			input.read(buffer,0,buffer.length);
			input.close();
			return(buffer);
		} catch (IOException e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#notifyEvent(java.lang.String, java.lang.String)
	 */
	public void notifyEvent(String e) throws RemoteException, ServerNotActiveException{
		String ip = getClientHost();
		synchronized(events) {
			Event event = new Event(ip, LocalDateTime.now(), e);
			event.setIndex(events.size());
			events.add(event);
		}
		synchronized(users) {
			for(String user:users.keySet()) {
				synchronized(users.get(user)){
					users.get(user).notifyAll();
				}
			}
			log(ip + " added \"" + e + "\".");
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.server.ServerInterface#getEvents(java.lang.String, int)
	 */
	public List<Event> getEvents(Event lastEvent) throws RemoteException, InterruptedException, ServerNotActiveException {
		if(lastEvent == null)
			return events;
		
		if(lastEvent.getIndex()+1 == events.size()) {			
			String ip = getClientHost();
			synchronized(users.get(ip)) {
				users.get(ip).wait();
			}
		}
		synchronized(events) {
			return new ArrayList<Event>(events.subList(lastEvent.getIndex()+1, events.size()));
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.ServerInterface#disconnect(java.lang.String)
	 */
	public void disconnect() throws RemoteException, ServerNotActiveException {
		String ip = getClientHost();
		synchronized(users) {
			synchronized(users.get(ip)) {
				users.get(ip).notifyAll();
			}
			users.remove(ip);
			log(ip + " disconnected.");
			if(users.isEmpty()) {
				try {
					synchronized(events) {
						new EventsDAO(this.eventFileName).update(events);
					}
					log("Events saved.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
