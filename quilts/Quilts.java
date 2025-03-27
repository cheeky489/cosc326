// Author: denan895 8264774

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Quilts extends JComponent {

    private static double totalRelativeSize = 0.0;
    private static ArrayList<Square> squares = new ArrayList<>();
    private static int windowSize = 800; // base window size for scaling

    public Quilts() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawQuilt(g, getWidth() / 2, getHeight() / 2); // draw the quilt starting from the center
    }

    // draws the quilt recursively using a queue
    private void drawQuilt(Graphics g, int centerX, int centerY) {
        // calculate scaling factor based on current size of the component
        double scaleFactor = Math.min(getWidth(), getHeight()) / (double) windowSize;

        Queue<SquareDrawing> queue = new LinkedList<>();
        queue.add(new SquareDrawing(centerX, centerY, 0)); // add the initial square drawing

        // go through the queue until all squares are drawn
        while (!queue.isEmpty()) {
            SquareDrawing current = queue.poll();
            drawSquare(g, current.x, current.y, current.index, scaleFactor); // draw current square

            // queue tasks for drawing smaller squares around the current one
            if (current.index + 1 < squares.size()) {
                int size = (int) Math.round(
                        squares.get(current.index).getRelativeSize() / totalRelativeSize * windowSize * scaleFactor);
                int newSize = size / 2;

                // add tasks for the four surrounding squares
                for (int offsetX = -1; offsetX <= 1; offsetX += 2) {
                    for (int offsetY = -1; offsetY <= 1; offsetY += 2) {
                        queue.add(new SquareDrawing(current.x + offsetX * newSize, current.y + offsetY * newSize,
                                current.index + 1));
                    }
                }
            }
        }
    }

    // draw a single square on the graphics context
    private void drawSquare(Graphics g, int x, int y, int index, double scaleFactor) {
        Square square = squares.get(index);
        // calculates size of the square
        int size = (int) Math.round(square.getRelativeSize() / totalRelativeSize * windowSize * scaleFactor);
        g.setColor(square.getColor());
        g.fillRect(x - size / 2, y - size / 2, size, size); // draw the square centered at (x, y)
    }

    // sets up and displays the GUI
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Quilt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Quilts());
        frame.setSize(windowSize + 60, windowSize + 60); // set the frame size
        frame.setLocationRelativeTo(null); // centre the frame on the screen
        frame.setVisible(true); // make the frame visible
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        while (scan.hasNextLine()) {
            String[] line = scan.nextLine().split("\\s+");
            double relSize = Double.parseDouble(line[0]);
            int r = Integer.parseInt(line[1]);
            int g = Integer.parseInt(line[2]);
            int b = Integer.parseInt(line[3]);
            totalRelativeSize += relSize;
            squares.add(new Square(new Color(r, g, b), relSize));
        }
        scan.close();

        // create and display the GUI
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}

class Square {
    private Color color;
    private double relativeSize;

    // constructor to initialize a square with color and relative size
    public Square(Color color, double relativeSize) {
        this.color = color;
        this.relativeSize = relativeSize;
    }

    public Color getColor() {
        return color;
    }

    public double getRelativeSize() {
        return relativeSize;
    }
}

class SquareDrawing {
    int x, y, index;

    // constructor to initialize a square drawing task
    public SquareDrawing(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }
}
