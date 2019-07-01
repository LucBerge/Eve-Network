package fr.eve.server;

import java.io.Serializable;
import java.time.LocalDateTime;

/** The {@code Event} class represent an event.<br><br>
 * An event is characterized by an index, an author, a date/time and a string. The string attribut is generaly a json format.
 */
public class Event implements Serializable{

	private static final long serialVersionUID = 7382591043788737998L;
	
	/***************/
	/** ATTRIBUTS **/
	/***************/
	
	private String author;
	private LocalDateTime time;
	private String event;
	private int index;

	/*************/
	/** BUILDER **/
	/*************/
	
	/** Builder of the {@code Server} class.
	 * @param author - Author of the event.
	 * @param time - Time of the event.
	 * @param event - Event as a string format.
	 */
	public Event(String author, LocalDateTime time, String event) {
		this.author = author;
		this.time = time;
		this.event = event;
	}

	/***********************/
	/** GETTERS & SETTERS **/
	/***********************/
	
	/** Get the author.
	 * @return Author of the event.
	 */
	public String getAuthor() {
		return author;
	}

	/** Get the date/time.
	 * @return - Date/time of the event.
	 */
	public LocalDateTime getDateTime() {
		return time;
	}

	/** Get the event as a string format.
	 * @return - Event as a string format.
	 */
	public String getEvent() {
		return event;
	}
	
	/** Get the index.
	 * @return - Index of the event.
	 */
	public int getIndex() {
		return index;
	}

	/** Set the index of the event.
	 * @param index - Index of the event.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/*************/
	/** GETTERS **/
	/*************/
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Event [index=" + index + ", author=" + author + ", time=" + time + ", event=" + event + "]";
	}
}
