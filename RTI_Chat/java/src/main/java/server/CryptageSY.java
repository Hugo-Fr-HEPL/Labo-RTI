package server;

import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;

public class CryptageSY {
    
    public void GenerateSYKey()
    {
        try {
            KeyGenerator genKey = KeyGenerator.getInstance("DES","BC");
            genKey.init(new SecureRandom());
            
            SecretKey key = genKey.generateKey();
            System.out.println("cle secrete 1 = " + key.toString());
            
            System.out.println(" *** Cle secrete 1 serialisee");
            ObjectOutputStream secKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se1.ser"));
            secKeyFile.writeObject(key);
            secKeyFile.close();
            
            key = genKey.generateKey();
            System.out.println("cle secrete 2 = " + key.toString());
            
            System.out.println(" *** Cle secrete 2 serialisee");
            secKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se2.ser"));
            secKeyFile.writeObject(key);
            secKeyFile.close();
            
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
    
    public byte[] CryptSI(byte[] clair, int numcle)
    {
        byte[] texteCrypté=null;
        
        try
        {
            SecretKey key=null;
            
            if(numcle==1)
            {
                ObjectInputStream secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se1.ser"));
                key = (SecretKey) secKeyFile.readObject();
                secKeyFile.close();
            }
            else
            {
                if(numcle==2)
                {
                    ObjectInputStream secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se2.ser"));
                    key = (SecretKey) secKeyFile.readObject();
                    secKeyFile.close();
                }
            }
            System.out.println(" *** Clé récupérée = " + key.toString());
            
            Cipher chiffrement = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
            chiffrement.init(Cipher.ENCRYPT_MODE, key);
            texteCrypté = chiffrement.doFinal(clair);
            String texteCryptéAff = new String (texteCrypté);
            System.out.println(new String(clair) + " ---> " + texteCryptéAff);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return texteCrypté;
    }
    
    public byte[] DecryptSI(byte[] chiffrement, int numcle)
    {
        byte[] clair=null;
        
        try {
            SecretKey key=null;
            
            if(numcle==1)
            {
                ObjectInputStream secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se1.ser"));
                key = (SecretKey) secKeyFile.readObject();
                secKeyFile.close();
            }
            else
            {
                if(numcle==2)
                {
                    ObjectInputStream secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se2.ser"));
                    key = (SecretKey) secKeyFile.readObject();
                    secKeyFile.close();
                }
            }
            System.out.println(" *** Clé récupérée = " + key.toString());
            
            Cipher chif = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
            chif.init(Cipher.DECRYPT_MODE,key);
            clair = chif.doFinal(chiffrement);
            String clairAff = new String(clair);
            System.out.println(new String(chiffrement) + "--->" + clairAff);
                    
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(CryptageSY.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return clair;
    }
    
}
