import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class SendThread extends Thread{
	private Thread t;
	ArrayList<Packet> packet_q;
	ArrayList<String> receive_q;
	Integer window, bytes_sent;
	Integer count;
	InetAddress receiver_IP;
	int receiver_Port;
	int ack_received;
	DatagramSocket client_skt;
	boolean pkt_drop;

/* SendThread is the object that handles parsing the acks received and 
also sending new packets based on window size and packets dropped. */

	SendThread(ArrayList<Packet> packet_q, ArrayList<String> receive_q, Integer window, Integer bytes_sent, InetAddress receiver_IP, int receiver_Port, boolean x){
		count = 1;
		ack_received = 0;
		this.packet_q = packet_q;
		this.receive_q = receive_q;
		this.window = window;
		this.bytes_sent = bytes_sent;
		this.receiver_IP = receiver_IP;
		this.receiver_Port = receiver_Port;
		this.pkt_drop = x;
		try
		{
			client_skt = new DatagramSocket(receiver_Port);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// SENDING the first packet ->
		Packet p = new Packet(System.nanoTime() + (long)(Math.pow(10,9)),0,window,0);
		String str = p.to_String();
		DatagramPacket pkt = new DatagramPacket(str.getBytes(),str.length(),receiver_IP,receiver_Port);
		synchronized(packet_q)
		{
			packet_q.add(p);
		}
		synchronized(bytes_sent)
		{
			bytes_sent = window;
		}
		try
		{
			client_skt.send(pkt);
		}
		catch(Exception e)
		{
			System.out.println("Error in client while sending \n");
		}
		System.out.println("!!!!bytes_sent " + bytes_sent);
	}

	public void start(){
		// Starting thread :
		if(t == null){
			t = new Thread(this, "SendThread");
			t.start();
		}
		bytes_sent = 1000;
	}

	public void run(){
	try {
		// DatagramPacket packet_receive;
		while (true && ack_received < 100000){
			Thread.sleep(50);
				System.out.println("Window " + window + " bytes_sent " + bytes_sent + " count " + count + " ack_received " + ack_received);
				System.out.println("RQ size " + receive_q.size());
				for(String i : receive_q) System.out.println("\t"+i);
				System.out.println("PQ size " + receive_q.size());
				for(Packet i : packet_q) System.out.println("\t"+i.to_String());

			// Parsing acks received ->
			while(!receive_q.isEmpty()){
				// process the receiving queue
				String receive_data = receive_q.get(0);
				String[] data = receive_data.split(" ");
				int id = Integer.parseInt(data[0]);
				int ack = Integer.parseInt(data[1]);
				/* Delete the packet with id same as the one received, 
				also, delete any packet whose data has been acknowledged 
				Update bytes_sent, window with every ack being parsed. */

				for(int i = packet_q.size()-1; i>=0; --i){
					Packet pkt = packet_q.get(i);
					if((pkt.start_num + pkt.length <= ack) || (pkt.id == id)){
						synchronized(bytes_sent){
							bytes_sent -= pkt.length;
						}
						synchronized(packet_q){
							packet_q.remove(i);
						}
						ack_received = Math.max(ack_received, ack);
					}
				}

				synchronized(receive_q){
					receive_q.remove(0);
				}
				synchronized(window){
					window += 1000*1000/window;
				}
			}
			System.out.println("Window " + window + " bytes_sent " + bytes_sent + " count " + count);
			/* send packets 
			With every packet being sent, update the value of bytes_sent and the counter of #packets
			and add the packet sent to the array of packets */
			int i = 1000;
			int pkt_start = ack_received;
			String str;
			while ((window - bytes_sent) >= 1000)
			{
				// Thread.sleep(20);
				Packet p1 = new Packet(System.nanoTime() + (long)(Math.pow(10,9)),pkt_start,i,count);
				str = p1.to_String();
				pkt_start += i;
				DatagramPacket pkt1 = new DatagramPacket(str.getBytes(),str.length(),receiver_IP,receiver_Port);
				packet_q.add(p1);
				try
				{
					/* Using random number generator to induce losses : */
					if(Math.random() > 0.05 || !pkt_drop){
						client_skt.send(pkt1);
					}
					count += 1;
				}
				catch(Exception e)
				{
					System.out.println("Error in client while sending \n");
				}

				synchronized(bytes_sent){
					bytes_sent += i;
				}
				System.out.println("sent pkt "+str+" bytes_sent "+bytes_sent);
			}
			if (window - bytes_sent > 0)
			{
				i = window - bytes_sent;
				Packet p1 = new Packet(System.nanoTime() + (long)(Math.pow(10,9)),pkt_start,i,count);
				str = p1.to_String();
				// count += 1;
				DatagramPacket pkt1 = new DatagramPacket(str.getBytes(),str.length(),receiver_IP,receiver_Port);
				packet_q.add(p1);
				try
				{
					if(Math.random() > 0.05 || !pkt_drop){
						client_skt.send(pkt1);
					}
					count += 1;
				}
				catch(Exception e)
				{
					System.out.println("Error in client while sending \n");
				}

				synchronized(bytes_sent){
					bytes_sent += i;
				}
				System.out.println("sent pkt "+str+" bytes_sent "+bytes_sent);
			}
		}
	}
	catch (Exception e) { e.printStackTrace(); }
	}
}
