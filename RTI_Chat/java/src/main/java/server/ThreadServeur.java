package server;

import java.io.*;
import java.net.*;


public class ThreadServeur extends Thread {
    public static String LOGIN_GROUP = "1";
    public static String LOGIN_JAVA = "JAVA";
    public static String LOGIN_C = "C";

    int port_tcp;
    String host_udp;
    int port_udp;

    ServerSocket SSocket = null;
    Socket sock = null;

    public ThreadServeur(int p_tcp, String h_udp, int p_udp) {
        port_tcp = p_tcp; host_udp = h_udp; port_udp = p_udp;
    }

    public void run() {
        try {
            SSocket = new ServerSocket(port_tcp);
        }
        catch (IOException e) { e.printStackTrace(); }

        while (!isInterrupted()) {
            try {
                System.out.println("Serveur en attente");
                sock = SSocket.accept();

                System.out.println("Le serveur est connecté");
                String[] tmp = GetMsg();

                if(tmp[0].equals(LOGIN_GROUP)) {
                    if(tmp[1].equals(LOGIN_JAVA)) {
                        // Digest
                    } else if(tmp[1].equals(LOGIN_C)) {

                    }
                    SendMsg(host_udp + "#" + port_udp + "$");
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }


    public void SendMsg(String msg) {
        try {
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF(msg); dos.flush();
        } catch (IOException e) {
            System.err.println("Error network ? [" + e.getMessage() + "]");
        }
    }

    public String[] GetMsg() {
        byte b;
        String[] msg = new String[10];
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());

            int i = 0;
            msg[i] = "";
            b = dis.readByte(); b = dis.readByte();
            while((b = dis.readByte()) != (byte)'$') {
                if(b == '#') {
                    i++;
                    msg[i] = "";
                } else
                    msg[i] += (char)b;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Recu " + msg[0] + " - "+ msg[1]);
        return msg;
    }


/*
    private void traiteConnexion(Socket sock)
    {
        System.out.println("traiteConnexion");
        String reussite=null;
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
            ResultSet rs = MySQL.MySQL_Request("select nom, motDePasse from agents where fonction = \"tour-operateur\"");
            
            while(rs.next())
            {
                HTConnection.put(rs.getString(1), rs.getString(2));
            }
            
            
            String nom = GetUser();
            byte[] mdp = GetPassword();
            int taille = size;
            double alea = rand;

            String m = HTConnection.get(nom);
            
            double rand=0;
            byte[] msgD={};
            if(m!=null)
            {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");

                    md.update(m.getBytes());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream bdos = new DataOutputStream(baos);
                    bdos.writeDouble(alea);
                    md.update(baos.toByteArray());
                    msgD = md.digest();
                } catch (NoSuchAlgorithmException ex) {
                    System.err.println("No Such Algorithm ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchProviderException ex) {
                    System.err.println("No Such Provider ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    System.err.println("IO Exception ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(MessageDigest.isEqual(msgD, mdp))
                {
                    reussite="connection ok";
                    
                    CryptageAS CAS = new CryptageAS();
                    CAS.GenerateASKey();
                }
                else
                    reussite="connection échouée";
            }
            else
                reussite="connection échouée";
            
        } catch (SQLException ex) {
            System.err.println("SQL Exception ? [" + ex.getMessage() + "]");
            //Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //ReponseSUM rep = new ReponseSUM(ReponseSUM.CONNECTION, reussite);
        try {
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeChars("tmp"); dos.flush();
            dos.close();
        }
        catch (IOException e) {
            System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
        }
    }
*/
}
