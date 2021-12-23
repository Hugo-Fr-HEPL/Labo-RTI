package server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptageSY {
    
    public void GenerateSYKey()
    {
        try {
            KeyGenerator genKey = KeyGenerator.getInstance("DES","BC");
            genKey.init(new SecureRandom());
            
            SecretKey key = genKey.generateKey();
            System.out.println("cle secrete 1 = " + key.toString());
            
            System.out.println(" *** Cle secrete 1 serialisee");
            ObjectOutputStream pubKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se1.ser"));
            pubKeyFile.writeObject(key);
            pubKeyFile.close();
            
            key = genKey.generateKey();
            System.out.println("cle secrete 2 = " + key.toString());
            
            System.out.println(" *** Cle publique 2 serialisee");
            pubKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se2.ser"));
            pubKeyFile.writeObject(key);
            pubKeyFile.close();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
