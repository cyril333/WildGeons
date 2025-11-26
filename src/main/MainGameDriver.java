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
        SHOW_STORY,
        MAP_CLEARED_STORY, // New State for Map Cleared Dialogue
        PLAYING
    }
    private GameState currentState = GameState.MENU;
    private String selectedCharacterName = "Bron";

    // NOTE: Map number is fixed at 1 for simplicity and to avoid multi-map compilation errors.
    private final int currentMap = 1;

    private final MenuPanel menuPanel;
    private final CharSelectPanel selectPanel;
    private final StoryPanel storyPanel;
    private final GamePanel gamePanel;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private static final String MENU_CARD = "MENU";
    private static final String SELECT_CARD = "SELECT";
    private static final String STORY_CARD = "STORY";
    private static final String GAME_CARD = "GAME";

    public MainGameDriver() {
        setTitle("WildGeons");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
 
        // Initialize all screen components
        menuPanel = new MenuPanel(this);
        selectPanel = new CharSelectPanel(this);
        storyPanel = new StoryPanel(this);
        gamePanel = new GamePanel();

        // Add all panels to the main panel using CardLayout
        mainPanel.add(menuPanel, MENU_CARD);
        mainPanel.add(selectPanel, SELECT_CARD);
        mainPanel.add(storyPanel, STORY_CARD);
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
     * Helper method called by GamePanel when boss is defeated.
     * FIX: This method resolves the "cannot find symbol" error and is the core of the progression switch.
     */
    public void showMapCleared(String charName, int mapNumber) {
        // 1. CRITICAL: STOP THE THREAD FIRST
        gamePanel.stopGameLoop();

        // 2. Set the state and switch view
        currentState = GameState.MAP_CLEARED_STORY;

        // 3. Delegate message display to StoryPanel
        storyPanel.displayMapCleared(charName, mapNumber);

        // 4. Switch the panel AFTER the thread is stopped and the new panel is ready
        cardLayout.show(mainPanel, STORY_CARD);
    }


    /**
     * Public method to switch the game state and view.
     */
    public void changeState(GameState newState) {
        if (this.currentState == newState) return;

        // CRITICAL: Always stop the game thread when leaving the playing state
        if (this.currentState == GameState.PLAYING) {
            gamePanel.stopGameLoop();
        }

        this.currentState = newState;

        if (newState == GameState.SELECT_CHAR) {
            cardLayout.show(mainPanel, SELECT_CARD);
            selectPanel.requestFocusInWindow();

        } else if (newState == GameState.SHOW_STORY) {
            // Intro Story
            storyPanel.displayStory(selectedCharacterName);
            cardLayout.show(mainPanel, STORY_CARD);

        } else if (newState == GameState.PLAYING) {
            // Flow: Story Panel -> Game
            cardLayout.show(mainPanel, GAME_CARD);

            // Re-initialize the game for the selected character and current map (fixed at 1)
            gamePanel.setPlayerCharacter(selectedCharacterName);
            gamePanel.startGameLoop();

            gamePanel.requestFocusInWindow();

        } else if (newState == GameState.MENU) {
            // Switch back to the menu
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