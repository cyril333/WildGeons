package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public interface IDrawableEntity    {

    void draw(Graphics g, JComponent observer);
    void drawHealthBar(Graphics2D g2d, String name);
}
