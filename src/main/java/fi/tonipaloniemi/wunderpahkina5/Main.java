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
    private static final String DEFAULT_PUZZLE_IMAGE_URL
            = "https://cloud.githubusercontent.com/assets/14271859/21306742/3663c24c-c5db-11e6-8be5-e358d0e0215a.png";

    public static void main(String[] args) throws Exception {
        RatpackServer.start(server -> server
                .handlers(chain -> chain
                        .get(ctx -> ctx.getResponse()
                                .contentType("image/png")
                                .noCompress()
                                .send(new SolutionDrawer(DEFAULT_PUZZLE_IMAGE_URL).solutionImage()))
                )
        );
    }

    private static class SolutionDrawer {

        private static final Color EMPTY_COLOR = new Color(0, 0, 0, 0);
        private static final Color DEBUG_COLOR = new Color(255, 0, 0, 255);
        private static final Color DRAW_COLOR = new Color(255, 255, 255, 255);
        private enum Direction {
            UP, DOWN, LEFT, RIGHT;
        }

        private final String url;
        private BufferedImage puzzleImage;
        private BufferedImage solutionImage;
        private int height;
        private int width;
        private int x;
        private int y;

        private SolutionDrawer(String url) {
            this.url = url;
        }

        private byte[] solutionImage() throws IOException {
            puzzleImage = getImage(url);
            int height = puzzleImage.getHeight();
            int width = puzzleImage.getWidth();
            solutionImage = new BufferedImage(width, height, puzzleImage.getType());

            y = 0;
            while (y < height) {
                x = 0;
                while (x < width) {
                    debugPixel(x, y);
                    x++;
                }
                y++;
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(solutionImage, "png", output);
            return output.toByteArray();
        }

        private void debugPixel(int x, int y) {
            Color puzzleColor = new Color(puzzleImage.getRGB(x, y), true);
            if (isActionColor(puzzleColor)) {
                solutionImage.setRGB(x, y, DEBUG_COLOR.getRGB());
            } else {
                solutionImage.setRGB(x, y, EMPTY_COLOR.getRGB());
            }
        }

        private void drawState(){

        }

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
