package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import package_reseaux.other.GetDirectory;
import package_reseaux.other.RequeteSUM;

public class JBagages extends javax.swing.JFrame implements TableModelListener{

    public JBagages(Vector vec) {
        initComponents();
        
        DefaultTableModel dtm = new DefaultTableModel();
        dtm = (DefaultTableModel) TBags.getModel();

        Iterator myIter = vec.iterator();
        while (myIter.hasNext())
        {
            Vector vtmp = (Vector) myIter.next();
            dtm.addRow(vtmp);
        }
        
        dtm.addTableModelListener(this);
        
        setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        
    }
    
    public void tableChanged (TableModelEvent e)
    {
        System.out.println("Modification dans le tableau en (" + e.getFirstRow() + "," + e.getColumn() + ")");
        
        String vchange = TBags.getValueAt(e.getFirstRow(), e.getColumn()).toString();
        String idbag =  TBags.getValueAt(e.getFirstRow(), 0).toString();
        
        RequeteSUM req=null;
        
        switch(e.getColumn())
        {
            case 3: req = new RequeteSUM(RequeteSUM.RECEPTION, idbag, vchange);
                break;
            case 4: if(vchange.equals("O")|| vchange.equals("N") || vchange.equals("R"))
                        req = new RequeteSUM(RequeteSUM.SOUTE, idbag, vchange);
                    else
                    {
                        TBags.setValueAt("N",e.getFirstRow(), e.getColumn());
                        JOptionPane.showConfirmDialog(this,"Vous avez écrit un mauvais caractère!","Attention",JOptionPane.OK_OPTION);
                    }
                break;
            case 5: req = new RequeteSUM(RequeteSUM.DOUANE, idbag, vchange);
                break;
            case 6: req = new RequeteSUM(RequeteSUM.REMARQUE, idbag, vchange);
                break;
        }
        
        if(req!=null)
        {
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            Socket cliSock = null;

            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

            int port = Integer.parseInt(prop.getProperty("PORT_BAGAGES"));
            String adresse = (String) prop.get("Adresse");

            try {
                cliSock = new Socket(adresse, port);
                System.out.println(cliSock.getInetAddress().toString());
            }
            catch (UnknownHostException ex) {
                System.err.println("Erreur ! Host non trouvé [" + ex + "]");
            }
            catch (IOException ex) {
                System.err.println("Erreur ! Pas de connexion ? [" + ex + "]");
            }

            // Envoie de la requête
            try {
                oos = new ObjectOutputStream(cliSock.getOutputStream());
                oos.writeObject(req); oos.flush();
            }
            catch (IOException ex) {
                System.err.println("Erreur réseau ? [" + ex.getMessage() + "]");
            }

        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TBags = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        TBags.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "idBagage", "poid", "valise (0=non, 1=oui)", "réception", "soute (O,N,R)", "vérifié", "remarque"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TBags.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(TBags);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int verif=0;
        for(int i=0 ; i<TBags.getRowCount() ; i++)
        {
            if(TBags.getValueAt(i, 4)!=null){
                if(TBags.getValueAt(i, 4).equals("O") || TBags.getValueAt(i, 4).equals("R"))
                verif++;
            }
        }
        if(verif == TBags.getRowCount())
            this.dispose();
    }//GEN-LAST:event_formWindowClosing

    
    /*public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JBagages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JBagages().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TBags;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
