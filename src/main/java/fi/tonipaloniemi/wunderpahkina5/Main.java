package fi.tonipaloniemi.wunderpahkina5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.server.RatpackServer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String DEFAULT_URL = "https://cloud.githubusercontent.com/assets/14271859/21306742/3663c24c-c5db-11e6-8be5-e358d0e0215a.png";

    public static void main(String[] args) throws Exception {
        RatpackServer.start(server -> server
                .handlers(chain -> chain
                        .get(ctx -> ctx.getResponse()
                                .contentType("image/png")
                                .noCompress()
                                .send(solutionImage(DEFAULT_URL)))
                )
        );
    }

    private static byte[] solutionImage(String url) throws IOException {
        BufferedImage puzzleImage = getImage(url);
        int height = puzzleImage.getHeight();
        int width = puzzleImage.getWidth();
        Color emptyColor = new Color(0, 0, 0, 0);
        Color drawColor = new Color(255, 0, 0, 255);
        BufferedImage solutionImage = new BufferedImage(width, height, puzzleImage.getType());
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                Color puzzleColor = new Color(puzzleImage.getRGB(x, y), true);
                if (isActionColor(puzzleColor)) {
                    solutionImage.setRGB(x, y, drawColor.getRGB());
                } else {
                    solutionImage.setRGB(x, y, emptyColor.getRGB());
                }
                x++;
            }
            y++;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(solutionImage, "png", output);
        return output.toByteArray();
    }

    private static boolean isActionColor(Color color) {
        int[][] actionColors = {
                {7, 84, 19},
                {139, 57, 137},
                {51, 69, 169},
                {182, 149, 72},
                {123, 131, 154}
        };
        return Arrays.stream(actionColors)
                .anyMatch(rgb -> rgb[0] == color.getRed() && rgb[1] == color.getGreen() && rgb[2] == color.getBlue());
    }

    private static BufferedImage getImage(String url) throws IOException {
        try (InputStream imageStream = new URL(url).openStream()) {
            return ImageIO.read(imageStream);
        }
    }
}
