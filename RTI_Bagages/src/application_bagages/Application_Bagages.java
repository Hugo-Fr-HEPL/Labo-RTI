package application_bagages;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import package_reseaux.other.*;

public class Application_Bagages extends javax.swing.JFrame {

    public Application_Bagages() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TUserName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        TMdp = new javax.swing.JTextField();
        BSeConnecter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        LVols = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Nom d'utilisateur");

        jLabel2.setText("Mot de passe");

        BSeConnecter.setText("Se connecter");
        BSeConnecter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BSeConnecterActionPerformed(evt);
            }
        });

        LVols.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LVolsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(LVols);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(TMdp, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BSeConnecter)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(TMdp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BSeConnecter))
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BSeConnecterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSeConnecterActionPerformed
        double rand=0;
        byte[] msgD={};
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            
            rand = Math.random();
            md.update(TMdp.getText().getBytes());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeDouble(rand);
            md.update(baos.toByteArray());
            msgD = md.digest();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RequeteSUM req = new RequeteSUM(RequeteSUM.CONNEXION, TUserName.getText(), rand, msgD.length, msgD);

        // Lecture de la réponse
        ReponseSUM rep = sendreceive(req);
        System.out.println("*** Reponse reçue : " + rep.getChargeUtile());
            
        DefaultListModel model = new DefaultListModel();

        LVols.setModel(model);

        model.addAll(rep.getVec());
    }//GEN-LAST:event_BSeConnecterActionPerformed

    private void LVolsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LVolsMouseClicked
        JList source;
        String selected;
        String numvol;
        int i, j;
        
        if(evt.getClickCount()==2)
        {
            source = (JList)evt.getSource();
            selected = source.getSelectedValue().toString();
            
            System.out.println(selected);
            
            i=selected.indexOf(' ', 0);
            j=selected.indexOf('-', 0);
            numvol=selected.substring(i+1, j);
            System.out.println(numvol);
            
            RequeteSUM req = new RequeteSUM(RequeteSUM.BAGAGES, numvol);

            ReponseSUM rep = sendreceive(req);
                    
            System.out.println("*** Reponse reçue : " + rep.getChargeUtile());

            JBagages jb = new JBagages(rep.getVec());
            jb.setVisible(true);
        }
    }//GEN-LAST:event_LVolsMouseClicked

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

        int port = Integer.parseInt(prop.getProperty("PORT_BAGAGES"));
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
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        /*TUserName.setText("Blair");
        TMdp.setText("mdpBlair");*/
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(Application_Bagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application_Bagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application_Bagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application_Bagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Application_Bagages().setVisible(true);                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BSeConnecter;
    private javax.swing.JList<String> LVols;
    private javax.swing.JTextField TMdp;
    private javax.swing.JTextField TUserName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
