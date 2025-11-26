package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GamePanel - The core canvas, now refactored to use OOP Entity classes for Phased Combat.
 * FINAL VERSION with robust crash prevention checks and optimized game loop.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // --- NEW CONSTANT: Locked to 60 FPS for smooth gameplay ---
    private static final int TARGET_FPS = 60;

    // --- GAME CONSTANTS ---
    public enum GamePhase { PHASE_PERSONNEL, PHASE_BOSS, MAP_CLEARED, GAME_OVER }
    private GamePhase currentPhase = GamePhase.PHASE_PERSONNEL;

    private final int PLAYER_DISPLAY_SIZE = 64;
    private final int PLAYER_SPEED = 4;
    private final int BOSS_SIZE = 96;
    private final int ENEMY_SIZE = 64;
    private final int HUD_HEIGHT = 60;

    private boolean upPressed, downPressed, leftPressed, rightPressed = false;
    private boolean isAttacking = false;

    private Thread gameThread;
    private volatile boolean isRunning = false;

    // --- OOP ENTITIES ---
    private GameEntity player;
    private GameEntity boss;
    private List<Personnel> personnelList = new ArrayList<>();

    // --- COMBAT & STATS ---
    private final int MAX_HP = 1500;
    private final int MAX_MANA = 500;
    private int currentHP = MAX_HP;
    private int currentMana = MAX_MANA;
    private final long ATTACK_COOLDOWN_MS = 4000;
    private final int BITS_MANA_COST = 30;
    private final int BITS_DAMAGE = 800; // SYNCHRONIZED: Damage set to 800 for quick testing
    private long attackStartTime = 0;
    private final long ATTACK_DURATION_MS = 200;
    private long bossAttackStartTime = 0;

    // --- DAMAGE POPUPS ---
    private static class DamagePopup {
        final String text;
        final int x, y;
        final long startTime;
        final Color color;
        final int duration = 1000;

        public DamagePopup(String text, int x, int y, Color color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.startTime = System.currentTimeMillis();
            this.color = color;
        }
    }
    private List<DamagePopup> popups = new ArrayList<>();

    // --- ASSETS ---
    private final Map<String, Image> assetCache = new ConcurrentHashMap<>();

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
    private JButton flowButton;

    private Image loadAsset(String path) {
        if (assetCache.containsKey(path)) return assetCache.get(path);
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                assetCache.put(path, img);
                return img;
            }
        } catch (Exception e) { /* ignored */ }
        return null;
    }

    public GamePanel() {
        setLayout(null);
        loadStaticAssets();
        setFocusable(true);
        addKeyListener(this);
        initFlowButton();
    }

    private void initFlowButton() {
        flowButton = new JButton();
        flowButton.setFont(new Font("Consolas", Font.BOLD, 24));

        // Default Button Styling (will be overridden in rendering)
        flowButton.setBackground(new Color(62, 0, 0, 200));
        flowButton.setForeground(new Color(255, 255, 0));
        flowButton.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 2));

        flowButton.setFocusPainted(false);
        flowButton.setVisible(false);
        flowButton.setBounds(300, 450, 200, 50);

        flowButton.addActionListener(e -> {
            stopGameLoop();
            if (SwingUtilities.getWindowAncestor(this) instanceof MainGameDriver driver) {
                driver.changeState(MainGameDriver.GameState.MENU);
            }
        });

        add(flowButton);
    }

    private void loadStaticAssets() {
        loadAsset(MAP_BG_PATH);
        loadAsset(BOSS_PATH);
        loadAsset(PERSONNEL_PATH);
        for(String path : PLAYER_SPRITE_PATHS.values()) {
            loadAsset(path);
        }
    }

    // --- INITIALIZATION AND FLOW METHODS ---

    public void setPlayerCharacter(String charName) {
        currentHP = MAX_HP;
        currentMana = MAX_MANA;
        currentPhase = GamePhase.PHASE_PERSONNEL;

        // FIX: Clear leftover popups and reset boss attack visual on new game
        popups.clear();
        flowButton.setVisible(false);
        bossAttackStartTime = 0; // CRITICAL FIX: Reset the visual effect start time

        Image playerImg = loadAsset(PLAYER_SPRITE_PATHS.getOrDefault(charName, "/bron_finalsprite.png"));

        // --- PLAYER INIT ---
        player = new GameEntity(
                400 - PLAYER_DISPLAY_SIZE / 2,
                300 - PLAYER_DISPLAY_SIZE / 2,
                PLAYER_DISPLAY_SIZE,
                PLAYER_DISPLAY_SIZE,
                MAX_HP,
                BITS_DAMAGE,
                ATTACK_COOLDOWN_MS,
                playerImg,
                charName
        );

        // --- BOSS INIT ---
        boss = new Boss(600, 150, assetCache.get(BOSS_PATH));

        initializePersonnel();
    }

    private void initializePersonnel() {
        personnelList.clear();
        personnelList.add(new Personnel(100, 100, assetCache.get(PERSONNEL_PATH)));
        personnelList.add(new Personnel(550, 450, assetCache.get(PERSONNEL_PATH)));
        personnelList.add(new Personnel(150, 400, assetCache.get(PERSONNEL_PATH)));
    }

    private void switchPhase() {
        if (currentPhase == GamePhase.PHASE_PERSONNEL && personnelList.isEmpty()) {
            currentPhase = GamePhase.PHASE_BOSS;
            currentHP = MAX_HP;
            currentMana = MAX_MANA;
            System.out.println("--- PHASE SWITCHED! BOSS (Chair Pantaleon) HAS APPEARED! ---");
        } else if (currentPhase == GamePhase.PHASE_BOSS && boss != null && boss.getHP() <= 0) {
            currentPhase = GamePhase.MAP_CLEARED;

            // CRITICAL FIX: Set boss to null immediately to prevent access
            boss = null;

            // Trigger Map Cleared Screen via driver
            if (SwingUtilities.getWindowAncestor(this) instanceof MainGameDriver driver) {
                SwingUtilities.invokeLater(() -> driver.showMapCleared(player.getPlayerName(), 1));
            }
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
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * FIX: Refactored to use high-precision Nanosecond timing for a stable 60 FPS update rate,
     * which drastically reduces CPU usage and overheating.
     */
    @Override
    public void run() {
        final double NANO_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS; // Use custom target FPS (60 FPS)
        long lastTime = System.nanoTime();
        double delta = 0;

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / NANO_PER_UPDATE;
            lastTime = now;

            // Update logic loop: ensures updateGameLogic runs at TARGET_FPS
            while (delta >= 1) {
                updateGameLogic();
                delta--;
            }

            // Repaint the screen (drawing)
            repaint();

            // Introduce a small sleep if we are ahead of schedule to yield CPU time
            long timeTaken = System.nanoTime() - now;
            long sleepTime = (long)(NANO_PER_UPDATE - timeTaken) / 1_000_000;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else if (sleepTime < -5) {
                // If we are significantly behind, we still yield to prevent the thread from spinning
                Thread.yield();
            }
        }
    }


    private void updateGameLogic() {
        switchPhase();

        if (currentPhase == GamePhase.GAME_OVER || currentPhase == GamePhase.MAP_CLEARED) {
            return;
        }

        applyPassiveDamage();

        // Update phase logic (Personnel movement, Boss movement)
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                p.update(getWidth(), getHeight());
            }
            personnelList.removeIf(p -> p.getHP() <= 0);
        } else if (currentPhase == GamePhase.PHASE_BOSS && boss != null) {
            if (boss instanceof Boss) {
                ((Boss) boss).update(getWidth(), getHeight());
            }
            bossAttackLogic();
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
        if (isAttacking && System.currentTimeMillis() - attackStartTime > ATTACK_DURATION_MS) {
            isAttacking = false;
        }

        // Update popups (move up and expire)
        popups.removeIf(p -> System.currentTimeMillis() - p.startTime > p.duration);
    }

    private void applyPassiveDamage() {
        if (currentHP <= 0) {
            currentPhase = GamePhase.GAME_OVER;
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Personnel Passive Damage
        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                if (player.getBounds().intersects(p.getBounds()) && p.getHP() > 0) {
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

        // Boss Passive Damage
        else if (currentPhase == GamePhase.PHASE_BOSS && boss != null && boss.getHP() > 0) {
            if (player.getBounds().intersects(boss.getBounds())) {
                if (boss.canAttack()) {
                    currentHP -= boss.getDamage();
                    boss.attack(currentTime);
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

    private void bossAttackLogic() {
        // Boss Attack Visual End
        if (System.currentTimeMillis() - bossAttackStartTime > 300) {
            bossAttackStartTime = 0;
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

        // Attack range rectangle (50 units wide, to the right of the player)
        Rectangle attackBounds = new Rectangle(player.getX() + player.getWidth(), player.getY(), 50, player.getHeight());

        // Player attacks enemies
        int damage = BITS_DAMAGE;

        if (currentPhase == GamePhase.PHASE_PERSONNEL) {
            for (Personnel p : personnelList) {
                if (p.getBounds().intersects(attackBounds) && p.getHP() > 0) {
                    p.takeDamage(damage);
                    popups.add(new DamagePopup("-" + damage, p.getX() + p.getWidth() / 2, p.getY(), Color.YELLOW));
                }
            }
        }
        else if (currentPhase == GamePhase.PHASE_BOSS) {
            if (boss != null && boss.getBounds().intersects(attackBounds) && boss.getHP() > 0) {
                boss.takeDamage(damage);
                popups.add(new DamagePopup("-" + damage, boss.getX() + boss.getWidth() / 2, boss.getY(), Color.YELLOW));
            }
        }
    }

    // --- RENDERING ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw Map Background
        Image mapImg = assetCache.get(MAP_BG_PATH);
        if (mapImg != null) {
            g2d.drawImage(mapImg, 0, 0, getWidth(), getHeight(), this);
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
        if (currentPhase == GamePhase.PHASE_BOSS && boss != null) {
            if (boss.getHP() > 0) {
                boss.draw(g2d, this);
                boss.drawHealthBar(g2d, "Chair (Pantaleon)");
            }
        }

        // Draw Player
        if (currentHP > 0) {
            player.draw(g2d, this);
            drawPlayerStatusAboveEntity(g2d);
        }


        // Draw Attack Visual Effect (Bits)
        if (isAttacking) {
            Color attackColor = PLAYER_COLOR_MAP.getOrDefault(player.getPlayerName(), Color.BLUE);
            g2d.setColor(attackColor.brighter());
            g2d.fillRect(player.getX() + player.getWidth(), player.getY() + 10, 50, player.getHeight() - 20);
        }

        // Draw Boss Attack Visual Effect (Red Flash)
        if (bossAttackStartTime > 0 && boss != null) {
            g2d.setColor(Color.RED.darker());
            g2d.fillRect(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
        }

        // Draw Damage Popups
        drawDamagePopups(g2d);

        // Draw Game Over / Map Cleared Message
        if (currentPhase == GamePhase.GAME_OVER) {
            drawMessage(g2d, "GAME OVER!", Color.RED);
            flowButton.setText("BACK TO MENU");

            // --- FIX: Change button color for Game Over ---
            flowButton.setBackground(new Color(150, 0, 0)); // Dark Red Background
            flowButton.setForeground(Color.BLACK); // White Text
            flowButton.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // Red Border

            flowButton.setVisible(true);
        } else if (currentPhase == GamePhase.MAP_CLEARED) {
            drawMessage(g2d, "MAP CLEARED!", Color.YELLOW);

            // --- FIX: Change button color back to Gold/Maroon for Map Cleared ---
            flowButton.setBackground(new Color(62, 0, 0, 200));
            flowButton.setForeground(new Color(255, 255, 0));
            flowButton.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 0), 3));

            // The button is hidden because we switch to story, but this sets the default if needed
            flowButton.setVisible(false);
        }

        drawHUD(g2d, getWidth());

        Toolkit.getDefaultToolkit().sync();
    }

    // --- HUD/UI Drawing ---

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

            float alpha = 1.0f - (float)timeElapsed / p.duration;
            int riseY = (int)(p.y - (timeElapsed / 10.0));

            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(255 * alpha)));
            g2d.drawString(p.text, p.x, riseY);
        }
    }

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