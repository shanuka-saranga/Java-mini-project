package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;

public class ProfilePhotoFrame extends JComponent {

    private static final int FRAME_SIZE = 150;
    private static final int PADDING = 6;

    private final String emptyText;
    private Image image;

    public ProfilePhotoFrame(String emptyText) {
        this.emptyText = emptyText;
        setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
        setMinimumSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
        setOpaque(false);
    }

    public void setImagePath(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty() || !new File(imagePath).exists()) {
            clearImage();
            return;
        }

        image = new ImageIcon(imagePath).getImage();
        repaint();
    }

    public void clearImage() {
        image = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int size = Math.min(getWidth(), getHeight()) - 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        Ellipse2D outerCircle = new Ellipse2D.Double(x, y, size, size);
        Ellipse2D innerCircle = new Ellipse2D.Double(x + PADDING, y + PADDING, size - (PADDING * 2), size - (PADDING * 2));

        g2.setColor(AppTheme.AVATAR_BG);
        g2.fill(innerCircle);

        Shape previousClip = g2.getClip();
        g2.setClip(innerCircle);
        if (image != null) {
            drawCoverImage(g2, image, innerCircle.getBounds());
        }
        g2.setClip(previousClip);

        g2.setColor(AppTheme.AVATAR_BORDER);
        g2.setStroke(new BasicStroke(4f));
        g2.draw(outerCircle);

        if (image == null) {
            g2.setColor(AppTheme.AVATAR_TEXT);
            g2.setFont(AppTheme.fontPlain(14));
            FontMetrics metrics = g2.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(emptyText)) / 2;
            int textY = (getHeight() + metrics.getAscent()) / 2 - 4;
            g2.drawString(emptyText, textX, textY);
        }

        g2.dispose();
    }

    private void drawCoverImage(Graphics2D g2, Image sourceImage, Rectangle bounds) {
        int imageWidth = sourceImage.getWidth(null);
        int imageHeight = sourceImage.getHeight(null);
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        double scale = Math.max(
                (double) bounds.width / imageWidth,
                (double) bounds.height / imageHeight
        );

        int drawWidth = (int) Math.round(imageWidth * scale);
        int drawHeight = (int) Math.round(imageHeight * scale);
        int drawX = bounds.x + (bounds.width - drawWidth) / 2;
        int drawY = bounds.y + (bounds.height - drawHeight) / 2;

        g2.drawImage(sourceImage, drawX, drawY, drawWidth, drawHeight, null);
    }
}
