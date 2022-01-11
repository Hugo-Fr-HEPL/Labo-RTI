package rti_airtraffic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import package_reseaux.other.GetDirectory;
import package_reseaux.other.ReponseSUM;
import package_reseaux.other.RequeteSUM;

public class AirTraffic {
    
    public Vector VolPrevu()
    {        
        RequeteSUM req = new RequeteSUM(RequeteSUM.VOL, "");

        ReponseSUM rep = sendreceive(req);
        
        return rep.getVec();
    }
    
    public String FinCheckin(String numvol)
    {
        RequeteSUM req = new RequeteSUM(RequeteSUM.CHECKIN, numvol);

        ReponseSUM rep = sendreceive(req);

        return rep.getChargeUtile();   
    }
    
    public String Ready(String numvol, String numavion)
    {
        RequeteSUM req = new RequeteSUM(RequeteSUM.READY, numavion, numvol);

        ReponseSUM rep = sendreceive(req);

        return rep.getChargeUtile();   
    }
    
    public String AskTrack()
    {
        RequeteSUM req = new RequeteSUM(RequeteSUM.ASKTRACK, "");

        ReponseSUM rep = sendreceive(req);

        return rep.getChargeUtile();   
    }
    
    public String SaveTrack(String numPiste, String numAvion)
    {
        RequeteSUM req = new RequeteSUM(RequeteSUM.SAVETRACK, numPiste, numAvion);

        ReponseSUM rep = sendreceive(req);

        return rep.getChargeUtile();   
    }
    
    public String TakingOff(String numAvion)
    {
        RequeteSUM req = new RequeteSUM(RequeteSUM.TAKINGOFF, numAvion);

        ReponseSUM rep = sendreceive(req);

        return rep.getChargeUtile();   
    }
    
    private ReponseSUM sendreceive(RequeteSUM req)
    {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        Socket cliSock = null;

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(prop.getProperty("PORT_AIRTRAFIC"));
        String adresse = (String) prop.get("Adresse");

        try {
            cliSock = new Socket(adresse, port);
            System.out.println(cliSock.getInetAddress().toString());
        }
        catch (UnknownHostException e) {
            System.err.println("Erreur ! Host non trouvé [" + e + "]");
        }
        catch (IOException e) {
            System.err.println("Erreur ! Pas de connexion ? [" + e + "]");
        }

        // Envoie de la requête
        try {
            oos = new ObjectOutputStream(cliSock.getOutputStream());
            oos.writeObject(req); oos.flush();
        }
        catch (IOException e) {
            System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
        }

        // Lecture de la réponse
        ReponseSUM rep = null;
        try {
            ois = new ObjectInputStream(cliSock.getInputStream());
            rep = (ReponseSUM)ois.readObject();
        }
        catch (ClassNotFoundException e) {
            System.out.println("--- erreur sur la classe = " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("--- erreur IO = " + e.getMessage());
        }
        return rep;
    }
}
