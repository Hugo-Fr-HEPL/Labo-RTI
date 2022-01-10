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

        int port = Integer.parseInt(prop.getProperty("PORT_AIRTRAFIC"));

        ThreadServeur ts1 = new ThreadServeur(port);
        ts1.start();
    }
}