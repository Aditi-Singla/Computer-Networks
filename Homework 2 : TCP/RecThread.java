import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class RecThread extends Thread{
	/* Has a global list of packets sent and strings (acks) received. */
	private Thread t;
	ArrayList<Packet> packet_q;
	ArrayList<String> receive_q;
	Integer window, bytes_sent;
	DatagramSocket socket_receive;

	RecThread(ArrayList<Packet> packet_q, ArrayList<String> receive_q, Integer window, Integer bytes_sent){
		this.packet_q = packet_q;
		this.receive_q = receive_q;
		this.window = window;
		this.bytes_sent = bytes_sent;
	}

	public void start(){
		if(t == null){
			t = new Thread(this, "RecThread");
			t.start();
		}
	}

	public void run(){
		byte[] buffer = new byte[1000];
		String receive_data;
		try{
			socket_receive = new DatagramSocket(8888);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		DatagramPacket packet_receive = new DatagramPacket(buffer, 1000);
		
		while(true){
			/* Keeps on receiving acks, with timeout being set to 
			timeout of first packet in our list of packets sent.
			Adds the ack received to the list of strings of received acks */
			long time = 1000;
			if(!packet_q.isEmpty()) time = (packet_q.get(0).end_time - System.nanoTime())/1000000;
			if(time < 0) time = 0;

			try{
				socket_receive.setSoTimeout((int)time);
				socket_receive.receive(packet_receive);
				receive_data = new String(packet_receive.getData(), 0, packet_receive.getLength());
				synchronized(receive_q){
					receive_q.add(receive_data);
				}
				System.out.println("received : " + receive_data);
			}
			catch(SocketTimeoutException e){
				synchronized(packet_q) {
					packet_q.clear();
				}
				synchronized(window) {
					window = 1000;
				}
				synchronized(bytes_sent) {
					bytes_sent = 0;
				}
				System.out.println("SocketTimeoutException");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}