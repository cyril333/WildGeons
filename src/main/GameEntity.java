package main;

import java.awt.*;
import javax.swing.JComponent;

public class GameEntity implements IDrawableEntity, IGameEntity {

    protected int x, y;
    protected int width, height;
    protected int hp;
    protected int maxHP;
    protected int damage;
    protected Image sprite;
    protected String playerName;

    protected long lastAttackTime = 0;
    protected long attackIntervalMs;

    public GameEntity(int x, int y, int width, int height, int hp, int damage, long attackIntervalMs, Image sprite, String playerName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hp = hp;
        this.maxHP = hp;
        this.damage = damage;
        this.attackIntervalMs = attackIntervalMs;
        this.sprite = sprite;
        this.playerName = playerName;
    }


    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void draw(Graphics g, JComponent observer) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, observer);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }
    }

    public void drawHealthBar(Graphics2D g2d, String name) {
        if (hp <= 0) return;

        int barWidth = width;
        int barHeight = 4;
        double hpRatio = (double)hp / maxHP;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y - barHeight - 2, barWidth, barHeight);

        if (hp > 0) {
            Color hpColor = (hpRatio > 0.5) ? Color.GREEN : (hpRatio > 0.2) ? Color.YELLOW : Color.RED;
            g2d.setColor(hpColor);
            g2d.fillRect(x, y - barHeight - 2, (int)(barWidth * hpRatio), barHeight);
        }

        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y - barHeight - 2, barWidth, barHeight);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, x, y + height + 12);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= attackIntervalMs;
    }

    public void attack(long currentTime) {
        lastAttackTime = currentTime;
    }

    // mga getters

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getDamage() {
        return damage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    // mga setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
    }
}