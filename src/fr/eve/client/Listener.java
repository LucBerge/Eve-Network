package fr.eve.client;


import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import fr.eve.dao.EventsDAO;
import fr.rmi.ServerRMI;

/** The {@code Listener} class is used to manage Eve client.<br><br>
 * This class extends {@code ServerRMI} class.
 */
public class Listener extends ServerRMI implements ClientInterface{

	private static final long serialVersionUID = 5956884117767760230L;
	
	/***************/
	/** ATTRIBUTS **/
	/***************/

	private EventListener eventListener;
	private List<String> events;

	/*************/
	/** BUILDER **/
	/*************/
	
	/**
	 * @param name - Client name.
	 * @param port - Client port.
	 * @param eventFileName - The event file in which store all the events.
	 * @param eventListener - Method called when a new event occurs.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 */
	public Listener(String name, int port, String eventFileName, EventListener eventListener) throws ClassNotFoundException, IOException {
		super(name, port);
		this.eventListener = eventListener;
		this.events = new EventsDAO(eventFileName).find();
	}

	/*************/
	/** GETTERS **/
	/*************/
	
	/** Get all the events.
	 * @return Events list.
	 */
	public List<String> getEvents() {
		return events;
	}
	
	/********************/
	/** PUBLIC METHODS **/
	/********************/

	/** Add an event to the list.
	 * @param event - New event.
	 */
	public void addEvent(String event) {
		synchronized(events) {
			events.add(event);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.eve.ClientInterface#notifyEvent(java.lang.String)
	 */
	public void notifyEvent(String event) throws RemoteException{
		addEvent(event);
		eventListener.eventReceived(event);
	}
}
