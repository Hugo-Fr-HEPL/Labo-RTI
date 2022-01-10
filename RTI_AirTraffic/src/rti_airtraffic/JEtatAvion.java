package rti_airtraffic;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class JEtatAvion extends javax.swing.JFrame {

    String[] infosTab = null;
    AirTraffic AT;
    
    public JEtatAvion(String infos) {
        initComponents();
        setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        AT = new AirTraffic();
        
        infosTab = infos.split("/"); 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TEtat = new javax.swing.JTable();
        BCheckingOff = new javax.swing.JButton();
        BReady = new javax.swing.JButton();
        BReadyToFly = new javax.swing.JButton();
        TNumPiste = new javax.swing.JTextField();
        BAskTrack = new javax.swing.JButton();
        BTakingOff = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        TEtat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Boolean(true), null, null, null, null, null}
            },
            new String [] {
                "Busy", "Checkin off", "Ready", "Ready to fly", "Taking off", "Flying"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TEtat.setRowHeight(30);
        jScrollPane1.setViewportView(TEtat);
        if (TEtat.getColumnModel().getColumnCount() > 0) {
            TEtat.getColumnModel().getColumn(0).setResizable(false);
            TEtat.getColumnModel().getColumn(1).setResizable(false);
            TEtat.getColumnModel().getColumn(2).setResizable(false);
            TEtat.getColumnModel().getColumn(3).setResizable(false);
            TEtat.getColumnModel().getColumn(4).setResizable(false);
            TEtat.getColumnModel().getColumn(5).setResizable(false);
        }

        BCheckingOff.setText("Set checkin off");
        BCheckingOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BCheckingOffActionPerformed(evt);
            }
        });

        BReady.setText("Set Ready");
        BReady.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BReadyActionPerformed(evt);
            }
        });

        BReadyToFly.setText("Set Ready to fly");
        BReadyToFly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BReadyToFlyActionPerformed(evt);
            }
        });

        BAskTrack.setText("Ask track ");
        BAskTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BAskTrackActionPerformed(evt);
            }
        });

        BTakingOff.setText("Set Taking off");
        BTakingOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTakingOffActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(BCheckingOff, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BReady, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TNumPiste)
                            .addComponent(BReadyToFly, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(BAskTrack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(BTakingOff, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BCheckingOff)
                    .addComponent(BReady)
                    .addComponent(BAskTrack)
                    .addComponent(BTakingOff))
                .addGap(18, 18, 18)
                .addComponent(BReadyToFly)
                .addGap(18, 18, 18)
                .addComponent(TNumPiste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BCheckingOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BCheckingOffActionPerformed
        String rep = AT.FinCheckin(infosTab[0]);
        if(rep.equals("ok"))
        {
            DefaultTableModel DTM = (DefaultTableModel) TEtat.getModel();
            DTM.setValueAt(false, 0, 0);
            DTM.setValueAt(true, 0, 1);
        }
        else
        {
            JOptionPane.showMessageDialog(this, rep ,"Information",JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_BCheckingOffActionPerformed

    private void BReadyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BReadyActionPerformed
        DefaultTableModel DTM = (DefaultTableModel) TEtat.getModel();
        
        Object checked = DTM.getValueAt(0, 1);
        if(checked!=null)
        {
            String rep = AT.Ready(infosTab[1]);
            
            DTM.setValueAt(false, 0, 1);
            DTM.setValueAt(true, 0, 2);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Le checkin est toujours ouvert !" ,"Information",JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_BReadyActionPerformed

    private void BAskTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BAskTrackActionPerformed
        String rep = AT.AskTrack();
        JOptionPane.showMessageDialog(this, rep ,"Information",JOptionPane.OK_OPTION);
    }//GEN-LAST:event_BAskTrackActionPerformed

    private void BReadyToFlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BReadyToFlyActionPerformed
        DefaultTableModel DTM = (DefaultTableModel) TEtat.getModel();
        Object checked = DTM.getValueAt(0, 2);
        
        if(checked!=null)
        {
            String choixPiste = TNumPiste.getText();
            if(choixPiste!=null)
            {
                TNumPiste.setText("");

                String rep = AT.SaveTrack(choixPiste, infosTab[1]);
                JOptionPane.showMessageDialog(this, rep ,"Information",JOptionPane.OK_OPTION);
                DTM.setValueAt(false, 0, 2);
                DTM.setValueAt(true, 0, 3);
            }
            
        }
        else
        {
            JOptionPane.showMessageDialog(this, "L'avion n'est pas encore prêt !" ,"Information",JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_BReadyToFlyActionPerformed

    private void BTakingOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTakingOffActionPerformed
        DefaultTableModel DTM = (DefaultTableModel) TEtat.getModel();
        
        Object checked = DTM.getValueAt(0, 3);
        if(checked!=null)
        {
            String rep = AT.TakingOff(infosTab[1]);
            
            DTM.setValueAt(false, 0, 3);
            DTM.setValueAt(true, 0, 4);
            
            DTM.setValueAt(false, 0, 4);
            DTM.setValueAt(true, 0, 5);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "L'avion n'est pas encore prêt !" ,"Information",JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_BTakingOffActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BAskTrack;
    private javax.swing.JButton BCheckingOff;
    private javax.swing.JButton BReady;
    private javax.swing.JButton BReadyToFly;
    private javax.swing.JButton BTakingOff;
    private javax.swing.JTable TEtat;
    private javax.swing.JTextField TNumPiste;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
