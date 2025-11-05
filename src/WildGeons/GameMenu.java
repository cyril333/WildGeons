package WildGeons;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Cyril Antolijao
 */

public class GameMenu extends javax.swing.JFrame {

    public GameMenu() {

        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        lblGameTitle = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnAbout = new javax.swing.JButton();
        btnMusicBG = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.red);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        lblGameTitle.setFont(new java.awt.Font("Consolas", 1, 100)); // NOI18N
        lblGameTitle.setForeground(new java.awt.Color(204, 153, 0));
        lblGameTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGameTitle.setText("WILDGEONS");

        btnStart.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        btnStart.setForeground(new java.awt.Color(204, 153, 0));
        btnStart.setText("START");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });


        btnAbout.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        btnAbout.setForeground(new java.awt.Color(204, 153, 0));
        btnAbout.setText("ABOUT");
        btnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutActionPerformed(evt);
            }
        });

        btnMusicBG.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnMusicBG.setForeground(new java.awt.Color(204, 153, 0));
        btnMusicBG.setText("MUSIC : ON / OFF");
        btnMusicBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMusicBGActionPerformed(evt);
            }
        });

        btnExit.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        btnExit.setForeground(new java.awt.Color(204, 153, 0));
        btnExit.setText("EXIT");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(btnMusicBG, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(184, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btnAbout, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(281, 281, 281))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(lblGameTitle)
                                                .addGap(124, 124, 124))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(179, Short.MAX_VALUE)
                                .addComponent(lblGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAbout, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnMusicBG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(52, 52, 52))
        );

        pack();
    }// </editor-fold>

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Game started, MAP 1: GLE Ancient Building");
    }

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {
        String about = "WILDGEONS RPG\n\n" +
                "Characters:\n" +
                "• Bron - IT Student\n" +
                "• Abdul - CS Student\n" +
                "• Jamal - CpE Student\n\n" +
                "Fight for your diploma!";
        JOptionPane.showMessageDialog(this, about);
    }

    private void btnMusicBGActionPerformed(java.awt.event.ActionEvent evt) {
        String current = btnMusicBG.getText();
        btnMusicBG.setText(current.equals("MUSIC : ON / OFF") ? "MUSIC : OFF / ON" : "MUSIC : ON / OFF");
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnAbout;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnMusicBG;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel lblGameTitle;
    // End of variables declaration
}
