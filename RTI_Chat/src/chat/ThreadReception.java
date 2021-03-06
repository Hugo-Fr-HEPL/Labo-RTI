package chat;

import java.net.*;
import java.io.*;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;


public class ThreadReception extends Thread
{
    public static int POST_QUESTION = 1;
    public static int ANSWER_QUESTION = -1;
    public static int POST_EVENT = 0;
    public static int EVENT_JOIN = 1;
    public static int EVENT_LEAVE = 2;

    private String nom;
    private MulticastSocket socketGroupe;
    private JList<String> LMsgRecus;


    public ThreadReception (String n, MulticastSocket ms, JList<String> l) {
        nom = n; socketGroupe = ms; LMsgRecus = l;
    }

    public void run() {
        DefaultListModel<String> model = (DefaultListModel<String>)LMsgRecus.getModel();
        while(true) {
            try {
                byte[] buf = new byte[1000];
                DatagramPacket dtg = new DatagramPacket(buf, buf.length);
                socketGroupe.receive(dtg);

                String msg = new String (buf).trim();
                System.out.println("test "+ msg);
                int num = Integer.parseInt(msg.substring(msg.indexOf("<")+1, msg.indexOf(">")));
                int numPlus = 0;
                if(num == POST_EVENT)
                    numPlus = Integer.parseInt(msg.substring(msg.indexOf("[")+1, msg.indexOf("]")));

                String msgFinal = null;
                if(num == POST_EVENT) {
                    if(numPlus == EVENT_JOIN) {
                        msgFinal = msg.substring(msg.indexOf("{")+1, msg.indexOf("}")) + " joined the group";
                    } else if (numPlus == EVENT_LEAVE) {
                        msgFinal = msg.substring(msg.indexOf("{")+1, msg.indexOf("}")) + " left the group";
                    } else {
                        msgFinal = msg.substring(msg.indexOf("{")+1, msg.indexOf("}")) +"'s event: "+ msg.substring(msg.indexOf("}")+1);
                        JOptionPane.showMessageDialog(null, msg.substring(msg.indexOf("{")+1, msg.indexOf("}")) +": "+ msg.substring(msg.indexOf("}")+1), "Event", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if(num >= POST_QUESTION) {
                        msgFinal = msg + " ?";
                    } else if(num <= ANSWER_QUESTION) {
                        msgFinal = "    <"+ num * -1 + msg.substring(msg.indexOf(">")) + " !";
                    }
                }
                model.addElement(msgFinal);
            }
            catch (IOException e) {
                System.out.println("Erreur dans le thread :-( :" + e.getMessage());
                break;
            }
        }
    }
} 
