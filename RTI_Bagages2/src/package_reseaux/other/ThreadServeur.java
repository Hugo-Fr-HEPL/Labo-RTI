package package_reseaux.other;

import java.io.*;
import java.net.*;
import java.util.Properties;

import package_reseaux.Interface.ConsoleServeur;
import package_reseaux.Interface.Requete;
import package_reseaux.Interface.SourceTaches;



public class ThreadServeur extends Thread {
    int port;
    ServerSocket SSocket = null;
    Socket CSocket = null;
    Requete req;

    private SourceTaches tachesAExecuter;
    private ConsoleServeur guiApplication;

    public ThreadServeur(int p, SourceTaches st) {
        port = p; tachesAExecuter = st;
    }

    public void run() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            SSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Démarrage du pool de threads");

        for (int i = 0; i < 3; i++) {
            ThreadClient thr = new ThreadClient(tachesAExecuter, "Thread du pool n° " + i);
            thr.start();
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
                if(CSocket.getPort() == Integer.parseInt(prop.getProperty("PORT_BAGAGES"))) {
                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());

                    //Reçus
                    req = (Requete) ois.readObject();
                } else {
                    DataInputStream dis = new DataInputStream(CSocket.getInputStream());
                    
                    String msgFinal = dis.readUTF();
                    /*
                    byte b;
                    String[] msg = new String[10];
                    int i = 0;
                    msg[i] = "";
                    while((b = dis.readByte()) != (byte)'$') {
                        if(b == '#') {
                            i++;
                            msg[i] = "";
                        } else
                            msg[i] += (char)b;
                    }
                    */
                    System.out.println("test " + msgFinal);
                    
                    req = new RequeteSUM(0, msgFinal);
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            Runnable travail = req.createRunnable(CSocket, guiApplication);
            if (travail != null) {
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail mis dans la file");
            }
        }
    }
}
