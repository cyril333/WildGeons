package main;

import java.awt.*;
import java.util.Random;

public class Personnel extends GameEntity {

    private final Random random = new Random();
    private int moveTimer = 0;
    private int targetDx = 0;
    private int targetDy = 0;
    private final int PATROL_SPEED = 3;
    private final int CHANGE_DIR_INTERVAL = 90;

    private static final int BASE_HP = 750;
    private static final int BASE_DAMAGE = 100;
    private static final long ATTACK_INTERVAL = 5000;

    public Personnel(int x, int y, Image sprite) {
        super(x, y, 64, 64, BASE_HP, BASE_DAMAGE, ATTACK_INTERVAL, sprite, null);
    }

    public void update(int gameWidth, int gameHeight) {
        moveTimer++;

        if (moveTimer >= CHANGE_DIR_INTERVAL) {
            targetDx = random.nextInt(3) - 1; // -1, 0, or 1
            targetDy = random.nextInt(3) - 1; // -1, 0, or 1
            moveTimer = 0;
        }

        x += targetDx * PATROL_SPEED;
        y += targetDy * PATROL_SPEED;

        x = Math.max(0, Math.min(x, gameWidth - width));
        y = Math.max(60, Math.min(y, gameHeight - height));
    }
}