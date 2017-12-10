package crtserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author maher
 */
public class CRTserver {
    
    private ServerSocket ss ;
    private Socket clientSocket;
    private ArrayList<Socket> list;
    private int port;
    private Boolean isThere = false;
    
    public CRTserver(int port){
        this.port = port;
        try {
            ss = new ServerSocket(port);
            list  = new ArrayList<>();
            while (true) {                
                clientSocket=ss.accept();
                System.out.println("client : "+clientSocket.getInetAddress());             
                list.add(clientSocket);              
                Thread t = new Thread(new Echo(clientSocket));
                t.start();
                System.out.println("thread just started for "+clientSocket.getInetAddress());      
            }
        }
        catch (Exception e) {
        }
    }
    class Echo extends Thread{
        Socket socket;
        BufferedReader reader;
        PrintWriter writer;
        public Echo(Socket socket){
            this.socket = socket;
        }
        public void run(){
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("reader created");
                //writer = new PrintWriter(socket.getOutputStream(),true);
                System.out.println("writer created");
                System.out.println("Checking users input...");
                while (true) {                    
                    String msg = reader.readLine();
                    //checking msg with multiple ways ...
                    if (msg!=null) {
                        System.out.print(socket.getInetAddress() + " : ");
                        System.out.println(msg); 
                        broadcast(msg,socket);
                        System.out.println("Send back with broadcast ended");
                        msg = null;
                    }
                }
                
            }
            catch (Exception e) {
                System.out.println("erreur");
            }
        }
        public void broadcast(String msg,Socket socket) {
            //System.out.println("sending message to : \n ");
            for (Socket s : list) {
                if(s!=socket){
                try{
                PrintWriter writer = new PrintWriter(s.getOutputStream(),true);
                writer.write(msg+" \n");
                writer.flush();
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("error printwriter");
                }
                System.out.println("Sent back "+msg+" to "+s.getInetAddress()+" Succefully !");
                }
               // System.out.println("@ : "+s.getInetAddress()+"Port : "+s.getLocalPort());
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("select port");
        Scanner sc = new Scanner(System.in);
        int port = sc.nextInt();
        System.out.println("Starting CRT server");
        System.out.println("server port : "+port);
        CRTserver server = new CRTserver(port);
    }
}

