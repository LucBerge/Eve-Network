package fr.eve.rmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/** The {@code RMIClient} class is used to access RMI server interface.
 */
public abstract class RMIClient {

	/** Get the server interface.
	 * @param ip - Server ip.
	 * @param port - Server port.
	 * @param name - Server name.
	 * @return Server interface.
	 * @throws RemoteException if the server is not reachable.
	 * @throws NotBoundException if the server name does not exist
	 */
	public static Remote getInterface(String ip, int port, String name) throws RemoteException, NotBoundException {
		System.setProperty("java.security.policy", "java.security.AllPermission");		
		Registry registry = LocateRegistry.getRegistry(ip, port);
		return registry.lookup(name);
	}
}
