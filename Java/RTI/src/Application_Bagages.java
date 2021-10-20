import java.io.*;
import java.net.*;

public class Application_Bagages {
    public static void main(String[] args)
    {
        Socket cliSock = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        String reponse = null;

        try
        {
            cliSock = new Socket("localhost",50000);
            System.out.println("Client connecté");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            dis = new DataInputStream(cliSock.getInputStream());
            dos = new DataOutputStream(cliSock.getOutputStream());
            System.out.println("Flux créés");

            if(cliSock==null || dis==null || dos==null)
                System.exit(1);

            BufferedReader disClavier = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("chaine a envoyer: ");
            String outchaine = disClavier.readLine();
            System.out.println("envoie: " + outchaine);
            disClavier.close();

            if(!outchaine.equals("") && dos!=null && dis!=null)
            {
                // Envoie
                dos.writeUTF(outchaine);
                //dos.writeUTF("FIN");

                //Réponse
                reponse = dis.readUTF();

                System.out.println("reponse: " + reponse);

                dis.close();
                dos.close();
                cliSock.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
