package main;

import java.awt.*;
import java.util.Random;

/**
 * Boss - Specific Enemy Class inheriting from GameEntity.
 * Implements aggressive movement and higher stats for the Map 1 Boss (Chair Pantaleon).
 */
public class Boss extends GameEntity {

    private final Random random = new Random();
    private int moveTimer = 0;
    private int targetDx = 0;
    private int targetDy = 0;
    private final int PATROL_SPEED = 3; // Aggressive speed to chase the player
    private final int CHANGE_DIR_INTERVAL = 90; // Change direction more often (smoother AI)

    // --- MOVEMENT BOUNDARIES ---
    // Boss will stay roughly in the center of the arena for a better fight
    private final int MIN_X = 100;
    private final int MAX_X = 700;
    private final int MIN_Y = 150;
    private final int MAX_Y = 500;

    // Data from README: Passive Attack: 150 DMG (Increased threat), Interval: 4s
    private static final int BASE_HP = 1500;
    private static final int BASE_DAMAGE = 150; // Increased damage from 120 to 150
    private static final long ATTACK_INTERVAL = 4000; // 4 seconds

    public Boss(int x, int y, Image sprite) {
        // Boss is 96x96, has 1500 HP, 150 DMG, 4s interval
        super(x, y, 96, 96, BASE_HP, BASE_DAMAGE, ATTACK_INTERVAL, sprite, null);
    }

    /**
     * Update function for movement/AI.
     */
    public void update(int gameWidth, int gameHeight) {
        moveTimer++;

        // Randomly change direction
        if (moveTimer >= CHANGE_DIR_INTERVAL) {
            targetDx = random.nextInt(3) - 1;
            targetDy = random.nextInt(3) - 1;
            moveTimer = 0;
        }

        // Apply movement
        x += targetDx * PATROL_SPEED;
        y += targetDy * PATROL_SPEED;

        // --- BOUNDARY CHECK ---
        // Ensure Boss stays within the center of the arena (100 < x < 700, 150 < y < 500)
        x = Math.max(MIN_X, Math.min(x, MAX_X - width));
        y = Math.max(MIN_Y, Math.min(y, MAX_Y - height));
    }
}