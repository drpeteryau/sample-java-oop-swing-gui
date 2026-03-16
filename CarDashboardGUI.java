
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CarDashboardGUI extends JFrame {

    // car state variables
    private int speed = 0;
    private boolean lightsOn = false;

    // UI components
    private JLabel speedLabel;
    private CarPanel carPanel;

    public CarDashboardGUI() {

        setTitle("Car Driving Simulator");
        setSize(800,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // speed display
        speedLabel = new JLabel("Speed: 0 km/h", SwingConstants.CENTER);
        speedLabel.setFont(new Font("Arial",Font.BOLD,26));

        add(speedLabel, BorderLayout.NORTH);

        // drawing panel for the car
        carPanel = new CarPanel();
        add(carPanel, BorderLayout.CENTER);

        // control buttons
        JPanel controls = new JPanel();

        JButton accelerate = new JButton("Speed Up");
        JButton brake = new JButton("Slow Down");
        JButton lights = new JButton("Toggle Lights");

        controls.add(accelerate);
        controls.add(brake);
        controls.add(lights);

        add(controls, BorderLayout.SOUTH);

        // button actions
        accelerate.addActionListener(this::speedUp);
        brake.addActionListener(this::slowDown);
        lights.addActionListener(this::toggleLights);
    }

    // increase speed
    private void speedUp(ActionEvent e) {

        speed = speed + 10;

        if(speed > 200)
            speed = 200;

        updateDisplay();
    }

    // decrease speed
    private void slowDown(ActionEvent e) {

        speed = speed - 10;

        if(speed < 0)
            speed = 0;

        updateDisplay();
    }

    // toggle headlights
    private void toggleLights(ActionEvent e) {

        lightsOn = !lightsOn;

        updateDisplay();
    }

    // update GUI display
    private void updateDisplay() {

        speedLabel.setText("Speed: " + speed + " km/h");

        carPanel.repaint();
    }

    // custom drawing panel
    class CarPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

            Graphics2D g2 = (Graphics2D) g;

            // road
            g2.setColor(Color.darkGray);
            g2.fillRect(0, height/2 + 60, width, 80);

            // car body
            g2.setColor(Color.red);
            g2.fillRoundRect(width/2 - 120, height/2, 240, 60, 20,20);

            // car roof
            g2.fillRoundRect(width/2 - 70, height/2 - 40, 140, 50,20,20);

            // wheels
            g2.setColor(Color.black);
            g2.fillOval(width/2 - 90, height/2 + 50, 50,50);
            g2.fillOval(width/2 + 40, height/2 + 50, 50,50);

            // headlight beams if lights on
            if(lightsOn) {

                g2.setColor(new Color(255,255,120,120));

                int x = width/2 + 120;
                int y = height/2 + 10;

                int[] xPoints = {x, x+120, x+120};
                int[] yPoints = {y, y-30, y+50};

                g2.fillPolygon(xPoints,yPoints,3);
            }

            // speed indicator bar
            g2.setColor(Color.green);

            int barWidth = speed * 2;

            g2.fillRect(40, 40, barWidth, 20);

            g2.setColor(Color.black);
            g2.drawRect(40, 40, 400, 20);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new CarDashboardGUI().setVisible(true);

        });
    }
}
