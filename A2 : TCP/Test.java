import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class Test{
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		byte[] buff = new byte[1000];
		DatagramSocket socket_send = new DatagramSocket(7777);
		InetAddress ip = InetAddress.getByName(args[0]);
		while(true){
			System.out.println("Start");
			String start = in.next();
			System.out.println("Size");
			String size = in.next();
			System.out.println("Id");
			String id = in.next();
			String data_send = start + " " + size + " " + id;
			DatagramPacket packet_send = new DatagramPacket(data_send.getBytes(), data_send.length(), ip, 7777);
			socket_send.send(packet_send);
		}
	}
}