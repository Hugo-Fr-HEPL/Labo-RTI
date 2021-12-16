package chat;

import java.net.*;
import java.awt.*; // Remove <String> from List when using gui
import java.io.*;
import java.util.List;


public class ThreadReception extends Thread
{
    private String nom;
    private MulticastSocket socketGroupe;
    private List<String> LMsgRecus;

    public ThreadReception (String n, MulticastSocket ms, List<String> l) {
        nom = n; socketGroupe = ms; LMsgRecus = l;
    }

    public void run() {
        while(true) {
            try {
                byte[] buf = new byte[1000];
                DatagramPacket dtg = new DatagramPacket(buf, buf.length);
                socketGroupe.receive(dtg);
                LMsgRecus.add(new String (buf).trim());
            }
            catch (IOException e) {
                System.out.println("Erreur dans le thread :-( :" + e.getMessage());
                break;
            }
        }
    }
} 
