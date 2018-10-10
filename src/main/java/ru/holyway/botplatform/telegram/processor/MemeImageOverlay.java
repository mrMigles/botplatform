package ru.holyway.botplatform.telegram.processor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Worker that knows how to overlay text onto an image.
 */
public class MemeImageOverlay {

  private static final int MAX_FONT_SIZE = 84;
  private static final int BOTTOM_MARGIN = 40;
  private static final int TOP_MARGIN = 30;
  private static final int SIDE_MARGIN = 30;

  public static BufferedImage overlay(BufferedImage image, String topCaption, String bottomCaption)
      throws IOException, InterruptedException {
    BufferedImage copy = deepCopy(image);
    Graphics graphics = copy.getGraphics();
    drawStringCentered(graphics, topCaption, copy, true);
    drawStringCentered(graphics, bottomCaption, copy, false);
    return copy;
  }

  private static BufferedImage deepCopy(BufferedImage bi) {
    ColorModel cm = bi.getColorModel();
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }

  /**
   * Draws the given string centered, as big as possible, on either the top or bottom 20% of the
   * image given.
   */
  private static void drawStringCentered(Graphics g, String text, BufferedImage image, boolean top)
      throws InterruptedException {
    if (text == null) {
      text = "";
    }

    int height = 0;
    int fontSize = MAX_FONT_SIZE;
    int maxCaptionHeight = image.getHeight() / 5;
    int maxLineWidth = image.getWidth() - SIDE_MARGIN * 2;
    String formattedString = "";

    do {
      g.setFont(new Font("Arial", Font.BOLD, fontSize));

      // first inject newlines into the text to wrap properly
      StringBuilder sb = new StringBuilder();
      int left = 0;
      int right = text.length() - 1;
      while (left < right) {

        String substring = text.substring(left, right + 1);
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(substring, g);
        while (stringBounds.getWidth() > maxLineWidth) {
          if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
          }

          // look for a space to break the line
          boolean spaceFound = false;
          for (int i = right; i > left; i--) {
            if (text.charAt(i) == ' ') {
              right = i - 1;
              spaceFound = true;
              break;
            }
          }
          substring = text.substring(left, right + 1);
          stringBounds = g.getFontMetrics().getStringBounds(substring, g);

          // If we're down to a single word and we are still too wide,
          // the font is just too big.
          if (!spaceFound && stringBounds.getWidth() > maxLineWidth) {
            break;
          }
        }
        sb.append(substring).append("\n");
        left = right + 2;
        right = text.length() - 1;
      }

      formattedString = sb.toString();

      // now determine if this font size is too big for the allowed height
      height = 0;
      for (String line : formattedString.split("\n")) {
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line, g);
        height += stringBounds.getHeight();
      }
      fontSize--;
    } while (height > maxCaptionHeight);

    // draw the string one line at a time
    int y = 0;
    if (top) {
      y = TOP_MARGIN + g.getFontMetrics().getHeight();
    } else {
      y = image.getHeight() - height - BOTTOM_MARGIN + g.getFontMetrics().getHeight();
    }
    for (String line : formattedString.split("\n")) {
      // Draw each string twice for a shadow effect
      Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line, g);
      g.setColor(Color.BLACK);
      g.drawString(line, (image.getWidth() - (int) stringBounds.getWidth()) / 2 + 2, y + 2);
      g.setColor(Color.WHITE);
      g.drawString(line, (image.getWidth() - (int) stringBounds.getWidth()) / 2, y);
      y += g.getFontMetrics().getHeight();
    }
  }
}