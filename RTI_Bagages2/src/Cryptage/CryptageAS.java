package Cryptage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptageAS {
    
    public void GenerateASKey()
    {
        try {
            System.out.println("Obtention d'un generateur de cle");
            KeyPairGenerator genKey = KeyPairGenerator.getInstance("RSA");

            System.out.println("Obtention de clés");
            KeyPair keys = genKey.generateKeyPair();
            PublicKey pubKey = keys.getPublic();
            PrivateKey priKey = keys.getPrivate();

            System.out.println("cle publique = " + pubKey);
            System.out.println("cle privee = " + priKey);
            
            System.out.println(" *** Cle publique generee serialisee");
            ObjectOutputStream pubKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\pu.ser"));
            pubKeyFile.writeObject(pubKey);
            pubKeyFile.close(); 

            System.out.println(" *** Cle privee generee serialisee");
            ObjectOutputStream privKeyFile = new ObjectOutputStream(new FileOutputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\pr.ser"));
            privKeyFile.writeObject(priKey);
            privKeyFile.close();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] CryptAS(byte[] CleClair)
    {
        byte[] CleCrypte = null;
        
        try {
            System.out.println("Tentative d'obtention d'un objet de chiffrement");
            
            Cipher chiffrement= Cipher.getInstance("RSA/ECB/PKCS1Padding"/*,CryptixCrypto*/);
            
            //chiffrement
            ObjectInputStream pubKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\pu.ser"));
            PublicKey clepublic = (PublicKey) pubKeyFile.readObject();
            pubKeyFile.close();

            chiffrement.init(Cipher.ENCRYPT_MODE, clepublic);
            CleCrypte = chiffrement.doFinal (CleClair);
            System.out.println(new String(CleClair) + " --->" + CleCrypte);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } /*catch (NoSuchProviderException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        }*/ catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return CleCrypte;
    }
    
    public byte[] DecrypAS(byte[] cleChiffre)
    {
        byte[] CleClair = null;
        
        try
        {
            //déchiffrement
            ObjectInputStream privKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\pr.ser"));
            PrivateKey cleprive = (PrivateKey) privKeyFile.readObject();
            privKeyFile.close();

            Cipher chiffrementd= Cipher.getInstance("RSA/ECB/PKCS1Padding"/*,CryptixCrypto*/);
            chiffrementd.init(Cipher.DECRYPT_MODE, cleprive);
            CleClair = chiffrementd.doFinal (cleChiffre);
            System.out.println(cleChiffre + " ---> " + new String(CleClair).substring(0, CleClair.length));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CryptageAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return CleClair;
    }
}
