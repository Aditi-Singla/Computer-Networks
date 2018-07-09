import java.io.*;
import java.net.*;

class echo
{
   public static void main(String args[]) throws Exception
   {
      DatagramSocket serverSocket = new DatagramSocket(9876);
      int RC = 0;
      int P = 0;
      while(true)
      {
         byte[] receiveData = new byte[1000];
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         String receivedSentence = (new String(receivePacket.getData())).trim();
         InetAddress IPAddress = receivePacket.getAddress();
         int port = receivePacket.getPort();
         
         String[] array = receivedSentence.split("\\s+");
         RC = Integer.parseInt(array[2]);
         P = Integer.parseInt(array[3]);
         String returnData = array[0] + " " + array[1] + " " + Integer.toString(RC-1) + " " + P;
         byte[] sendData = new byte[P];
         sendData = returnData.getBytes();
         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
         serverSocket.send(sendPacket);
      }
   }
}