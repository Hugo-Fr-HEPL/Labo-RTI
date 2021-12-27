/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package chat;

import java.net.*;
import java.util.*;
import java.io.*; 

/**
 *
 * @author Hugo
 */
public class FApp_IALogin extends javax.swing.JFrame {
    public static String LOGIN_GROUP = "1";
    public static String LOGIN_JAVA = "JAVA";

    DataInputStream dis = null;
    DataOutputStream dos = null;
    Socket sock = null;

    /**
     * Creates new form FApp_IALogin
     */
    public FApp_IALogin() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextPwd = new javax.swing.JPasswordField();
        jTextLogin = new javax.swing.JTextField();
        jButtonConnect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextPwd.setToolTipText("Password");

        jTextLogin.setToolTipText("Name");

        jButtonConnect.setText("Connect");
        jButtonConnect.setMaximumSize(new java.awt.Dimension(80, 25));
        jButtonConnect.setMinimumSize(new java.awt.Dimension(80, 25));
        jButtonConnect.setPreferredSize(new java.awt.Dimension(80, 25));
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        jLabel1.setText("Login to the chat");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextLogin)
                            .addComponent(jTextPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jLabel1)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        String login = jTextLogin.getText();
        String pwd = jTextPwd.getPassword().toString();
        String digest = null;


        // TCP
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));

            int port = Integer.parseInt(prop.getProperty("Port_tcp"));
            String adresse = (String) prop.get("Host_tcp");

            sock = new Socket(adresse, port);
            System.out.println(sock.getInetAddress().toString());
        }
        catch (UnknownHostException e) { System.err.println("Erreur ! Host non trouvé [" + e + "]"); }
        catch (IOException e) { System.out.println("--- erreur IO = " + e.getMessage()); }

        // Envoie du message
        SendMsg(LOGIN_GROUP + "#" + LOGIN_JAVA + "#" + digest + "$");

        // Lecture de la réponse
        String[] msg = GetMsg();



        this.setVisible(false);
        new FApp_IAChat(login, "234.5.5.9", 5001).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonConnectActionPerformed


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
            while((b = dis.readByte()) != (byte)'$') {
                /*
                if(b != '$' && b != '#')
                    msg[i] += (char)b;
                if(b == '#') {
                    i++;
                    msg[i] = "";
                }
                */
                if(b == '#') {
                    i++;
                    msg[i] = "";
                }
                msg[i] += (char)b;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Recu " + msg[0] + " - "+ msg[1]);
        return msg;
    }

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
            java.util.logging.Logger.getLogger(FApp_IALogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FApp_IALogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FApp_IALogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FApp_IALogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FApp_IALogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextLogin;
    private javax.swing.JPasswordField jTextPwd;
    // End of variables declaration//GEN-END:variables
}
