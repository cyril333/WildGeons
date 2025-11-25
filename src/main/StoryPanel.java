package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * StoryPanel - Implements a classic JRPG dialogue box interface (NPC bottom-left, text box covers bottom section).
 */
public class StoryPanel extends JPanel {

    private final MainGameDriver driver;
    private final JTextArea storyTextArea;
    private final JLabel npcLabel;

    // --- Data Maps for NPC Story and Stats ---
    private final String NPC_NAME = "Snyd Jabines (Merchant)";

    // Storylines pulled from your README.md
    private final Map<String, String> CHARACTER_STORIES = Map.of(
            "Bron", "Welcome, determined IT Student! Bron is a determined student who dreams of earning his “certification.” To achieve it, he must conquer every subject that stands in his way.",
            "Abdul", "Developer in training, welcome. Abdul is a hardworking CIT student fighting through stress and challenges. His dream is to become a developer who will make the world a better place.",
            "Jamal", "Jamal, a shiftee from another program, has finally found his passion. Now he fights to prove his worth."
    );

    // UPDATED MAP: Includes Basic, Intermediate, and Ultimate Skills
    private final Map<String, String> CHARACTER_STATS = Map.of(
            "Bron", "HP: 1500 | Mana: 500\nBasic: Bits (120 DMG, 4s CD)\nIntermediate: Syntax Error (230 DMG, 12s CD)\nUltimate: RJ45 Bomb (550 DMG, 60s CD)",
            "Abdul", "HP: 1500 | Mana: 500\nBasic: Server Guard (120 DMG, 4s CD)\nIntermediate: Abstract Invi (250 DMG, 15s CD)\nUltimate: Automata (550 DMG, 60s CD)",
            "Jamal", "HP: 1500 | Mana: 500\nBasic: Wire Whip (120 DMG, 4s CD)\nIntermediate: Trouble Shoot (+500 HP, 20s CD)\nUltimate: Robo Minion (400 DMG per hit, 60s CD)"
    );

    public StoryPanel(MainGameDriver driver) {
        this.driver = driver;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 0, 0)); // Dark background for the top/game area

        // --- TOP/CENTER: Game Background Placeholder ---
        // This panel simulates the game world running above the dialogue box
        JPanel gameViewPlaceholder = new JPanel();
        gameViewPlaceholder.setBackground(new Color(20, 0, 0));
        add(gameViewPlaceholder, BorderLayout.CENTER);


        // --- BOTTOM: The Dialogue Box Container (Fixed Height) ---
        JPanel dialogueBoxContainer = new JPanel(new BorderLayout());
        dialogueBoxContainer.setPreferredSize(new Dimension(800, 300)); // Fixed height for bottom dialogue box
        dialogueBoxContainer.setBackground(new Color(62, 0, 0, 200)); // Semi-transparent maroon background
        dialogueBoxContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. NPC Image (Bottom Left)
        npcLabel = new JLabel();
        npcLabel.setPreferredSize(new Dimension(200, 280)); // Fixed size for the portrait area
        npcLabel.setHorizontalAlignment(SwingConstants.CENTER);
        npcLabel.setVerticalAlignment(SwingConstants.BOTTOM); // NPC sits on the bottom of the portrait area
        loadNpcImage(true); // Load and scale the image to fit
        dialogueBoxContainer.add(npcLabel, BorderLayout.WEST);

        // 2. Text and Buttons (Center/East)
        JPanel textAndButtonsPanel = new JPanel(new BorderLayout(10, 10));
        textAndButtonsPanel.setOpaque(false);

        // --- Dialogue Text Area ---
        storyTextArea = new JTextArea();
        storyTextArea.setEditable(false);
        storyTextArea.setWrapStyleWord(true);
        storyTextArea.setLineWrap(true);
        storyTextArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        storyTextArea.setForeground(Color.WHITE);
        storyTextArea.setBackground(new Color(0, 0, 0, 180)); // Semi-transparent dark background for better text contrast
        storyTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(storyTextArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textAndButtonsPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Buttons (Bottom Right) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Back Button
        RpgButton backButton = new RpgButton("BACK TO SELECT", 28); // Increased Font Size
        backButton.setPreferredSize(new Dimension(250, 40)); // Set to 250 wide to match Continue
        backButton.addActionListener(e -> driver.changeState(MainGameDriver.GameState.SELECT_CHAR)); // Back to char select
        buttonPanel.add(backButton);

        // Continue Button
        RpgButton continueButton = new RpgButton("CONTINUE", 28);
        continueButton.setPreferredSize(new Dimension(250, 40));
        continueButton.addActionListener(e -> driver.changeState(MainGameDriver.GameState.PLAYING));
        buttonPanel.add(continueButton);

        textAndButtonsPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialogueBoxContainer.add(textAndButtonsPanel, BorderLayout.CENTER);

        add(dialogueBoxContainer, BorderLayout.SOUTH); // Dialogue box covers the bottom part of the screen
    }

    public void displayStory(String characterName) {
        String story = CHARACTER_STORIES.getOrDefault(characterName, "The narrator is busy. Proceed with caution!");
        String stats = CHARACTER_STATS.getOrDefault(characterName, "Stats unavailable.");

        // REVISED FORMAT: NPC speaks, then Character stats are displayed
        String fullDialogue = String.format(
                "%s speaks to %s:\n\n" +
                        "\"%s\"\n\n" +
                        "--- CHARACTER STATUS ---\n" +
                        "%s",
                NPC_NAME, characterName, story, stats
        );

        storyTextArea.setText(fullDialogue);
        storyTextArea.setCaretPosition(0); // Scroll to top
    }

    private void loadNpcImage(boolean scale) {
        // Loads the NPC image (assuming 'npc_jabines.png' is in 'res' folder)
        try {
            java.net.URL imgURL = getClass().getResource("/npc_jabines.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image originalImage = icon.getImage();

                if (scale) {
                    // Scale the image to fit the 200x280 panel height
                    Image scaledImage = originalImage.getScaledInstance(180, 260, Image.SCALE_SMOOTH);
                    npcLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    // Load at natural size (not used in this reverted version)
                    npcLabel.setIcon(icon);
                }
            } else {
                npcLabel.setText("NPC Image Missing");
            }
        } catch (Exception e) {
            System.err.println("Error loading NPC image.");
        }
    }

    // RpgButton Class (reused for consistent styling)
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
}