package fr.rmi;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

/** The {@code ServiceRMI} class provide usefull methods for RMI servers.
 */
public class ServiceRMI{
	
	/*************/
	/** METHODS **/
	/*************/
	
	/** Get the rmi url.
	 * @param name - Server name.
	 * @param ip - Server ip.
	 * @param port - Server port.
	 * @return Server url.
	 */
	public static String getUrl(String name, String ip, int port){
		return "rmi://" + ip + ":" + port + "/" + name;
	}
	
	/** Wait for the the user to type a text on the keyboard
	 * @param CR - {@code true} if the text should and with carriage return, {@code false} if not.
	 * @return Typed text.
	 * @throws IOException If an I/O error occurs.
	 */
	public static String readKeyboard(boolean CR) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String message = br.readLine();
		if (CR)
			message += "\n";
		return message;
	}
}
