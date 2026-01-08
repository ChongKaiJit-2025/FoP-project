package com.app.ui;

import java.awt.*;
import javax.swing.*;

public class GradientPanel extends JPanel {
    private Color color1;
    private Color color2;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // always call super first
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}