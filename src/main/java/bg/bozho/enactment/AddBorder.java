package bg.bozho.enactment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import bg.bozho.enactment.GenerateCards.Resource;

public class AddBorder {

    public static void main(String[] args) {
        String root = args[0];
        for (Resource resource : Resource.values()) {
            try {
                String filename = root + resource.toString().toLowerCase() + ".png";
                try (InputStream in = new FileInputStream(filename)) {
                    BufferedImage img = ImageIO.read(in);
                    Graphics2D g2 = img.createGraphics();
                    g2.setColor(Color.BLACK);
                    g2.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
                    ImageIO.write(img, "png", new File(filename));
                }
            } catch (Exception ex) {
            }
        }
    }
}
