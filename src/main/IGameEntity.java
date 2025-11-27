package main;

import java.awt.Rectangle;

public interface IGameEntity {


    void move(int dx, int dy);
    boolean canAttack();
    void attack(long currentTime);
    void takeDamage(int damage);

    int getX();
    int getY();
    int getHP();
    int getMaxHP();
    int getDamage();
    int getWidth();
    int getHeight();
    long getLastAttackTime();
    String getPlayerName();
    Rectangle getBounds();

    void setX(int x);
    void setY(int y);
}
