package package_reseaux.other;

import java.io.*;
import java.util.Properties;



public class Server {
    public static void main(String[] args) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(prop.getProperty("PORT_BAGAGES"));

        ThreadServeur ts1 = new ThreadServeur(port, new ListeTaches());
        ts1.start();
        
        port = Integer.parseInt(prop.getProperty("PORT_CHECKIN"));
        
        ThreadServeur ts2 = new ThreadServeur(port, new ListeTaches());
        ts2.start();
    }
}