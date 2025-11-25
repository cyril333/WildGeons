package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GamePanel - The core canvas, now refactored to use OOP Entity classes for Phased Combat.
 * Implements: Player movement, Phased Combat flow, Enemy Passive Damage, Boss Attack/Movement, Floating HP/MP Bars.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // --- NEW CLASS: DamagePopup ---
    private static class DamagePopup {
        final String text;
        final int x, y;
        final long startTime;
        final Color color;
        final int duration = 1000; // Visible for 1 second

        public DamagePopup(String text, int x, int y, Color color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.startTime = System.currentTimeMillis();
            this.color = color;
        }
    }
    private List<DamagePopup> popups = new ArrayList<>();
    // --- END DamagePopup ---


    // --- GAME CONSTANTS ---
    public enum GamePhase { PHASE_PERSONNEL, PHASE_BOSS, PHASE_CLEARED }
    private GamePhase currentPhase = GamePhase.PHASE_PERSONNEL;

    private final int PLAYER_SPRITE_SOURCE_SIZE = 32;
    private final int PLAYER_DISPLAY_SIZE = 64;
    private final int PLAYER_SPEED = 4;
    private final int HUD_HEIGHT = 60;

    private boolean upPressed, downPressed, leftPressed, rightPressed = false;
    private boolean isAttacking = false;
    private boolean isBossAttacking = false;

    private Thread gameThread;
    private volatile boolean isRunning = false;

    // --- OOP ENTITIES ---
    private GameEntity player;
    private Boss boss;
    private List<Personnel> personnelList = new ArrayList<>();

    // --- COMBAT & STATS ---
    private final int MAX_HP = 1500;
    private final int MAX_MANA = 500;
    private int currentHP = MAX_HP;
    private int currentMana = MAX_MANA;
    private final long ATTACK_COOLDOWN_MS = 4000;
    private final int BITS_MANA_COST = 30;
    private final int BITS_DAMAGE = 250;
    private long attackStartTime = 0;
    private final long ATTACK_DURATION_MS = 200;
    private long bossAttackStartTime = 0;
    private final long BOSS_ATTACK_DURATION_MS = 300;

    // --- ASSETS ---
    private Image mapBackground;
    private Image personnelSprite;
    private Image bossSprite;
    private Image playerSprite;

    private final String MAP_BG_PATH = "/map1_bg.png";
    private final String BOSS_PATH = "/chair_finalsprite.png";
    private final String PERSONNEL_PATH = "/personnel_finalsprite.png";

    private final Map<String, String> PLAYER_SPRITE_PATHS = Map.of(
            "Bron", "/bron_finalsprite.png",
            "Abdul", "/abdul_finalsprite.png",
            "Jamal", "/jamal_finalsprite.png"
    );

    private final Map<String, Color> PLAYER_COLOR_MAP = Map.of(
            "Bron", new Color(0, 153, 204), // Blue
            "Abdul", new Color(0, 204, 102), // Green
            "Jamal", new Color(255, 102, 0) // Orange
    );

    // --- UI/FLOW COMPONENTS ---
    private JButton gameOverButton;

    /** Loads asset using robust ImageIcon method. */
    private Image loadAsset(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                return new ImageIcon(url).getImage();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public GamePanel() {
        setLayout(null);
        loadStaticAssets();
        setFocusable(true);
        addKeyListener(this);
        initGameOverButton();
    }

    private void initGameOverButton() {
        gameOverButton = new JButton("BACK TO MENU");
        gameOverButton.setFont(new Font("Consolas", Font.BOLD, 24));
        gameOverButton.setBackground(new Color(62, 0, 0, 200));
        gameOverButton.setForeground(new Color(255, 255, 0));
        gameOverButton.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 2));
        gameOverButton.setFocusPainted(false);

        // Reset game state and return to menu
        gameOverButton.addActionListener(e -> {
            stopGameLoop();
            SwingUtilities.getWindowAncestor(this).dispose();
            EventQueue.invokeLater(main.MainGameDriver::new);
        });

        // Initial state: hidden
        gameOverButton.setVisible(false);
        gameOverButton.setBounds(300, 450, 200, 50); // Centered placement
        add(gameOverButton);
    }

    private void loadStaticAssets() {
        mapBackground = loadAsset(MAP_BG_PATH);
        bossSprite = loadAsset(BOSS_PATH);
        personnelSprite = loadAsset(PERSONNEL_PATH);
    }

    // --- INITIALIZATION AND FLOW METHODS ---

    public void setPlayerCharacter(String charName) {
        playerSprite = loadAsset(PLAYER_SPRITE_PATHS.getOrDefault(charName, "/bron_finalsprite.png"));

        // --- PLAYER INIT: Passed the charName to GameEntity constructor ---
        player = new GameEntity(
                400 - PLAYER_DISPLAY_SIZE / 2,
                300 - PLAYER_DISPLAY_SIZE / 2,
                PLAYER_DISPLAY_SIZE,
                PLAYER_DISPLAY_SIZE,
                MAX_HP,
                BITS_DAMAGE,
                ATTACK_COOLDOWN_MS,
                playerSprite,
                charName
        );

        // --- BOSS INIT: Using the new Boss class ---
        // Boss class itself will define its larger size (96x96)
        boss = new Boss(600, 150, bossSprite);

        currentPhase = GamePhase.PHASE_PERSONNEL;
        initializePersonnel();

        // Reset HP/MP on first load
        currentHP = MAX_HP;
        currentMana = MAX_MANA;
    }

    private void initializePersonnel() {
        personnelList.clear();
        // Spawning 3 Personnel entities using the larger size
        personnelList.add(new Personnel(100, 100, personnelSprite));
        personnelList.add(new Personnel(550, 450, personnelSprite));
        personnelList.add(new Personnel(150, 400, personnelSprite));
    }

    private void switchPhase() {
        if (currentPhase == GamePhase.PHASE_PERSONNEL && personnelList.isEmpty()) {
            currentPhase = GamePhase.PHASE_BOSS;

            // FIX 2: Reset HP and Mana to MAX when entering Boss Phase
            currentHP = MAX_HP;
            currentMana = MAX_MANA;

            System.out.println("--- PHASE SWITCHED! BOSS (Chair Pantaleon) HAS APPEARED! HP/MP RESTORED! ---");
        }
    }

    // --- GAME LOOP / UPDATE ---

    public void startGameLoop() {
        if (!isRunning) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameLoop() {
        isRunning = false;
    }

    @Override
    public void run() {
        final long targetTime = 1000 / 60;

        while (isRunning) {
            long start = System.currentTimeMillis();
            updateGameLogic();
            repaint();

            long elapsed = System.currentTimeMillis() - start;
            long waitTime = targetTime - elapsed;

            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateGameLogic() {
        if (player == null || currentHP <= 0) {
            if (currentHP <= 0) gameOverButton.setVisible(true);
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Personnel Movement/Logic
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                p.update(getWidth(), getHeight());
            }
            personnelList.removeIf(p -> p.getHP() <= 0);
            switchPhase();
        }

        // Boss Movement/Logic
        if (currentPhase == GamePhase.PHASE_BOSS) {
            if (boss.getHP() > 0) {
                boss.update(getWidth(), getHeight()); // Boss movement
                if (isBossAttacking && (currentTime - bossAttackStartTime) > BOSS_ATTACK_DURATION_MS) {
                    isBossAttacking = false;
                }
            }
        }

        // Player movement
        if (upPressed) player.setY(player.getY() - PLAYER_SPEED);
        if (downPressed) player.setY(player.getY() + PLAYER_SPEED);
        if (leftPressed) player.setX(player.getX() - PLAYER_SPEED);
        if (rightPressed) player.setX(player.getX() + PLAYER_SPEED);

        // Boundary Check
        player.setX(Math.max(0, Math.min(player.getX(), getWidth() - player.getWidth())));
        player.setY(Math.max(HUD_HEIGHT, Math.min(player.getY(), getHeight() - player.getHeight())));

        // Update attack state based on time
        if (isAttacking && (currentTime - attackStartTime) > ATTACK_DURATION_MS) {
            isAttacking = false;
        }

        // Update popups (move up and expire)
        popups.removeIf(p -> System.currentTimeMillis() - p.startTime > p.duration);

        // Apply enemy passive damage
        applyPassiveDamage(currentTime);
    }

    private void applyPassiveDamage(long currentTime) {
        if (player.getHP() <= 0) return;

        // --- PHASE 1: Personnel Passive Damage ---
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                if (player.getBounds().intersects(p.getBounds())) {
                    if (p.canAttack()) {
                        currentHP -= p.getDamage();
                        p.attack(currentTime);

                        popups.add(new DamagePopup(
                                "-" + p.getDamage(),
                                player.getX() + player.getWidth() / 2,
                                player.getY(),
                                Color.RED.brighter()
                        ));
                    }
                }
            }
        }

        // --- PHASE 2: Boss Passive Damage ---
        else if (currentPhase == GamePhase.PHASE_BOSS && boss.getHP() > 0) {
            if (player.getBounds().intersects(boss.getBounds())) {
                if (boss.canAttack()) {
                    currentHP -= boss.getDamage();
                    boss.attack(currentTime);

                    isBossAttacking = true;
                    bossAttackStartTime = currentTime;

                    popups.add(new DamagePopup(
                            "-" + boss.getDamage(),
                            player.getX() + player.getWidth() / 2,
                            player.getY(),
                            Color.RED.brighter()
                    ));
                }
            }
        }
    }

    // --- COMBAT LOGIC METHODS ---

    private void performBasicAttack() {
        if (player == null || currentHP <= 0) return;
        long currentTime = System.currentTimeMillis();

        if (currentTime - player.getLastAttackTime() < ATTACK_COOLDOWN_MS) { return; }
        if (currentMana < BITS_MANA_COST) { return; }

        currentMana -= BITS_MANA_COST;
        player.attack(currentTime);
        isAttacking = true;
        attackStartTime = currentTime;

        Rectangle attackBounds = new Rectangle(player.getX() + player.getWidth(), player.getY(), 50, player.getHeight());

        // Player attacks Personnel
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                if (p.getBounds().intersects(attackBounds) && p.getHP() > 0) {
                    p.takeDamage(BITS_DAMAGE);
                    popups.add(new DamagePopup(
                            "-" + BITS_DAMAGE,
                            p.getX() + p.getWidth() / 2,
                            p.getY(),
                            Color.YELLOW
                    ));
                }
            }
        }
        // Player attacks Boss
        else if (currentPhase == GamePhase.PHASE_BOSS) {
            if (boss.getBounds().intersects(attackBounds) && boss.getHP() > 0) {
                boss.takeDamage(BITS_DAMAGE);
                popups.add(new DamagePopup(
                        "-" + BITS_DAMAGE,
                        boss.getX() + boss.getWidth() / 2,
                        boss.getY(),
                        Color.YELLOW
                ));
            }
        }
    }

    // --- RENDERING ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw Map Background
        mapBackground = loadAsset(MAP_BG_PATH);
        if (mapBackground != null) {
            g2d.drawImage(mapBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Entities
        if (player == null) return;

        // Draw Personnel
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                p.draw(g2d, this);
                p.drawHealthBar(g2d, "Personnel");
            }
        }

        // Draw Boss
        if (currentPhase == GamePhase.PHASE_BOSS) {
            if (boss.getHP() > 0) {
                boss.draw(g2d, this);
                boss.drawHealthBar(g2d, "Chair (Pantaleon)");
            } else {
                drawMessage(g2d, "MAP 1 CLEARED!", Color.YELLOW);
                currentPhase = GamePhase.PHASE_CLEARED;
            }
        }

        // Draw Player
        if (currentHP > 0) {
            player.draw(g2d, this);
            drawPlayerStatusAboveEntity(g2d);
        } else {
            drawMessage(g2d, "GAME OVER!", Color.RED);
            // Button visibility handled in updateGameLogic
        }

        // Draw Attack Visual Effect (Bits)
        if (isAttacking) {
            Color attackColor = PLAYER_COLOR_MAP.getOrDefault(player.getPlayerName(), Color.BLUE);
            g2d.setColor(attackColor.brighter());
            g2d.fillRect(player.getX() + player.getWidth(), player.getY() + 10, 50, player.getHeight() - 20);
        }

        // Draw Boss Attack Visual Effect (Red Aura)
        if (isBossAttacking) {
            g2d.setColor(new Color(255, 0, 0, 150)); // Semi-transparent Red
            g2d.fillRect(boss.getX() - 10, boss.getY() - 10, boss.getWidth() + 20, boss.getHeight() + 20);
        }

        // Draw Damage Popups
        drawDamagePopups(g2d);

        drawHUD(g2d, getWidth());

        Toolkit.getDefaultToolkit().sync();
    }

    // --- DAMAGE POPUP RENDERING ---

    private void drawDamagePopups(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        long currentTime = System.currentTimeMillis();

        Iterator<DamagePopup> iterator = popups.iterator();
        while (iterator.hasNext()) {
            DamagePopup p = iterator.next();
            long timeElapsed = currentTime - p.startTime;

            if (timeElapsed > p.duration) {
                iterator.remove();
                continue;
            }

            // Calculate fade and rise effect
            float alpha = 1.0f - (float)timeElapsed / p.duration;
            int riseY = (int)(p.y - (timeElapsed / 10.0));

            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(255 * alpha)));
            g2d.drawString(p.text, p.x, riseY);
        }
    }


    // --- HUD/UI Drawing ---

    private void drawPlayerStatusAboveEntity(Graphics2D g2d) {
        int hpBarWidth = 50;
        int hpBarHeight = 6;
        int statusYOffset = -15;

        // HP Bar
        double hpRatio = (double)currentHP / MAX_HP;
        g2d.setColor(Color.RED);
        g2d.fillRect(player.getX() + (player.getWidth() - hpBarWidth) / 2, player.getY() + statusYOffset, (int)(hpBarWidth * hpRatio), hpBarHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(player.getX() + (player.getWidth() - hpBarWidth) / 2, player.getY() + statusYOffset, hpBarWidth, hpBarHeight);

        // Mana Bar (just below HP)
        double manaRatio = (double)currentMana / MAX_MANA;
        Color playerColor = PLAYER_COLOR_MAP.getOrDefault(player.getPlayerName(), Color.CYAN);
        g2d.setColor(playerColor);
        g2d.fillRect(player.getX() + (player.getWidth() - hpBarWidth) / 2, player.getY() + statusYOffset + hpBarHeight + 1, (int)(hpBarWidth * manaRatio), hpBarHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(player.getX() + (player.getWidth() - hpBarWidth) / 2, player.getY() + statusYOffset + hpBarHeight + 1, hpBarWidth, hpBarHeight);
    }

    private void drawHUD(Graphics2D g2d, int width) {
        Color playerColor = PLAYER_COLOR_MAP.getOrDefault(player.getPlayerName(), Color.CYAN);

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, width, HUD_HEIGHT);
        g2d.setColor(new Color(204, 153, 0));
        g2d.drawLine(0, HUD_HEIGHT, width, HUD_HEIGHT);

        // Name & Map Info (Left)
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        g2d.setColor(playerColor);
        g2d.drawString(player.getPlayerName() + " | Map 1: GLE The Ancient Building", 15, 35);

        // Phase Indicator (Rightmost)
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        g2d.setColor(Color.YELLOW);
        String phaseText = (currentPhase == GamePhase.PHASE_PERSONNEL) ?
                "Phase: Personnel (" + personnelList.size() + ")" :
                (currentPhase == GamePhase.PHASE_BOSS) ? "Phase: BOSS" :
                        "CLEARED";

        int textWidth = g2d.getFontMetrics().stringWidth(phaseText);
        g2d.drawString(phaseText, width - textWidth - 15, 35);
    }

    private void drawMessage(Graphics2D g2d, String message, Color color) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 60));
        g2d.setColor(color);
        int x = (getWidth() - g2d.getFontMetrics().stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(message, x, y);
    }


    // --- Input Handling ---

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) rightPressed = true;

        if (code == KeyEvent.VK_SPACE && currentHP > 0) {
            performBasicAttack();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { /* Not used */ }
}