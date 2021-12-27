package application_billets;

import Cryptage.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import package_reseaux.other.GetDirectory;
import package_reseaux.other.ReponseSUM;
import package_reseaux.other.RequeteSUM;

public class Application_Billets extends javax.swing.JFrame {

    public Application_Billets() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        TLogin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TPassword = new javax.swing.JPasswordField();
        BConnect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tvols7 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("login:");

        jLabel2.setText("password: ");

        BConnect.setForeground(new java.awt.Color(255, 0, 0));
        BConnect.setText("Connect");
        BConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BConnectActionPerformed(evt);
            }
        });

        Tvols7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numéro vol", "Date vol", "Heure départ", "Destination", "Pays", "Compagnies", "Prix"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tvols7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tvols7MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Tvols7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(TLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(TPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(BConnect)
                        .addGap(0, 464, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BConnect)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(TLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        TLogin.setText("Ayers");
        TPassword.setText("mdpAyers");
    }//GEN-LAST:event_formWindowOpened

    private void BConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BConnectActionPerformed
        double rand=0;
        byte[] msgD={};

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");

            rand = Math.random();
            md.update(TPassword.getText().getBytes());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeDouble(rand);
            md.update(baos.toByteArray());
            msgD = md.digest();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
        }

        RequeteSUM req = new RequeteSUM(RequeteSUM.CONNEXION, TLogin.getText(), rand, msgD.length, msgD);

        ReponseSUM rep = sendreceive(req);
        System.out.println("*** Reponse reçue : " + rep.getChargeUtile());

        if(rep.getChargeUtile().equals("connection ok"))
        {
            try {
                Color c = new Color(0,150,0);
                BConnect.setForeground(c);

                CryptageSY CSY = new CryptageSY();
                CSY.GenerateSYKey();
                
                //chiffrement clé sec 1
                
                ObjectInputStream secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se1.ser"));
                SecretKey cleSec = (SecretKey) secKeyFile.readObject();
                secKeyFile.close();

                String cs = cleSec.toString();

                CryptageAS CAS = new CryptageAS();
                byte[] cript1 = CAS.CryptAS(cs.getBytes());
                
                //chiffrement clé se 2
                
                secKeyFile = new ObjectInputStream(new FileInputStream("c:\\Users\\dries\\Documents\\école\\3eme supérieur\\RTI\\RTI_Billets\\Files\\se2.ser"));
                cleSec = (SecretKey) secKeyFile.readObject();
                secKeyFile.close();

                cs = cleSec.toString();

                byte[] cript2 = CAS.CryptAS(cs.getBytes());

                System.out.println("*/*/*/*" + cript1 + "    " + cript2);
                req = new RequeteSUM(RequeteSUM.HANDSHAKE, cript1, cript2);
                rep = sendreceive(req);
                
                Vector vec = rep.getVec();
                DefaultTableModel DTM = (DefaultTableModel) Tvols7.getModel();
                for(int i=0 ; i<vec.size() ; i++)
                {
                    DTM.addRow((Vector)vec.get(i));
                }

            } catch (IOException ex) {
                Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_BConnectActionPerformed

    private void Tvols7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tvols7MouseClicked
        if(evt.getClickCount()==2)
        {
            JTable JT = (JTable) evt.getSource();
            int ligne = JT.getSelectedRow();
            System.out.println(ligne);
            String infosVol = JT.getValueAt(ligne, 1) + "/" + JT.getValueAt(ligne, 0) + "/" + JT.getValueAt(ligne, 3) + "/" + JT.getValueAt(ligne, 4);

            JFormulaire JF = new JFormulaire(infosVol);
            JF.setVisible(true);
        }
        
    }//GEN-LAST:event_Tvols7MouseClicked

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

        int port = Integer.parseInt(prop.getProperty("PORT_BILLETS"));
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
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application_Billets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application_Billets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application_Billets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application_Billets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Application_Billets().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BConnect;
    private javax.swing.JTextField TLogin;
    private javax.swing.JPasswordField TPassword;
    private javax.swing.JTable Tvols7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
