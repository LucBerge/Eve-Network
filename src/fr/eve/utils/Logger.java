package fr.eve.utils;

/** The {@code Logger} class is used to display log messages.
 */
public class Logger {

	/**************/
	/** ATRIBUTS **/
	/**************/
	
	private boolean logOn;

	/*************/
	/** BUILDER **/
	/*************/
	
	/** Builder of the {@code Logger} class.
	 */
	public Logger() {
		this.logOn  =true;
	}
	
	/***********************/
	/** GETTERS & SETTERS **/
	/***********************/
	
	/** Set the current log status. 
	 * @param logOn - {@code true} to allow log messages to be displayed, {@code false} otherwise.
	 */
	public void setLogOn(boolean logOn) {
		this.logOn = logOn;
	}
	
	/** Return the current log status.
	 * @return {@code true} if the log status is on, {@code false} otherwise.
	 */
	public boolean isLogOn() {
		return this.logOn;
	}

	/*************/
	/** METHODS **/
	/*************/
	
	/** Display a log on the server console.
	 * @param log - Message to display.
	 */
	public void log(String log) {
		if(logOn)
			System.out.println(log);
	}
}
