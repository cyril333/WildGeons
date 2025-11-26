package main;

import java.awt.*;
import java.util.Random;


public class Boss extends GameEntity {

    private final Random random = new Random();
    private int moveTimer = 0;
    private int targetDx = 0;
    private int targetDy = 0;
    private final int PATROL_SPEED = 3;
    private final int CHANGE_DIR_INTERVAL = 90;

    private final int MIN_X = 100;
    private final int MAX_X = 700;
    private final int MIN_Y = 150;
    private final int MAX_Y = 500;

    private static final int BASE_HP = 1500;
    private static final int BASE_DAMAGE = 150;
    private static final long ATTACK_INTERVAL = 4000;

    public Boss(int x, int y, Image sprite) {
        super(x, y, 96, 96, BASE_HP, BASE_DAMAGE, ATTACK_INTERVAL, sprite, null);
    }


    public void update(int gameWidth, int gameHeight) {
        moveTimer++;

        if (moveTimer >= CHANGE_DIR_INTERVAL) {
            targetDx = random.nextInt(3) - 1;
            targetDy = random.nextInt(3) - 1;
            moveTimer = 0;
        }

        x += targetDx * PATROL_SPEED;
        y += targetDy * PATROL_SPEED;

        x = Math.max(MIN_X, Math.min(x, MAX_X - width));
        y = Math.max(MIN_Y, Math.min(y, MAX_Y - height));
    }
}