import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AsciiSwingAnimation extends JFrame {

    // canvas size
    static final int WIDTH = 120;
    static final int HEIGHT = 35;

    // shared animation state
    static final Object LOCK = new Object();

    volatile int eatFrame = 0;
    volatile int drinkFrame = 0;
    volatile int runFrame = 0;
    volatile int runnerX = 2;

    volatile boolean eatPaused = false;
    volatile boolean drinkPaused = false;
    volatile boolean runPaused = false;

    JTextArea screenArea;
    JTextField inputField;

    public AsciiSwingAnimation() {

        setTitle("ASCII Animation Swing Example");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        screenArea = new JTextArea();
        screenArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        screenArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(screenArea);

        inputField = new JTextField();
        inputField.addActionListener(this::handleInput);

        add(scroll, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        startThreads();
        startRenderLoop();
    }

    // handle user typing 1 2 3
    void handleInput(ActionEvent e) {

        String input = inputField.getText().trim();

        if (input.equals("1"))
            eatPaused = !eatPaused;

        if (input.equals("2"))
            drinkPaused = !drinkPaused;

        if (input.equals("3"))
            runPaused = !runPaused;

        inputField.setText("");
    }

    void startThreads() {

        // eating animation thread
        new Thread(() -> {

            while (true) {

                if (!eatPaused) {
                    synchronized (LOCK) {
                        eatFrame = (eatFrame + 1) % 2;
                    }
                }

                sleep(500);
            }

        }).start();

        // drinking animation thread
        new Thread(() -> {

            while (true) {

                if (!drinkPaused) {
                    synchronized (LOCK) {
                        drinkFrame = (drinkFrame + 1) % 2;
                    }
                }

                sleep(500);
            }

        }).start();

        // running animation thread
        new Thread(() -> {

            while (true) {

                if (!runPaused) {

                    synchronized (LOCK) {

                        runFrame = (runFrame + 1) % 4;
                        runnerX += 2;

                        if (runnerX > WIDTH - 20)
                            runnerX = 2;
                    }
                }

                sleep(150);
            }

        }).start();
    }

    // render loop
    void startRenderLoop() {

        new Thread(() -> {

            while (true) {

                char[][] screen = createScreen();

                synchronized (LOCK) {

                    drawBorder(screen);

                    drawText(screen, 35, 2,
                            "SWING ASCII PEOPLE ANIMATION");

                    drawText(screen, 25, 3,
                            "Type 1 dinner  |  2 coffee  |  3 running");

                    drawEatingScene(screen, 4, 8, eatFrame);
                    drawDrinkingScene(screen, 45, 8, drinkFrame);
                    drawRunningScene(screen, runnerX, 22, runFrame);
                }

                screenArea.setText(convert(screen));

                sleep(80);
            }

        }).start();
    }

    char[][] createScreen() {

        char[][] screen = new char[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++)
            for (int c = 0; c < WIDTH; c++)
                screen[r][c] = ' ';

        return screen;
    }

    void drawBorder(char[][] s) {

        for (int c = 0; c < WIDTH; c++) {
            s[0][c] = '#';
            s[HEIGHT - 1][c] = '#';
        }

        for (int r = 0; r < HEIGHT; r++) {
            s[r][0] = '#';
            s[r][WIDTH - 1] = '#';
        }
    }

    void drawEatingScene(char[][] s, int x, int y, int frame) {

        String[] f1 = {
                "   (o o)   ",
                "    \\_/    ",
                "    /|>    ",
                "    / \\    ",
                "   eating  "
        };

        String[] f2 = {
                "   (o o)   ",
                "    \\_/    ",
                "   <|\\     ",
                "    / \\    ",
                "   eating  "
        };

        drawAscii(s, x, y, frame == 0 ? f1 : f2);

        drawText(s, x, y + 6, "====== DINNER TABLE ======");
    }

    void drawDrinkingScene(char[][] s, int x, int y, int frame) {

        String[] f1 = {
                "   (o o)   ",
                "    \\_/    ",
                "    /|]    ",
                "    / \\    ",
                "  coffee   "
        };

        String[] f2 = {
                "   (o o)   ",
                "    \\_/    ",
                "   [|\\     ",
                "    / \\    ",
                "  coffee   "
        };

        drawAscii(s, x, y, frame == 0 ? f1 : f2);

        drawText(s, x, y + 6, "====== COFFEE BAR ======");
    }

    void drawRunningScene(char[][] s, int x, int y, int frame) {

        String[][] frames = {

                {
                        "   O   ",
                        "  /|_  ",
                        "  / \\  ",
                        " run  "
                },

                {
                        "   O   ",
                        "  _|\\  ",
                        "  / \\  ",
                        " run  "
                },

                {
                        "   O   ",
                        "  /|_  ",
                        "  /\\   ",
                        " run  "
                },

                {
                        "   O   ",
                        "  _|\\  ",
                        "   /\\  ",
                        " run  "
                }
        };

        drawAscii(s, x, y, frames[frame]);

        for (int c = 2; c < WIDTH - 2; c++)
            s[y + 5][c] = '=';
    }

    void drawAscii(char[][] s, int x, int y, String[] art) {

        for (int r = 0; r < art.length; r++) {

            for (int c = 0; c < art[r].length(); c++) {

                int px = x + c;
                int py = y + r;

                if (px >= 0 && px < WIDTH && py >= 0 && py < HEIGHT) {

                    char ch = art[r].charAt(c);

                    if (ch != ' ')
                        s[py][px] = ch;
                }
            }
        }
    }

    void drawText(char[][] s, int x, int y, String t) {

        for (int i = 0; i < t.length(); i++) {

            int px = x + i;

            if (px >= 0 && px < WIDTH)
                s[y][px] = t.charAt(i);
        }
    }

    String convert(char[][] screen) {

        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < HEIGHT; r++) {
            sb.append(screen[r]);
            sb.append('\n');
        }

        return sb.toString();
    }

    static void sleep(int ms) {

        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new AsciiSwingAnimation().setVisible(true);
        });
    }
}
