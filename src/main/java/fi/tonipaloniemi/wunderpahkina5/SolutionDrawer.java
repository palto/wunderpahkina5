package fi.tonipaloniemi.wunderpahkina5;

import ratpack.server.RatpackServer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class SolutionDrawer {

    private static final String DEFAULT_PUZZLE_IMAGE_URL
            = "https://cloud.githubusercontent.com/assets/14271859/21306742/3663c24c-c5db-11e6-8be5-e358d0e0215a.png";

    public static void main(String[] args) throws Exception {
        RatpackServer.start(server -> server
                .handlers(chain -> chain
                        .get(ctx -> ctx.getResponse()
                                .contentType("image/png")
                                .noCompress()
                                .send(new SolutionDrawer(DEFAULT_PUZZLE_IMAGE_URL).solutionImage()))
                        .get("debug", ctx -> ctx.getResponse()
                                .contentType("image/png")
                                .noCompress()
                                .send(new SolutionDrawer(DEFAULT_PUZZLE_IMAGE_URL, true).solutionImage()))
                )
        );
    }

    private enum Pixels {
        DRAW_COLOR(new Color(0, 0, 0)),
        STOP_COLOR(new Color(51, 69, 169)),
        START_UP(new Color(7, 84, 19)),
        START_LEFT(new Color(139, 57, 137)),
        COUNTER_CLOCKWISE_COLOR(new Color(123, 131, 154)),
        CLOCKWISE_COLOR(new Color(182, 149, 72));

        Color color;

        Pixels(Color color) {
            this.color = color;
        }
    }


    private enum Direction {
        UP(Pixels.START_UP.color),
        LEFT(Pixels.START_LEFT.color),
        DOWN(null),
        RIGHT(null);
        Color startColor;

        Direction(Color startColor) {
            this.startColor = startColor;
        }

        boolean isDirectionColor(Color testColor) {
            return Optional.ofNullable(startColor).map(c -> c.equals(testColor)).orElse(false);
        }

        Direction counterClockWise() {
            switch (this) {
                case LEFT:
                    return Direction.DOWN;
                case RIGHT:
                    return Direction.UP;
                case UP:
                    return Direction.LEFT;
                default:
                    return Direction.RIGHT;
            }
        }

        Direction clockWise() {
            switch (this) {
                case LEFT:
                    return Direction.UP;
                case RIGHT:
                    return Direction.DOWN;
                case UP:
                    return Direction.RIGHT;
                default:
                    return Direction.LEFT;
            }
        }
    }

    private final String url;
    private final boolean debug;
    private BufferedImage puzzleImage;
    private BufferedImage solutionImage;

    private SolutionDrawer(String url) {
        this.url = url;
        this.debug = false;
    }

    private SolutionDrawer(String url, boolean debug) {
        this.url = url;
        this.debug = debug;
    }

    private byte[] solutionImage() throws IOException {
        puzzleImage = getImage(url);
        int height = puzzleImage.getHeight();
        int width = puzzleImage.getWidth();
        solutionImage = new BufferedImage(width, height, puzzleImage.getType());

        IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> {
            if (debug) {
                debugPixel(x, y);
                return;
            }
            Color puzzleColor = new Color(puzzleImage.getRGB(x, y), true);
            Arrays.stream(Direction.values())
                    .filter(d -> d.isDirectionColor(puzzleColor))
                    .forEach(d -> draw(x, y, d));

        }));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(solutionImage, "png", output);
        return output.toByteArray();
    }

    private void debugPixel(int x, int y) {
        Color puzzleColor = new Color(puzzleImage.getRGB(x, y), true);
        Arrays.stream(Pixels.values())
                .map(pixel -> pixel.color)
                .filter(color -> color.equals(puzzleColor))
                .findFirst()
                .ifPresent(color -> solutionImage.setRGB(x, y, puzzleColor.getRGB()));
    }

    private void draw(int startX, int startY, Direction direction) {
        int drawX = startX;
        int drawY = startY;
        while (true) {
            solutionImage.setRGB(drawX, drawY, Pixels.DRAW_COLOR.color.getRGB());
            Color puzzleColor = new Color(puzzleImage.getRGB(drawX, drawY), true);
            if (puzzleColor.equals(Pixels.STOP_COLOR.color)) {
                break;
            }
            if (puzzleColor.equals(Pixels.COUNTER_CLOCKWISE_COLOR.color)) {
                direction = direction.counterClockWise();
            }
            if (puzzleColor.equals(Pixels.CLOCKWISE_COLOR.color)) {
                direction = direction.clockWise();
            }
            switch (direction) {
                case UP:
                    drawY--;
                    break;
                case DOWN:
                    drawY++;
                    break;
                case RIGHT:
                    drawX++;
                    break;
                case LEFT:
                    drawX--;
                    break;
            }
        }
    }

    private static BufferedImage getImage(String url) throws IOException {
        try (InputStream imageStream = new URL(url).openStream()) {
            return ImageIO.read(imageStream);
        }
    }
}
