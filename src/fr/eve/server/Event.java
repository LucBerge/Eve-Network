package fr.eve.server;

import java.io.Serializable;
import java.time.LocalDateTime;

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
	
	public Event(String author, LocalDateTime time, String event) {
		this.author = author;
		this.time = time;
		this.event = event;
	}

	/***********************/
	/** GETTERS & SETTERS **/
	/***********************/
	
	public String getAuthor() {
		return author;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String getEvent() {
		return event;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/*************/
	/** GETTERS **/
	/*************/
	
	public String toString() {
		return "Event [index=" + index + ", author=" + author + ", time=" + time + ", event=" + event + "]";
	}
}
