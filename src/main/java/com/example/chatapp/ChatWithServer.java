package com.example.chatapp;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatWithServer extends Thread {
    private int ClientNumber;

    private List<Communication> clientsConnectes = new ArrayList<Communication>();
    public static void main(String[] args) {
        new ChatWithServer().start();
    }

    @Override
    public void run(){
        try {
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("The server tries to start ....");
            while(true){
                Socket s = ss.accept(); //Accept client
                ++ClientNumber;

                Communication NewCommunication = new Communication(s,ClientNumber);
                clientsConnectes.add(NewCommunication);
                NewCommunication.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Classe interne
    public class Communication extends Thread{
        private final int ClientNumber;
        private Socket s;
        Communication(Socket s, int ClientNumber){
            this.s = s;
            this.ClientNumber = ClientNumber;
        }
        @Override
        public void run() {
            try {
                InputStream is = null; //octet
                try {
                    is = s.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                InputStreamReader isr = new InputStreamReader(is); //caractere
                BufferedReader br = new BufferedReader(isr); //Chaîne de caractére
                OutputStream os = null;
                try {
                    os = s.getOutputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String Ip = s.getRemoteSocketAddress().toString();
                System.out.println("The client number :" + ClientNumber + " And his IP :" + Ip);
                PrintWriter pw = new PrintWriter(os, true); //caractere
                pw.println("You are the client" + ClientNumber);
                pw.println("Send a message that you want  :D");
                while (true) {
                    String UserRequest = br.readLine();
                    if(UserRequest.contains("=>")){
                        String[] usermessage = UserRequest.split("=>");
                        if(usermessage.length ==2){
                            String msg = usermessage[1];
                            int numeroclient = Integer.parseInt(usermessage[0]);
                            Send(UserRequest,s,numeroclient);
                        }
                    }
                    else {
                        Send(UserRequest,s,-1);
                    }

                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        void Send(String UserRequest, Socket socket,int numeroclient) throws IOException{
            for(Communication client : clientsConnectes) {
                if (client.s != socket) {
                    if(client.ClientNumber==numeroclient || numeroclient==-1){
                        PrintWriter pw = new PrintWriter(client.s.getOutputStream(), true);
                        pw.println(UserRequest);
                    }
                }
            }
        }
    }
}


