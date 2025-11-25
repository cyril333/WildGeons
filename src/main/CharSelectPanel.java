package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class CharSelectPanel extends JPanel {

    // --- Custom JLabel that forces the image to scale ---
    class ImageScalerLabel extends JLabel {
        private Image originalImage;

        public ImageScalerLabel() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        public void setImage(Image img) {
            this.originalImage = img;
            setIcon(null);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (originalImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.SrcOver);

                double imageRatio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                int newWidth = panelWidth;
                int newHeight = (int) (panelWidth / imageRatio);

                if (newHeight < panelHeight) {
                    newHeight = panelHeight;
                    newWidth = (int) (panelHeight * imageRatio);
                }

                int x = (panelWidth - newWidth) / 2;
                int y = (panelHeight - newHeight) / 2;

                g2d.drawImage(originalImage, x, y, newWidth, newHeight, this);
            }
        }
    }
    // --- END ImageScalerLabel ---

    private final MainGameDriver driver;

    private JPanel charDisplayPanel;
    private JLabel nameHeader;
    private JButton confirmButton;
    private JButton backButton;

    private String selectedCharacter = "Bron";
    private final Map<String, JButton> charButtons = new HashMap<>();
    private final Map<String, ImageScalerLabel> charImageLabels = new HashMap<>();

    // --- Data Maps ---
    private final Map<String, Color> charColors = Map.of(
            "Bron", new Color(0, 153, 204),
            "Abdul", new Color(0, 204, 102),
            "Jamal", new Color(255, 102, 0)
    );

    private final Map<String, String> PROGRAM_MAP = Map.of(
            "Bron", " - IT Student",
            "Abdul", " - CS Student",
            "Jamal", " - CpE Student"
    );

    private final Map<String, String> SKILL_MAP = Map.of(
            "Bron", "Ultimate: RJ45 Bomb (550 DMG)",
            "Abdul", "Ultimate: Automata (550 DMG)",
            "Jamal", "Ultimate: Robo Minion (400 DMG per hit)"
    );

    private final String[] CHARACTERS = {"Bron", "Abdul", "Jamal"};

    public CharSelectPanel(MainGameDriver driver) {
        this.driver = driver;
        initUI();
        updateInfoDisplay(selectedCharacter);
        updateButtonBorders();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // --- Title ---
        JLabel titleLabel = new JLabel("CHARACTER SELECT");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 36));
        titleLabel.setForeground(new Color(204, 153, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // --- CENTER PANEL (Character Images) ---
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        centerWrapper.setOpaque(false);

        // 1. Character Image Display (3 Columns in a Row)
        charDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        charDisplayPanel.setOpaque(false);

        for (String name : CHARACTERS) {
            JPanel card = createCharacterCard(name);
            charDisplayPanel.add(card);
        }

        centerWrapper.add(charDisplayPanel);

        // 2. Selected Name Header and Skill Display
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);
        nameHeader = new JLabel();
        nameHeader.setFont(new Font("Consolas", Font.BOLD, 24));
        nameHeader.setHorizontalAlignment(SwingConstants.CENTER);
        nameHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerWrapper.add(nameHeader, BorderLayout.CENTER);

        centerWrapper.add(headerWrapper);
        add(centerWrapper, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Confirm + Back) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Back Button
        RpgButton backButton = new RpgButton("BACK", 28);
        backButton.setPreferredSize(new Dimension(200, 60));
        backButton.addActionListener(e -> driver.changeState(MainGameDriver.GameState.MENU));
        bottomPanel.add(backButton, BorderLayout.WEST);

        // Confirm Button
        confirmButton = new RpgButton("CONFIRM (" + selectedCharacter + PROGRAM_MAP.get(selectedCharacter) + ")", 28);
        confirmButton.setPreferredSize(new Dimension(500, 60));
        confirmButton.addActionListener(e -> {
            driver.setSelectedCharacter(selectedCharacter);
            // CRITICAL CHANGE: Switch to the Story Panel!
            driver.changeState(MainGameDriver.GameState.SHOW_STORY);
        });

        JPanel confirmWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmWrapper.setOpaque(false);
        confirmWrapper.add(confirmButton);
        bottomPanel.add(confirmWrapper, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createCharacterCard(String name) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(new Color(20, 0, 0));
        card.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 2));

        // Image Label
        ImageScalerLabel imageLabel = new ImageScalerLabel();
        imageLabel.setPreferredSize(new Dimension(200, 350));
        charImageLabels.put(name, imageLabel);
        loadCharacterImage(name, imageLabel);
        card.add(imageLabel, BorderLayout.CENTER);

        // Select Button
        String buttonText = name + PROGRAM_MAP.get(name);
        RpgButton button = new RpgButton(buttonText, 18);
        button.addActionListener(e -> {
            selectedCharacter = name;
            confirmButton.setText("CONFIRM (" + selectedCharacter + PROGRAM_MAP.get(selectedCharacter) + ")");
            updateInfoDisplay(name);
            updateButtonBorders();
        });
        charButtons.put(name, button);
        card.add(button, BorderLayout.SOUTH);

        return card;
    }

    // RpgButton Class (Same as before)
    class RpgButton extends JButton {
        public RpgButton(String text, int fontSize) {
            super(text);
            setFont(new Font("Consolas", Font.BOLD, fontSize));
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
                    if (!getText().contains(selectedCharacter)) {
                        setBackground(new Color(62, 0, 0, 180));
                        setForeground(new Color(204, 153, 0));
                    }
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

    private void loadCharacterImage(String charName, ImageScalerLabel label) {
        String imageName = charName.toLowerCase() + "_charselect.png";
        try {
            java.net.URL imgURL = getClass().getResource("/" + imageName);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                label.setImage(icon.getImage());
            } else {
                label.setImage(null);
                System.err.println("Character image not found: " + imageName);
            }
        } catch (Exception e) {
            label.setImage(null);
            System.err.println("Error loading image: " + imageName);
        }
    }

    private void updateButtonBorders() {
        charImageLabels.forEach((name, label) -> {
            JPanel parentCard = (JPanel) label.getParent();
            if (name.equals(selectedCharacter)) {
                parentCard.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 0), 4));
                charButtons.get(name).setBackground(new Color(204, 153, 0));
                charButtons.get(name).setForeground(new Color(62, 0, 0));

            } else {
                parentCard.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 2));
                charButtons.get(name).setBackground(new Color(62, 0, 0, 180));
                charButtons.get(name).setForeground(new Color(204, 153, 0));
            }
        });
    }

    private void updateInfoDisplay(String charName) {
        // --- 1. Name Header (CENTER BOTTOM) ---
        // Final, clean display: Name, Program, and Ultimate Skill
        String nameText = charName.toUpperCase() + PROGRAM_MAP.get(charName).toUpperCase();

        String htmlContent = String.format(
                "<html><div style='text-align: center; color: #FFFFFF;'>" +
                        // Name & Program (Large and Colored)
                        "<span style='font-size: 24pt; color: %s;'>%s</span><br>" +
                        // Ultimate Skill (Bold and Yellow/Gold)
                        "<span style='font-size: 16pt; color: #FFD700; font-weight: bold;'>%s</span>" +
                        "</div></html>",
                "#" + Integer.toHexString(charColors.get(charName).getRGB()).substring(2),
                nameText,
                SKILL_MAP.get(charName)
        );

        nameHeader.setText(htmlContent);
        nameHeader.setForeground(charColors.get(charName));

        charDisplayPanel.revalidate();
        charDisplayPanel.repaint();
    }
}