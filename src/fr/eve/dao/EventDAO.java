package fr.eve.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import fr.eve.server.Event;

/** The {@code EventsDAO} class is used to manage serialization and deserialization of a {@code List<String>} object.
 */
public class EventDAO{

	/**************/
	/** ATRIBUTS **/
	/**************/
	
	private File eventFile;
	
	/*************/
	/** BUILDER **/
	/*************/
	
	/** Builder of the {@code EventsDAO} class.
	 * @param eventFileName - Name of the event file. This file will store all the events on the server.
	 */
	public EventDAO(String eventFileName) {
		eventFile = new File(eventFileName);
	}
	
	/*************/
	/** METHODS **/
	/*************/

	/** Get the class instance.
	 * @return Class instance.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 */
	public Event find() throws IOException, ClassNotFoundException {
		if(eventFile.exists())
			return deserialize(eventFile);
		else
			return create();
	}

	/** Create a new class instance.
	 * @return Class instance.
	 * @throws IOException if an I/O error occurs.
	 */
	protected Event create() throws IOException {
		Event event = null;
		serialize(event, eventFile);
		return event;
	}

	/** Update the class instance.
	 * @param event - New instance.
	 * @throws IOException if an I/O error occurs.
	 */
	public void update(Event event) throws IOException {
		serialize(event, eventFile);
	}

	/*********************/
	/** IMPORT & EXPORT **/
	/*********************/

	/** Serialize the class instance in a file.
	 * @param event - Class instance to serialize.
	 * @param file - File in which serialize the class instance.
	 * @throws IOException if an I/O error occurs.
	 */
	public void serialize(Event event, File file) throws IOException {		  
		FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(event);
		out.close();
		fileOut.close();
	}

	/** Deserialize the class instance from a file.
	 * @param file - File from which deserialize the class instance.
	 * @return Deserialized class instance.
	 * @throws ClassNotFoundException if class of a serialized object cannot be found.
	 * @throws IOException if an I/O error occurs.
	 */
	public Event deserialize(File file) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(file.getAbsolutePath());
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Event event = (Event) in.readObject();
		in.close();
		fileIn.close();
		return event;
	}	
}
