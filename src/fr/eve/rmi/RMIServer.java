package fr.eve.rmi;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import fr.eve.utils.Logger;
import fr.eve.utils.Service;


/** The {@code RMIServer} class is used to manage RMI servers.<br><br>
 * This class extends {@code UnicastRemoteObject} class.
 */
public abstract class RMIServer extends UnicastRemoteObject{
	
	private static final long serialVersionUID = 6863636390287094989L;

	/**************/
	/** ATRIBUTS **/
	/**************/
	
	private String name, ip;
	private int port;
	private Logger logger;
	
	private Registry registry;
	
	/*************/
	/** BUILDER **/
	/*************/
	
	/** Builder of the {@code RMIServer} class.
	 * @param port - Server port.
	 * @param name - Server name. 
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws UnknownHostException if the local host name could not be resolved into an address.
	 */
	public RMIServer(int port, String name) throws UnknownHostException, RemoteException {
		super(port);
		this.port = port;
		this.name = name;
		this.ip = InetAddress.getLocalHost().getHostAddress();
		this.logger = new Logger();
		
		System.setProperty("java.security.policy", "java.security.AllPermission");
	}

	/***********************/
	/** GETTERS & SETTERS **/
	/***********************/

	/** Get the server name.
	 * @return Server name.
	 */
	public String getName() {return name;}

	/** Get the server ip.
	 * @return Server ip.
	 */
	public String getIp() {return ip;}

	/** Get the server port.
	 * @return Server port.
	 */
	public int getPort() {return port;}

	/** Get the server url.
	 * @return Server url.
	 */
	public String getUrl() {return Service.getUrl(name, ip, port);}

	/** Get the server logger.
	 * @return Server logger.
	 */
	public Logger getLogger() {return logger;}
	
	/*************/
	/** METHODS **/
	/*************/
	
	/** Open the server.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws MalformedURLException if the name is not an appropriately formatted URL
	 */
	public void open() throws RemoteException, MalformedURLException {
		registry = LocateRegistry.createRegistry(this.port);
		Naming.rebind(Service.getUrl(name, ip, port), this);
		logger.log("Server opened (Name : " + this.name + ", Ip : " + this.ip + ", Port : " +  this.port + ")");
	}

	/**
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws NotBoundException if name is not currently bound.
	 * @throws AccessException if this registry is local and it denies the caller access to perform this operation.
	 */
	public void close() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(this.name);
        UnicastRemoteObject.unexportObject(this, true);
        logger.log("Server closed");
	}
}
