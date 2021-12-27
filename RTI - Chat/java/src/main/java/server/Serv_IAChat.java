package server;

import java.io.*;
import java.util.*;

/*
Donne une adresse de classe D précise et un port PORT_CHAT précis obtenu via TCP
Vérifie l'état de voyageur ou l'appartenance à une compagnie aérienne dans la base de données
    (BD_AIRPORT contenant les informations nécessaires à l'identification)
Attend sur le port PORT_FLY une seule commande permettant de valider le voyageur ou l’employé 
*/

public class Serv_IAChat
{
    public static void main( String[] args )
    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(prop.getProperty("Port_tcp"));

        ThreadServeur ts = new ThreadServeur(port);
        ts.start();
    }
}
