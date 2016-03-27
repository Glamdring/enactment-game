package bg.bozho.enactment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.imageio.ImageIO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import bg.bozho.enactment.GenerateCards.Resource;

public class GenerateBoard {
    private static Font font = new Font("Arial", Font.BOLD, 18);
    
    public static void main(String[] args) throws IOException {
        BufferedImage img = new BufferedImage(595, 843, BufferedImage.TYPE_INT_ARGB);
        InputStream csvStream = GenerateCards.class.getResourceAsStream("/board.csv");
        final Reader reader = new InputStreamReader(new BOMInputStream(csvStream), "UTF-8");

        Graphics2D g2 = img.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, img.getWidth(), img.getHeight());
        g2.setColor(Color.BLACK);
        g2.setFont(font);
        
        try (CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
            int offset = 0;
            for (CSVRecord record : parser.getRecords()) {
                String phase = record.get("phase");
                if (phase.equals("First reading in parliamentary committee I")) {
                    offset += 1; //2nd segment
                }
                int steps = Integer.parseInt(record.get("steps"));
                g2.drawString(phase, 15, 25 + offset * 30 + 5);
                for (int i = 0; i < steps; i ++) {
                    g2.drawRect(10, 10 + offset * 30, img.getWidth() - 17, 30);
                    int start = img.getWidth() - 6 * 30 - 5;
                    for (int k = 0; k < 6; k++) {
                        g2.drawOval(start + k * 30, 8 + offset * 30 + 5, 25, 25);
                    }
                    offset++;
                }
                
            }
        }
        
        GenerateCards.initlializeResourceImages();
        g2.drawImage(GenerateCards.resourcesImages.get(Resource.STOP), 30, img.getHeight() - 85, null);
        g2.drawOval(70, img.getHeight() - 100, 60, 60);
        
        ImageIO.write(img, "png", new File(GenerateCards.OUTPUT_DIR + "board.png"));
    }
}
