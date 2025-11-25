package main;

import javax.swing.*;
import java.awt.*;

/**
 * MainGameDriver - Manages the top-level JFrame and swaps between game screens
 * (MenuPanel, CharSelectPanel, StoryPanel, and GamePanel) based on the current GameState.
 */

public class MainGameDriver extends JFrame {

    // Define the possible states of the game
    public enum GameState {
        MENU,
        SELECT_CHAR,
        SHOW_STORY, // NEW STATE
        PLAYING
    }

    private GameState currentState = GameState.MENU;
    private String selectedCharacterName = "Bron";

    private final MenuPanel menuPanel;
    private final CharSelectPanel selectPanel;
    private final StoryPanel storyPanel; // NEW PANEL REFERENCE
    private final GamePanel gamePanel;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private static final String MENU_CARD = "MENU";
    private static final String SELECT_CARD = "SELECT";
    private static final String STORY_CARD = "STORY"; // NEW CARD NAME
    private static final String GAME_CARD = "GAME";

    public MainGameDriver() {
        setTitle("WildGeons");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);

        // Initialize all screen components
        menuPanel = new MenuPanel(this);
        selectPanel = new CharSelectPanel(this);
        storyPanel = new StoryPanel(this); // Initialize the new panel
        gamePanel = new GamePanel();

        // Add all panels to the main panel using CardLayout
        mainPanel.add(menuPanel, MENU_CARD);
        mainPanel.add(selectPanel, SELECT_CARD);
        mainPanel.add(storyPanel, STORY_CARD); // Add the new panel to the layout
        mainPanel.add(gamePanel, GAME_CARD);

        add(mainPanel);

        // Start in the Menu state
        cardLayout.show(mainPanel, MENU_CARD);

        setLocationRelativeTo(null);
        setVisible(true);
        menuPanel.requestFocusInWindow();
    }

    public void setSelectedCharacter(String charName) {
        this.selectedCharacterName = charName;
    }

    public String getSelectedCharacter() {
        return selectedCharacterName;
    }

    /**
     * Public method to switch the game state and view.
     */
    public void changeState(GameState newState) {
        if (this.currentState == newState) return;

        this.currentState = newState;

        if (newState == GameState.SELECT_CHAR) {
            gamePanel.stopGameLoop();
            cardLayout.show(mainPanel, SELECT_CARD);
            selectPanel.requestFocusInWindow();

        } else if (newState == GameState.SHOW_STORY) {

            // NEW FLOW: Character Select -> Story Panel
            // Prepare the story panel with the selected character's details
            storyPanel.displayStory(selectedCharacterName);
            cardLayout.show(mainPanel, STORY_CARD);
            storyPanel.requestFocusInWindow();

        } else if (newState == GameState.PLAYING) {
            // Flow: Story Panel -> Game
            cardLayout.show(mainPanel, GAME_CARD);

            // Pass the selected character's name to the game screen
            gamePanel.setPlayerCharacter(selectedCharacterName);
            gamePanel.startGameLoop();

            gamePanel.requestFocusInWindow();

        } else if (newState == GameState.MENU) {
            // Stop the game loop and switch back to the menu
            gamePanel.stopGameLoop();
            cardLayout.show(mainPanel, MENU_CARD);
            menuPanel.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(MainGameDriver::new);
    }
}