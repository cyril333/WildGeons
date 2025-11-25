package main;

import java.awt.*;
import java.util.Random;

/**
 * Personnel - Specific Enemy Class inheriting from GameEntity.
 * Implements simple movement (Patrol) and passive attack logic specific to the Personnel enemy.
 */
public class Personnel extends GameEntity {

    private final Random random = new Random();
    private int moveTimer = 0;
    private int targetDx = 0;
    private int targetDy = 0;
    private final int PATROL_SPEED = 3; // INCREASED: Smoother movement
    private final int CHANGE_DIR_INTERVAL = 90; // DECREASED: Change direction every 1.5 seconds (was 2s)

    // Data from README: HP: 750, Damage: 100, Interval: 5s
    private static final int BASE_HP = 750;
    private static final int BASE_DAMAGE = 100;
    private static final long ATTACK_INTERVAL = 5000; // 5000 ms = 5 seconds

    public Personnel(int x, int y, Image sprite) {
        // FIX: Set size to 64x64 to match the player (Point 1)
        super(x, y, 64, 64, BASE_HP, BASE_DAMAGE, ATTACK_INTERVAL, sprite, null);
    }

    /**
     * Update function for movement/AI.
     */
    public void update(int gameWidth, int gameHeight) {
        moveTimer++;

        // Randomly change direction
        if (moveTimer >= CHANGE_DIR_INTERVAL) {
            targetDx = random.nextInt(3) - 1; // -1, 0, or 1
            targetDy = random.nextInt(3) - 1; // -1, 0, or 1
            moveTimer = 0;
        }

        // Apply movement
        x += targetDx * PATROL_SPEED;
        y += targetDy * PATROL_SPEED;

        // Basic Boundary Check (Keep enemy within the main map area)
        x = Math.max(0, Math.min(x, gameWidth - width));
        y = Math.max(60, Math.min(y, gameHeight - height));
    }
}