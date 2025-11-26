package main;

import javax.swing.*;
import java.awt.*;

public class MainGameDriver extends JFrame {

    public enum GameState {
        MENU,
        SELECT_CHAR,
        SHOW_STORY,
        MAP_CLEARED_STORY,
        PLAYING
    }
    private GameState currentState = GameState.MENU;
    private String selectedCharacterName = "Bron";

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

        menuPanel = new MenuPanel(this);
        selectPanel = new CharSelectPanel(this);
        storyPanel = new StoryPanel(this);
        gamePanel = new GamePanel();

        mainPanel.add(menuPanel, MENU_CARD);
        mainPanel.add(selectPanel, SELECT_CARD);
        mainPanel.add(storyPanel, STORY_CARD);
        mainPanel.add(gamePanel, GAME_CARD);

        add(mainPanel);

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

    public void showMapCleared(String charName, int mapNumber) {
        // incase ra ni pang stop sa game
        gamePanel.stopGameLoop();

        // switch state if clear
        currentState = GameState.MAP_CLEARED_STORY;

        // npc alsen mo pop up after ma defeat map 1
        storyPanel.displayMapCleared(charName, mapNumber);

        cardLayout.show(mainPanel, STORY_CARD);
    }

    public void changeState(GameState newState) {
        if (this.currentState == newState) return;

        if (this.currentState == GameState.PLAYING) {
            gamePanel.stopGameLoop();
        }

        this.currentState = newState;

        if (newState == GameState.SELECT_CHAR) {
            cardLayout.show(mainPanel, SELECT_CARD);
            selectPanel.requestFocusInWindow();

        } else if (newState == GameState.SHOW_STORY) {
            storyPanel.displayStory(selectedCharacterName);
            cardLayout.show(mainPanel, STORY_CARD);

        } else if (newState == GameState.PLAYING) {
            cardLayout.show(mainPanel, GAME_CARD);

            gamePanel.setPlayerCharacter(selectedCharacterName);
            gamePanel.startGameLoop();

            gamePanel.requestFocusInWindow();

        } else if (newState == GameState.MENU) {
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