package fr.eve.server;

/** Signal that a user is already disconnected from the network.
 */
public class AlreadyDisconnectedException extends Exception {

	private static final long serialVersionUID = -4970707290156775910L;

	/** Builder of the {@code AlreadyDisconnectedException} class.
     */
	public AlreadyDisconnectedException() {
		super();
	}
}
