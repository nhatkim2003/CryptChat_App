package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private Color borderColor;

    public RoundedPanel() {
        setOpaque(false);
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        g2.dispose();
        super.paint(graphics);
    }

    public void add(FlatSVGIcon flatSVGIcon) {
        JLabel label = new JLabel();
        label.setIcon(flatSVGIcon);
        add(label);
    }

}
