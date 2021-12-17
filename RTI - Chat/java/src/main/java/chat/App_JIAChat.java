package chat;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.*; 

/*
Obtenir une adresse de classe D précise et un port PORT_CHAT précis
    Aura été obtenu en s'adressant en TCP à un serveur Serv_IAChat

LOGIN_GROUP  quelqu'un veut se joindre au groupe 
paramètres : nom et digest "salé" du mot de passe 
Réponse : oui  + envoi de l'adresse et du port PORT_CHAT à utiliser ce jour;  
        ou non

En cas de succès, l'agent pourra alors réellement participer au chat 
POST_QUESTION
ANSWER_QUESTION
POST_EVENT - Signale un fait mais n'attend pas de réponse (Tag supplémentaire pour différencier les events)
*/

public class App_JIAChat 
{
    static private String nomCli;
    static private InetAddress adresseGroupe;
    static MulticastSocket socketGroupe;
    static ThreadReception thr;

    static private int port = 5001;
    static private String address = "234.5.5.9";


    public static void main( String[] args ) throws InterruptedException
    {
        AskTCP();
        Connect();

        TimeUnit.SECONDS.sleep(1);
        System.out.println("oui 1 " + LMsgRecus);

        BEnvoyerActionPerformed(null);
        TimeUnit.SECONDS.sleep(1);
        System.out.println("oui 2 " + LMsgRecus);

        BArreterActionPerformed(null);
    }

    /*
    private void initComponents() {
        BDemarrer = new java.awt.Button();
        ZTNom = new java.awt.TextField();
        BArreter = new java.awt.Button();
        ZTMessage = new java.awt.TextField();
        BEnvoyer = new java.awt.Button();
        LMsgRecus = new java.awt.List();
        ...
        setLayout(new java.awt.GridLayout(2, 1));
        setTitle("Client multicast");
        ... // Instructions pour le GUI
    }
    */


    public static void AskTCP() {
        
    }

    public static void Connect() {
        try {
            adresseGroupe = InetAddress.getByName(address);
            socketGroupe = new MulticastSocket(port);
            socketGroupe.joinGroup(adresseGroupe);

            thr = new ThreadReception (nomCli, socketGroupe, LMsgRecus);
            thr.start();

            //nomCli = ZTNom.getText();
            nomCli = "Hugo";
            String msgDeb = nomCli + " rejoint le groupe";
            DatagramPacket dtg = new DatagramPacket(msgDeb.getBytes(), msgDeb.length(), adresseGroupe, port);
            socketGroupe.send(dtg);
        }
        catch (UnknownHostException e){ System.out.println("Erreur :-( : " + e.getMessage()); }
        catch (IOException e){ System.out.println("Erreur :-( : " + e.getMessage()); }
    }


    //private void BEnvoyerActionPerformed(java.awt.event.ActionEvent evt) {
    private static void BEnvoyerActionPerformed(java.awt.event.ActionEvent evt) {
        //String msg = nomCli + "> " + ZTMessage.getText();
        String msg = nomCli + "> " + "Bonjour";
        DatagramPacket dtg = new DatagramPacket(msg.getBytes(), msg.length(), adresseGroupe, port);
        try {
            socketGroupe.send(dtg);
        } catch (IOException e) {
            System.out.println("Erreur :-( : " + e.getMessage());
        }
    }


    //private void BArreterActionPerformed(java.awt.event.ActionEvent evt) {
    private static void BArreterActionPerformed(java.awt.event.ActionEvent evt) {
        String msg = nomCli + " quitte le groupe";
        DatagramPacket dtg = new DatagramPacket(msg.getBytes(), msg.length(), adresseGroupe, port);
        try {
            socketGroupe.send(dtg);
            thr.stop();

            //BDemarrer.setEnabled(true);
            //BArreter.setEnabled(false);
            //BEnvoyer.setEnabled(false);

            socketGroupe.leaveGroup(adresseGroupe); System.out.println("Après leaveGroup");

            socketGroupe.close(); System.out.println("Après close");
        } catch (IOException e) {
            System.out.println("Erreur :-( : " + e.getMessage());
        }
    }

    /*
    public static void main(String args[]) {
        new ClientChat().show();
    }
    
    private java.awt.Button BDemarrer;
    private java.awt.TextField ZTNom;
    private java.awt.Button BArreter;
    private java.awt.TextField ZTMessage;
    private java.awt.Button BEnvoyer;
    private java.awt.List LMsgRecus;
    */
    static List<String> LMsgRecus = new ArrayList<String>();
}
