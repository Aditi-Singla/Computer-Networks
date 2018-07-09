import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

class client
{
   public static void main(String args[]) throws Exception
   {
      Scanner inFromUser = new Scanner(System.in);
      DatagramSocket clientSocket = new DatagramSocket();
      String ip = inFromUser.nextLine();
      InetAddress IPAddress = InetAddress.getByName(ip);
      for (int k=100; k<=1000; k += 100){   
         int P = k;
         for (int k1 = 2000; k1<=8000; k1 += 6000){
            int T = k1;
            String fileName = "Output_with_netem_" + T + "_" + P + ".txt";
            Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            
            byte[] sendData = new byte[P];
            long start = System.nanoTime();
            int RC = T;
            for (int i=0;i<50;i++){
               while (true){   
                  try{
                     if (RC == T)
                        start = System.nanoTime();
                     String sentence = i + " " + start + " " + RC + " " + P;

                     sendData = new byte[P];
                     sendData = sentence.getBytes();
                     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                     clientSocket.send(sendPacket);
                     clientSocket.setSoTimeout(3);                  // timeout after 1 second
                     // System.out.println(sentence);
                     byte[] receiveData = new byte[P];
                     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     clientSocket.receive(receivePacket);
                     String receivedData = (new String(receivePacket.getData())).trim();
                     String[] array = receivedData.split("\\s+");
                     RC = Integer.parseInt(array[2]);
                     if (RC == 1){
                        System.out.println("Cumulative RTT : " + (System.nanoTime()-start)/1000 + "us " + T + " " + P + " " + i);
                        outFile.write("Cumulative RTT : " + (System.nanoTime()-start)/1000 + "us\n");
                        RC = T;
                        break;
                     }
                     else
                        RC--;
                  }
                  catch (SocketTimeoutException e)
                  {
                     System.out.println("Hello");
                     outFile.write("\n");
                     RC = T;
                     break;
                  }
               }   
            }
            outFile.close();
         }   
      }   
      clientSocket.close();
   }
}