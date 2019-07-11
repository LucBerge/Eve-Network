package fr.eve.example;

import java.io.File;

import fr.eve.client.Client;
import fr.eve.client.EventListener;
import fr.rmi.ServiceRMI;

public class Example {

	public static void parse(String[] args) throws Exception{
		if(args.length == 1) {
			switch(args[0]) {
			case("-chat"):
				chat();
			break;
			case("-file"):
				file();
			break;
			default:
				help();
				break;
			}
		}
		else
			help();
	}

	private static void help() {
		System.out.println("Use the following options :\n-chat : A simple chat.");
	}

	private static Client getClient()  throws Exception{
		Client client = new Client("client.eve", new EventListener() {
			public void eventReceived(String event) {
				System.out.println(event);
			}
		});

		System.out.print("Server name : ");
		String name = ServiceRMI.readKeyboard(false);
		System.out.print("Server ip : ");
		String ip = ServiceRMI.readKeyboard(false);
		System.out.print("Server port : ");
		int port = Integer.parseInt(ServiceRMI.readKeyboard(false));

		client.connect(name, ip, port);
		return client;
	}

	public static void chat() throws Exception {
		Client client = getClient();
		System.out.println("------------------");
		client.join();

		String event;
		while(true) {
			if((event = ServiceRMI.readKeyboard(false)).equals("end"))
				break;
			client.notifyEvent(event);
		}
		client.disconnect();
	}

	public static void file() throws Exception {
		Client client = getClient();
		client.join();
		File file = client.getInitialFile(".");
		client.disconnect();

		if(file == null)
			System.out.println("There is no initial file to download.");
		else if(file.isFile())
			System.out.println("Initial file downloaded : " + file.getName());
		else
			System.out.println("Initial directory downloaded : " + file.getName());
	}
}
