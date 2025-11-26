package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class StoryPanel extends JPanel {

    private final MainGameDriver driver;
    private final JTextArea storyTextArea;
    private final JLabel npcLabel;
    private final RpgButton continueButton;
    private final RpgButton backButton;

    private final String NPC_JABINES = "npc_jabines.png";
    private final String NPC_LEGASPINO = "npc_legaspino.png";

    private final String NPC_NAME_JABINES = "Snyd Jabines (Merchant)";
    private final String NPC_NAME_LEGASPINO = "Alsen Blythe Legaspino (Narrator)";

    private final Map<String, String> CHARACTER_STORIES = Map.of(
            "Bron", "Welcome, determined IT Student! Bron is a determined student who dreams of earning his “certification.” To achieve it, he must conquer every subject that stands in his way.",
            "Abdul", "Developer in training, welcome. Abdul is a hardworking CIT student fighting through stress and challenges. His dream is to become a developer who will make the world a better place.",
            "Jamal", "Jamal, a shiftee from another program, has finally found his passion. Now he fights to prove his worth."
    );

    private final Map<String, String> CHARACTER_STATS = Map.of(
            "Bron", "HP: 1500 | Mana: 500\nBasic: Bits (500 DMG, 4s CD)\nIntermediate: Syntax Error (230 DMG, 12s CD)\nUltimate: RJ45 Bomb (550 DMG, 60s CD)",
            "Abdul", "HP: 1500 | Mana: 500\nBasic: Server Guard (500 DMG, 4s CD)\nIntermediate: Abstract Invi (250 DMG, 15s CD)\nUltimate: Automata (550 DMG, 60s CD)",
            "Jamal", "HP: 1500 | Mana: 500\nBasic: Wire Whip (500 DMG, 4s CD)\nIntermediate: Trouble Shoot (+500 HP, 20s CD)\nUltimate: Robo Minion (400 DMG per hit, 60s CD)"
    );

    public StoryPanel(MainGameDriver driver) {
        this.driver = driver;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 0, 0));

        JPanel gameViewPlaceholder = new JPanel();
        gameViewPlaceholder.setBackground(new Color(20, 0, 0));
        add(gameViewPlaceholder, BorderLayout.CENTER);

        JPanel dialogueBoxContainer = new JPanel(new BorderLayout());
        dialogueBoxContainer.setPreferredSize(new Dimension(800, 300));
        dialogueBoxContainer.setBackground(new Color(62, 0, 0, 200));
        dialogueBoxContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        npcLabel = new JLabel();
        npcLabel.setPreferredSize(new Dimension(200, 280));
        npcLabel.setHorizontalAlignment(SwingConstants.CENTER);
        npcLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        loadNpcImage(NPC_JABINES);
        dialogueBoxContainer.add(npcLabel, BorderLayout.WEST);

        JPanel textAndButtonsPanel = new JPanel(new BorderLayout(10, 10));
        textAndButtonsPanel.setOpaque(false);

        storyTextArea = new JTextArea();
        storyTextArea.setEditable(false);
        storyTextArea.setWrapStyleWord(true);
        storyTextArea.setLineWrap(true);
        storyTextArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        storyTextArea.setForeground(Color.WHITE);
        storyTextArea.setBackground(new Color(0, 0, 0, 180));
        storyTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(storyTextArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textAndButtonsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        backButton = new RpgButton("BACK TO SELECT", 28);
        backButton.setPreferredSize(new Dimension(250, 40));
        backButton.addActionListener(e -> driver.changeState(MainGameDriver.GameState.SELECT_CHAR));
        buttonPanel.add(backButton);

        continueButton = new RpgButton("CONTINUE", 28);
        continueButton.setPreferredSize(new Dimension(250, 40));
        buttonPanel.add(continueButton);

        textAndButtonsPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialogueBoxContainer.add(textAndButtonsPanel, BorderLayout.CENTER);

        add(dialogueBoxContainer, BorderLayout.SOUTH);
    }

    public void displayStory(String characterName) {
        loadNpcImage(NPC_JABINES);

        String story = CHARACTER_STORIES.getOrDefault(characterName, "The narrator is busy. Proceed with caution!");
        String stats = CHARACTER_STATS.getOrDefault(characterName, "Stats unavailable.");

        String fullDialogue = String.format(
                "--- MAP 1: GLE The Ancient Building ---\n\n" +
                        "**%s speaks to %s:**\n\n" +
                        "\"%s\"\n\n" +
                        "--- CHARACTER STATUS ---\n" +
                        "%s",
                NPC_NAME_JABINES, characterName, story, stats
        );
        storyTextArea.setText(fullDialogue);
        storyTextArea.setCaretPosition(0);

        for (java.awt.event.ActionListener al : continueButton.getActionListeners()) {
            continueButton.removeActionListener(al);
        }
        continueButton.setText("CONTINUE");
        continueButton.addActionListener(e -> driver.changeState(MainGameDriver.GameState.PLAYING));
        backButton.setVisible(true);
    }

    public void displayMapCleared(String characterName, int clearedMap) {
        loadNpcImage(NPC_LEGASPINO);

        String message = String.format(
                "--- MAP %d CLEARED! ---\n\n" +
                        "**%s speaks to %s:**\n\n" +
                        "\"Congratulations, %s! You have conquered the challenges of Map %d and obtained the required course units. The path to the Diploma continues.\"",
                clearedMap, NPC_NAME_LEGASPINO, characterName, characterName, clearedMap
        );
        storyTextArea.setText(message);

        for (java.awt.event.ActionListener al : continueButton.getActionListeners()) {
            continueButton.removeActionListener(al);
        }
        backButton.setVisible(false);
        continueButton.setText("RETURN TO MENU");

        continueButton.addActionListener(e -> {
            driver.changeState(MainGameDriver.GameState.MENU);
        });
    }

    private void loadNpcImage(String npcFileName) {
        try {
            java.net.URL imgURL = getClass().getResource("/" + npcFileName);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image originalImage = icon.getImage();

                Image scaledImage = originalImage.getScaledInstance(180, 260, Image.SCALE_SMOOTH);
                npcLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                npcLabel.setText("NPC Image Missing");
            }
        } catch (Exception e) {
            System.err.println("Error loading NPC image.");
        }
    }

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