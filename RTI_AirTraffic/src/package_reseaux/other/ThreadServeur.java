package package_reseaux.other;

import java.io.*;
import java.net.*;

import package_reseaux.Interface.ConsoleServeur;
import package_reseaux.Interface.Requete;
import package_reseaux.Interface.SourceTaches;



public class ThreadServeur extends Thread {
    int port;
    ServerSocket SSocket = null;
    Socket CSocket = null;
    Requete req;

    private ConsoleServeur guiApplication;

    public ThreadServeur(int p) {
        port = p;
    }

    public void run() {
        
        try {
            SSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        while (!isInterrupted()) {
            System.out.println("Serveur en attente");
            try {
                CSocket = SSocket.accept();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println("Le serveur est connecté");
            try {
                ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());

                //Reçus
                req = (Requete) ois.readObject();

            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            SourceTaches tachesAExecuter = new Tache();
            ThreadClient thr = new ThreadClient(tachesAExecuter);
            
            Runnable travail = req.createRunnable(CSocket, guiApplication);
            if (travail != null) {
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail donné");
                
                thr.start();
            }
        }
    }
}
