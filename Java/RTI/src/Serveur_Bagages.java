import java.io.*;
import java.net.*;

public class Serveur_Bagages extends Thread {
    public static void main(String[] args)
    {
        int port = 50000;
        ServerSocket SSocket = null;
        Socket CSocket = null;
        String inchaine;

            try {
                SSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Demarrage du pool de threads");

            for(int i=0 ; i<3 ; i++)
            {
                //code creation pool de thread
            }

            System.out.println("Serveur en attente");
            try {
                CSocket = SSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                System.out.println("Le serveur est connecté");

                DataInputStream dis = new DataInputStream(CSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(CSocket.getOutputStream());

                //while(!dis.readUTF().equals("FIN"))
                //{
                //Reçus
                inchaine = dis.readUTF();

                System.out.println(inchaine);

                //Envoie
                dos.writeUTF(inchaine);
                //}

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
