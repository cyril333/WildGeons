package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel { // Extends JPanel now

    private MainGameDriver driver;

    // --- Custom Button Class ---
    class RpgButton extends JButton {
        public RpgButton(String text) {
            super(text);
            setFont(new Font("Consolas", Font.BOLD, 24));
            setForeground(new Color(204, 153, 0));
            setBackground(new Color(62, 0, 0, 180));

            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 2));
            setContentAreaFilled(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(204, 153, 0));
                    setForeground(new Color(62, 0, 0));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(62, 0, 0, 180));
                    setForeground(new Color(204, 153, 0));
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    // --- Background Panel ---
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            try {
                java.net.URL imgURL = getClass().getResource("/" + fileName);
                if (imgURL != null) {
                    backgroundImage = new ImageIcon(imgURL).getImage();
                } else {
                    System.err.println("Image not found: " + fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(62, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // --- CONSTRUCTOR: Takes the driver reference ---
    public MenuPanel(MainGameDriver driver) {
        this.driver = driver;
        initComponents();
    }

    private void initComponents() {
        // Set Background
        BackgroundPanel bgPanel = new BackgroundPanel("menu_bg.png");
        setLayout(new BorderLayout());
        bgPanel.setLayout(new GroupLayout(bgPanel));
        add(bgPanel, BorderLayout.CENTER);

        // --- BUTTONS ---
        btnStart = new RpgButton("START");
        btnStart.addActionListener(this::btnStartActionPerformed);

        btnAbout = new RpgButton("ABOUT");
        btnAbout.addActionListener(this::btnAboutActionPerformed);

        btnMusicBG = new RpgButton("MUSIC : ON / OFF");
        btnMusicBG.setFont(new Font("Consolas", Font.BOLD, 18));
        btnMusicBG.addActionListener(this::btnMusicBGActionPerformed);

        btnExit = new RpgButton("EXIT");
        btnExit.setFont(new Font("Consolas", Font.BOLD, 18));
        btnExit.addActionListener(this::btnExitActionPerformed);

        // --- LAYOUT ---
        GroupLayout layout = (GroupLayout) bgPanel.getLayout();
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
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
                        .addGap(280) // Push buttons down below the title graphic
                        .addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(btnAbout, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnMusicBG, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                        .addGap(50)
        );

        setPreferredSize(new Dimension(800, 600));
    }

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {
        driver.changeState(MainGameDriver.GameState.SELECT_CHAR);
    }

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {
        String about = """
                Created by: 3rd Street 
                """;
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.PLAIN_MESSAGE);
    }

    private void btnMusicBGActionPerformed(java.awt.event.ActionEvent evt) {
        String current = btnMusicBG.getText();
        btnMusicBG.setText(current.equals("MUSIC : ON / OFF") ? "MUSIC : OFF / ON" : "MUSIC : ON / OFF");
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    // Variables
    private RpgButton btnAbout;
    private RpgButton btnExit;
    private RpgButton btnMusicBG;
    private RpgButton btnStart;
}