package main;

import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {

    //yeah
    public GameMenu() {
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        lblGameTitle = new JLabel();
        btnStart = new JButton();
        btnAbout = new JButton();
        btnMusicBG = new JButton();
        btnExit = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("WildGeons - 3rd Street");
        setSize(800, 600);
        setResizable(false);
        getContentPane().setBackground(new Color(62, 0, 0));

        lblGameTitle.setFont(new Font("Consolas", Font.BOLD, 100));
        lblGameTitle.setForeground(new Color(204, 153, 0));
        lblGameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblGameTitle.setText("WILDGEONS");

        btnStart.setFont(new Font("Consolas", Font.PLAIN, 24));
        btnStart.setForeground(new Color(204, 153, 0));
        btnStart.setText("START");
        btnStart.setBackground(new Color(62, 0, 0));
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(this::btnStartActionPerformed);

        btnAbout.setFont(new Font("Consolas", Font.PLAIN, 24));
        btnAbout.setForeground(new Color(204, 153, 0));
        btnAbout.setText("ABOUT");
        btnAbout.setBackground(new Color(62, 0, 0));
        btnAbout.setFocusPainted(false);
        btnAbout.addActionListener(this::btnAboutActionPerformed);

        btnMusicBG.setFont(new Font("Consolas", Font.PLAIN, 18));
        btnMusicBG.setForeground(new Color(204, 153, 0));
        btnMusicBG.setText("MUSIC : ON / OFF");
        btnMusicBG.setBackground(new Color(62, 0, 0));
        btnMusicBG.setFocusPainted(false);
        btnMusicBG.addActionListener(this::btnMusicBGActionPerformed);

        btnExit.setFont(new Font("Consolas", Font.PLAIN, 18));
        btnExit.setForeground(new Color(204, 153, 0));
        btnExit.setText("EXIT");
        btnExit.setBackground(new Color(62, 0, 0));
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(this::btnExitActionPerformed);

        // Perfected alignment using GroupLayout (preserving your original structure)
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblGameTitle, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(300)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnAbout, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                                .addGap(300))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(80)
                                .addComponent(btnMusicBG, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
                                .addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                .addGap(80))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(100)
                        .addComponent(lblGameTitle, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        .addGap(50)
                        .addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(btnAbout, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnMusicBG, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                        .addGap(50)
        );

        pack();
    }

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Game started, MAP 1: GLE Ancient Building");
    }

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {
        String about = """
                WILDGEONS RPG
                
                Characters:
                • Bron - IT Student
                • Abdul - CS Student
                • Jamal - CpE Student
                
                Fight for your diploma!""";
        JOptionPane.showMessageDialog(this, about);
    }

    private void btnMusicBGActionPerformed(java.awt.event.ActionEvent evt) {
        String current = btnMusicBG.getText();
        btnMusicBG.setText(current.equals("MUSIC : ON / OFF") ? "MUSIC : OFF / ON" : "MUSIC : ON / OFF");
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(GameMenu::new);
    }

    // Variables
    private JButton btnAbout;
    private JButton btnExit;
    private JButton btnMusicBG;
    private JButton btnStart;
    private JLabel lblGameTitle;
}