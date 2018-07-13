import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class Data{
	/* A class to efficiently store the bytes being received here */
	int start;
	int end;
	Data(int a, int b){
		start = a;
		end = b;
	}
};

class Receiver{

	public static void main(String[] args) throws Exception	{
		int last_receive = -1;
		// int port = Integer.parseInt(args[0]);
		Receiver r = new Receiver();
		int port = Integer.parseInt(args[0]);
		System.out.println("Port is " + port);
		DatagramSocket socket_receive = new DatagramSocket(port);

		DatagramSocket socket_send = new DatagramSocket(8888);
		LinkedList<Data> received_packets = new LinkedList<Data>();

		byte[] buff = new byte[1000];
		String data_receive, data_send;
		DatagramPacket packet_send;
		DatagramPacket packet_receive = new DatagramPacket(buff, 1000);

		while(true){
			/* Simply receives packets, and adds them to the list of Data objects.
			Also, merges the intervals of bytes received to calculate the new ack value */
			socket_receive.receive(packet_receive);
			data_receive = new String(packet_receive.getData(), 0, packet_receive.getLength());
			String[] s = data_receive.split(" ");
			int start = Integer.parseInt(s[0]);
			int end = Integer.parseInt(s[1]) + start - 1;
			int id = Integer.parseInt(s[2]);

			System.out.println("Start " + start + " end " + end + " id " + id);

			int old_ack = last_receive;

			if(start <= last_receive + 1) last_receive = Math.max(end,last_receive);
			else{
				Data d = new Data(start, end);
				received_packets.add(d);
			}
			boolean done = false;
			while(!done){
				boolean found = false;
				for(int i=0; i<received_packets.size(); ++i){
					Data d = received_packets.get(i);
					if(d.start <= last_receive + 1){
						found = true;
						last_receive = Math.max(d.end,last_receive);
						received_packets.remove(i);
					}
				}
				if(!found) done = true;
			}
			System.out.println("ACK " + (last_receive+1));

			if (old_ack > last_receive)
				System.out.println("ERROR!!! NEW < OLD!!!!");

			data_send = id + " " + (last_receive+1);
			packet_send = new DatagramPacket(data_send.getBytes(), data_send.length(), packet_receive.getAddress(), 8888);
			socket_send.send(packet_send);
			/* Sends back an ack for each packet received */
		}
	}
}