package com.example.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ImageAvatar extends JComponent {

    private Icon image;
    private int borderSize = 2;
    private Color borderColor = new Color(130, 130, 190, 223);
    private int avatarSize = 55; // Default avatar size
    private Color statusDotColor = new Color(0, 128, 0); // Color for the status dot
    private final int dotSize = 18; // Size of the status dot

    public Icon getImage() {
        return image;
    }

    public void setImage(Icon image) {
        this.image = image;
        repaint();
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public void setAvatarSize(int size) {
        this.avatarSize = size;
        revalidate();
        repaint();
    }

    public void setStatusDotColor(Color color) {
        this.statusDotColor = color;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(avatarSize, avatarSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            int diameter = avatarSize - borderSize * 2;

            // Create a circular image
            BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circularImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2.drawImage(toImage(image), 0, 0, diameter, diameter, null);
            g2.dispose();

            // Draw the circular image at the center of the component
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g.drawImage(circularImage, x, y, null);

            // Draw the border
            if (borderSize > 0) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(borderSize));
                g2d.drawOval(x, y, diameter, diameter);
                g2d.dispose();
            }

            // Adjusted dot position to overlap slightly within the bottom-right of the avatar border
            g.setColor(statusDotColor);
            int dotX = x + diameter - dotSize / 2 - borderSize / 2 - 10; // Fine-tune horizontal position
            int dotY = y + diameter - dotSize / 2 - borderSize / 2 - 8; // Fine-tune vertical position
            g.fillOval(dotX, dotY, dotSize, dotSize);
        }
    }

    private Image toImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return bufferedImage;
        }
    }
}
