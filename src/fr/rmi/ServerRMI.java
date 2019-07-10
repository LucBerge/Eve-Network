package fr.rmi;

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


/** The {@code ServerRMI} class is used to manage RMI servers.<br><br>
 * This class extends {@code UnicastRemoteObject} class.
 */
public abstract class ServerRMI extends UnicastRemoteObject{
	
	private static final long serialVersionUID = 6863636390287094989L;

	/**************/
	/** ATRIBUTS **/
	/**************/
	
	private String name, ip;
	private int port;
	private Registry registry;
	private boolean logStatus = true;
	
	/*************/
	/** BUILDER **/
	/*************/
	
	/** Builder of the {@code ServerRMI} class.
	 * @param name - Server name.
	 * @param port - Server port.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws UnknownHostException if the local host name could not be resolved into an address.
	 */
	public ServerRMI(String name, int port) throws UnknownHostException, RemoteException {
		System.setProperty("java.security.policy", "file:file.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		this.name = name;
		this.ip = InetAddress.getLocalHost().getHostAddress();
		this.port = port;
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
	public String getUrl() {return ServiceRMI.getUrl(name, ip, port);}

	/** Get the server log status.
	 * @return Server log status. {@code true} if logs are enabled, {@code false} if not.
	 */
	public boolean getLogStatus() {return logStatus;}
	
	/** Set the server log status.
	 * @param logStatus - {@code true} to enable logs, {@code false} to disable it.
	 */
	public void setLogStatus(boolean logStatus) {this.logStatus = logStatus;}
	
	/*************/
	/** METHODS **/
	/*************/
	
	/** Open the server.
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws MalformedURLException if the name is not an appropriately formatted URL
	 */
	public void open() throws RemoteException, MalformedURLException {
		registry = LocateRegistry.createRegistry(this.port);
		Naming.rebind(ServiceRMI.getUrl(name, ip, port), this);
		log("Server opened (Name : " + this.name + ", Ip : " + this.ip + ", Port : " +  this.port + ")");
	}

	/**
	 * @throws RemoteException if the registry could not be exported or contacted.
	 * @throws NotBoundException if name is not currently bound.
	 * @throws AccessException if this registry is local and it denies the caller access to perform this operation.
	 */
	public void close() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(this.name);
        UnicastRemoteObject.unexportObject(this, true);
        log("Server closed");
	}

	/*********/
	/** LOG **/
	/*********/
	
	/** Display a log on the server console.
	 * @param log - Message to display.
	 */
	public void log(String log) {
		if(logStatus)
			System.out.println(log);
	}
}
