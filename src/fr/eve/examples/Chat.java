package fr.eve.examples;

import fr.eve.client.Client;
import fr.eve.client.EventListener;
import fr.rmi.ServiceRMI;

public class Chat {

	public static void main(String[] args) {
		try {
			Client client = new Client("EveClient", 5001, "client.eve", new EventListener() {
				public void eventReceived(String event) {
					System.out.println(event);
				}
			});
			client.getListener().setLogStatus(false);

			System.out.print("Server name : ");
			String name = ServiceRMI.readKeyboard(false);
			System.out.print("Server ip : ");
			String ip = ServiceRMI.readKeyboard(false);
			System.out.print("Server port : ");
			int port = Integer.parseInt(ServiceRMI.readKeyboard(false));

			System.out.println("[Chat opened]");
			for(String event:client.getListener().getEvents())
				System.out.println(event);
			client.connect(name, ip, port);

			String event;
			while(true) {
				if((event = ServiceRMI.readKeyboard(false)).equals("end"))
					break;
				client.notifyEvent(event);
			}
			client.disconnect();
			System.out.println("[Chat ended]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
