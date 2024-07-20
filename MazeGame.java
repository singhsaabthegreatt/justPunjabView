import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MazeGame extends JFrame {
    private static final int GRID_SIZE = 20; // Larger maze
    private static final int CELL_SIZE = 40; // Cell size for better visuals
    private static final int WINDOW_WIDTH = GRID_SIZE * CELL_SIZE;
    private static final int WINDOW_HEIGHT = GRID_SIZE * CELL_SIZE;
    private static final int CHARACTER_SIZE = CELL_SIZE;

    private BufferedImage doorImage;
    private BufferedImage wallImage;
    private BufferedImage characterImage;
    private BufferedImage goalImage;

    private int[][] maze;
    private Rectangle[][] doors;
    private Rectangle character;
    private Point goal;

    public MazeGame() {
        setTitle("Maze Navigation Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadImages();
        setupMenu();
        setupGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        moveCharacter(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveCharacter(1, 0);
                        break;
                    case KeyEvent.VK_UP:
                        moveCharacter(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveCharacter(0, 1);
                        break;
                }
                checkGoal();
                repaint();
            }
        });
    }

    private void loadImages() {
        try {
            String basePath = "src/images/";
            doorImage = ImageIO.read(new File(basePath + "door.jpg"));
            wallImage = ImageIO.read(new File(basePath + "wall.jpg"));
            characterImage = ImageIO.read(new File(basePath + "character.jpeg"));
            goalImage = ImageIO.read(new File(basePath + "goal.jpeg"));

            // Debugging: Check if images are loaded correctly
            System.out.println("Images loaded successfully");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading images");
            System.exit(1);
        }
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem infoItem = new JMenuItem("Info");

        startItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        infoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MazeGame.this, "Use arrow keys to navigate through the maze and reach the goal!");
            }
        });

        menu.add(startItem);
        menu.add(infoItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void setupGame() {
        maze = generateMaze(GRID_SIZE, GRID_SIZE);
        doors = new Rectangle[GRID_SIZE][GRID_SIZE];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                doors[y][x] = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        character = new Rectangle(1 * CELL_SIZE, 1 * CELL_SIZE, CHARACTER_SIZE, CHARACTER_SIZE);
        goal = new Point(GRID_SIZE - 2, GRID_SIZE - 2);
    }

    private void resetGame() {
        maze = generateMaze(GRID_SIZE, GRID_SIZE);
        doors = new Rectangle[GRID_SIZE][GRID_SIZE];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                doors[y][x] = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        character.setLocation(1 * CELL_SIZE, 1 * CELL_SIZE);
        goal.setLocation(GRID_SIZE - 2, GRID_SIZE - 2);
        repaint();
    }

    private void moveCharacter(int dx, int dy) {
        int x = character.x / CELL_SIZE;
        int y = character.y / CELL_SIZE;
        int newX = x + dx;
        int newY = y + dy;

        if (isValidMove(newX, newY)) {
            character.setLocation(newX * CELL_SIZE, newY * CELL_SIZE);
        }
    }

    private boolean isValidMove(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_SIZE || y >= GRID_SIZE) {
            return false;
        }
        return maze[y][x] == 0;
    }

    private void checkGoal() {
        int x = character.x / CELL_SIZE;
        int y = character.y / CELL_SIZE;
        if (x == goal.x && y == goal.y) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've reached the goal!");
            resetGame();
        }
    }

    private int[][] generateMaze(int width, int height) {
        int[][] maze = new int[height][width];
        Random rand = new Random();

        // Fill with walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = 1; // Wall
            }
        }

        // Create a path through the maze
        int currentX = 1;
        int currentY = 1;
        maze[currentY][currentX] = 0; // Start point

        while (currentX < width - 2 || currentY < height - 2) {
            if (rand.nextBoolean() && currentX < width - 2) {
                currentX++;
            } else if (currentY < height - 2) {
                currentY++;
            }
            maze[currentY][currentX] = 0;
        }

        // Ensure start and end are open
        maze[1][1] = 0;
        maze[height - 2][width - 2] = 0;

        return maze;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw maze
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (maze[y][x] == 1) {
                    g2d.drawImage(wallImage, doors[y][x].x, doors[y][x].y, doors[y][x].width, doors[y][x].height, this);
                } else {
                    g2d.drawImage(doorImage, doors[y][x].x, doors[y][x].y, doors[y][x].width, doors[y][x].height, this);
                }
            }
        }

        // Draw character
        g2d.drawImage(characterImage, character.x, character.y, CHARACTER_SIZE, CHARACTER_SIZE, this);

        // Draw goal
        g2d.drawImage(goalImage, goal.x * CELL_SIZE, goal.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeGame().setVisible(true));
    }
}
