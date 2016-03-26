package bg.bozho.enactment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class GenerateCards {
    private static final int ICON_WIDTH_HEIGHT = 32;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 280;
    
    private static Font font = new Font("Arial", Font.BOLD, 20);
    
    private static Map<Resource, BufferedImage> resourcesImages = new HashMap<>();
    
    public static void main(String[] args) throws Exception {
        initlializeResourceImages();
        
        InputStream csvStream = GenerateCards.class.getResourceAsStream("/cards.csv");
        final Reader reader = new InputStreamReader(new BOMInputStream(csvStream), "UTF-8");

        try (CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader())) {
            for (CSVRecord record : parser.getRecords())  { 
                int count = Integer.parseInt(record.get("Count"));
                for (int i = 0; i < count; i ++) {
                    BufferedImage card = ImageIO.read(GenerateCards.class.getResourceAsStream("/blank.png"));
                    
                    String text = record.get("Text");
                    int influence = Integer.parseInt(record.get("influence"));
                    int capacity = Integer.parseInt(record.get("capacity"));
                    int financialResources = Integer.parseInt(record.get("financial resources"));
                    int popularity = Integer.parseInt(record.get("popularity"));
                    int luck = Integer.parseInt(record.get("luck"));
                    int morality = Integer.parseInt(record.get("morality"));
                    
                    // effects
                    int influenceEffect = Integer.parseInt(record.get("influence-e"));
                    int capacityEffect = Integer.parseInt(record.get("capacity-e"));
                    int financialResourcesEffect = Integer.parseInt(record.get("financial resources-e"));
                    int popularityEffect = Integer.parseInt(record.get("popularity-e"));
                    int moveEffect = Integer.parseInt(record.get("move"));
                    int targetEffect = Integer.parseInt(record.get("target"));
                    
                    String playableInSegment = record.get("Playable in segment");

                    int resourceCount = 0;
                    Graphics2D g2 = card.createGraphics();
                    g2.setFont(font);
                    g2.setColor(Color.BLACK);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    resourceCount = drawResource(influence, Resource.INFLUENCE, resourceCount, g2);
                    resourceCount = drawResource(capacity, Resource.CAPACITY, resourceCount, g2);
                    resourceCount = drawResource(popularity, Resource.POPULARITY, resourceCount, g2);
                    resourceCount = drawResource(financialResources, Resource.MONEY, resourceCount, g2);
                    resourceCount = drawResource(luck, Resource.LUCK, resourceCount, g2);
                    resourceCount = drawResource(morality, Resource.MORALITY, resourceCount, g2);
                    
                    drawText(text, g2);
                    
                    int effectCount = 0;
                    int totalEffect = Math.abs(moveEffect) + Math.abs(targetEffect) + Math.abs(influenceEffect)
                                    + Math.abs(capacityEffect) + Math.abs(popularityEffect)
                                    + Math.abs(financialResourcesEffect);
                    
                    effectCount = drawEffect(moveEffect, Resource.MOVE, effectCount, totalEffect, g2);
                    effectCount = drawEffect(targetEffect, Resource.TARGET, effectCount, totalEffect, g2);
                    effectCount = drawEffect(influenceEffect, Resource.INFLUENCE, effectCount, totalEffect, g2);
                    effectCount = drawEffect(capacityEffect, Resource.CAPACITY, effectCount, totalEffect, g2);
                    effectCount = drawEffect(popularityEffect, Resource.POPULARITY, effectCount, totalEffect, g2);
                    effectCount = drawEffect(financialResourcesEffect, Resource.MONEY, effectCount, totalEffect, g2);
                    
                    drawEffectSign(moveEffect + targetEffect + influenceEffect + capacityEffect + popularityEffect
                            + financialResourcesEffect, g2);
                    
                    drawPlayableInSegment(playableInSegment, g2);
                    
                    ImageIO.write(card, "png", new File("c:/tmp/cards/" + text.replace(" ", "_") + i + ".png"));
                }
            }
        }
    }

    private static void drawPlayableInSegment(String playableInSegment, Graphics2D g2) {
        if (StringUtils.isNotBlank(playableInSegment)) {
            g2.drawString(playableInSegment, 10, 20 + ICON_WIDTH_HEIGHT + 20);
        }
    }

    private static void drawEffectSign(int totalEffect, Graphics2D g2) {
        String text = totalEffect > 0 ? "+" : "-";
        g2.drawString(text, 10, CARD_HEIGHT - 20 - ICON_WIDTH_HEIGHT - 10);
    }

    private static void drawText(String text, Graphics2D g2) {
        String wrapped = WordUtils.wrap(text, 17);
        String[] lines = wrapped.split(System.lineSeparator());
        int lineIdx = 0;
        for (String line : lines) {
            g2.drawString(line, 10, CARD_HEIGHT / 2 - 40 + lineIdx * 30);
            lineIdx++;
        }
    }

    private static int drawEffect(int effectValue, Resource resource, int effectCount, int totalEffect, Graphics2D g2) {
        if (effectValue != 0) {
            for (int k = 0; k < Math.abs(effectValue); k ++) {
                BufferedImage resourceIcon = null;
                if (resource != Resource.MOVE) {
                    resourceIcon = resourcesImages.get(resource);
                } else {
                    if (effectValue > 0) {
                        resourceIcon = resourcesImages.get(Resource.MOVE);
                    } else {
                        resourceIcon = resourcesImages.get(Resource.STOP);
                    }
                }
                int start = CARD_WIDTH - totalEffect * ICON_WIDTH_HEIGHT - 10;
                g2.drawImage(resourceIcon, start + effectCount * ICON_WIDTH_HEIGHT, 
                        CARD_HEIGHT - 20 - ICON_WIDTH_HEIGHT,
                        ICON_WIDTH_HEIGHT, ICON_WIDTH_HEIGHT, null);
                effectCount ++;
            }
        }
        return effectCount;
    }
    
    private static int drawResource(int resourceValue, Resource resource, int resourceCount, Graphics2D g2) {
        if (resourceValue < 0) {
            for (int k = 0; k < Math.abs(resourceValue); k ++) {
                BufferedImage resourceIcon = resourcesImages.get(resource);
                g2.drawImage(resourceIcon, 10 + resourceCount * ICON_WIDTH_HEIGHT, 20, ICON_WIDTH_HEIGHT,
                        ICON_WIDTH_HEIGHT, null);
                resourceCount ++;
            }
        }
        return resourceCount;
    }

    private static void initlializeResourceImages() throws IOException {
        for (Resource resource : Resource.values()) {
            resourcesImages.put(resource,
                    ImageIO.read(GenerateCards.class.getResourceAsStream("/icons/" + resource.toString() + ".png")));
        }
        
    }

    public static enum Resource {
        CAPACITY, INFLUENCE, LUCK, MONEY, MORALITY, MOVE, POPULARITY, STOP, TARGET
    }
}