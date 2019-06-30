package fr.eve.server;

/** Signal that a user is already connected to the network.
 */
public class AlreadyConnectedException extends Exception {

	private static final long serialVersionUID = -828996993030917419L;
	
    /** Builder of the {@code AlreadyConnectedException} class.
     */
	public AlreadyConnectedException() {
		super();
	}
	
}
