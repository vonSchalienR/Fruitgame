package JAVA_LOPPUTYÖ;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;


import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;





public class Kopio_FruitJumpGame extends JFrame implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FRUIT_WIDTH = 100;
    private static final int FRUIT_HEIGHT = 100;
    private static final int GROUND_Y = HEIGHT - FRUIT_HEIGHT - 30;

    private Timer timer;
    private int fruitX, fruitY, fruitVelocityY;
    private boolean isJumping;
    private List<BufferedImage> fruitImages;
    private int currentFruitIndex;

    private BufferedImage buffer;
    private BufferedImage currentFruitImage;
    private BufferedImage discoBallImage;
    private int spaceBarClicks;
    private int enterKeyClicks;

    private static Clip clip; // Tarvitaan tämä jotta ääniraita saadaan

    public Kopio_FruitJumpGame() {
        soitaTunnari("pedro.wav");

        setTitle("Fruit Jump Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);

        fruitX = WIDTH / 2 - FRUIT_WIDTH / 2;
        fruitY = GROUND_Y;
        fruitVelocityY = 0;
        isJumping = false;
        currentFruitIndex = 0;
        spaceBarClicks = 0;
        enterKeyClicks = 0;

        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        fruitImages = new ArrayList<>();
        loadFruitImages();

        currentFruitImage = fruitImages.get(currentFruitIndex);

        try {
            URL discoBallURL = new URL("https://pngimg.com/uploads/disco_ball/disco_ball_PNG22.png");
            discoBallImage = resizeImage(ImageIO.read(discoBallURL), 100, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(10, this);
        timer.start();
        
        
   
    }

    private void loadFruitImages() {
        try {
            URL appleURL = new URL("https://www.freeiconspng.com/thumbs/apple-png/red-fresh-shiny-apple-png-0.png");
            URL apricotURL = new URL("https://pngimg.com/d/apricot_PNG12652.png");
            URL pomegranateURL = new URL("https://static.vecteezy.com/system/resources/previews/024/991/948/original/pomegranate-punica-granatum-pomegranate-transparent-background-png.png");
            URL pyjamabananaURL = new URL("https://www.seekpng.com/png/full/416-4167032_bananas-bananas-in-pyjamas-day-and-night-banana.png");

            fruitImages.add(resizeImage(ImageIO.read(appleURL), FRUIT_WIDTH, FRUIT_HEIGHT));
            fruitImages.add(resizeImage(ImageIO.read(apricotURL), FRUIT_WIDTH, FRUIT_HEIGHT));
            fruitImages.add(resizeImage(ImageIO.read(pomegranateURL), FRUIT_WIDTH, FRUIT_HEIGHT));
            fruitImages.add(resizeImage(ImageIO.read(pyjamabananaURL), FRUIT_WIDTH, FRUIT_HEIGHT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        if (spaceBarClicks < 10) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(getRandomColor());
        }

        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw text at the top middle of the screen
        if (spaceBarClicks < 10) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            String message = "Click the spacebar 10 times and have a party!";
            int messageWidth = fm.stringWidth(message);
            int x = (WIDTH - messageWidth) / 2;
            int y = 40; // Adjust this value to change the vertical position
            g2d.drawString(message, x, y);
        }

        g2d.drawImage(currentFruitImage, fruitX, fruitY, FRUIT_WIDTH, FRUIT_HEIGHT, null);

        if (spaceBarClicks >= 10) {
            g2d.drawImage(discoBallImage, WIDTH / 2 - 50, HEIGHT / 2 - 50, 100, 100, null);
        }

        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isJumping) {
            fruitY -= fruitVelocityY;
            fruitVelocityY -= 1;
            if (fruitY >= GROUND_Y) {
                fruitY = GROUND_Y;
                isJumping = false;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !isJumping) {
            isJumping = true;
            fruitVelocityY = 20;
            spaceBarClicks++;
            if (spaceBarClicks == 10) {
                showDiscoBall();
                startFlicker();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentFruitIndex = (currentFruitIndex + 1) % fruitImages.size();
            currentFruitImage = fruitImages.get(currentFruitIndex);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentFruitIndex = (currentFruitIndex - 1 + fruitImages.size()) % fruitImages.size();
            currentFruitImage = fruitImages.get(currentFruitIndex);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            enterKeyClicks++;
            if (enterKeyClicks >= 2) {
                restartGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FruitJumpGame game = new FruitJumpGame();
            game.setVisible(true);
        });
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private void showDiscoBall() {
        // Implement method to show disco ball
        // For example, you can draw an image of a disco ball on the screen
    }

    private void startFlicker() {
        Timer flickerTimer = new Timer(3000, new ActionListener() {
            boolean isBackgroundWhite = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof Timer) {
                    isBackgroundWhite = !isBackgroundWhite;
                    if (isBackgroundWhite) {
                        getContentPane().setBackground(Color.WHITE);
                    } else {
                        getContentPane().setBackground(getRandomColor());
                    }
                }
            }
        });
        flickerTimer.start();
    }

    private void restartGame() {
        // Reset game variables
        fruitX = WIDTH / 2 - FRUIT_WIDTH / 2;
        fruitY = GROUND_Y;
        fruitVelocityY = 0;
        isJumping = false;
        currentFruitIndex = 0;
        spaceBarClicks = 0;
        enterKeyClicks = 0;

        currentFruitImage = fruitImages.get(currentFruitIndex);

        // Stop any ongoing timers
        timer.stop();

        // Restart the timer
        timer.start();
    }

    private Color getRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }


    public static void soitaTunnari(String filePath) {      
        try {
            File file = new File(filePath);     // Luodaan uusi tiedosto-olio
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);   //Haetaan tiedoston syöte virta
            clip = AudioSystem.getClip();   // Luodaan uusi Clip-olio, johon tallennetaan äänitiedoston toistoon liittyvät tiedot
            System.out.println("Tiedostopolku: " + file.getAbsolutePath());
            clip.open(audioIn);     // tunnari alkaa soimaan
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // Loputon audio loop
            clip.start();  // Aloitetaan soittamaan alusta 
        } catch (UnsupportedAudioFileException e) { // Virheilmoitus jos tunnarin toisto ei onnistu.
            System.out.println("Tunnarin soittaminen epäonnistui. ");
            e.printStackTrace();
        } catch (IOException e) { // Virheilmoituksia mahdollisesti monta erilaista
            e.printStackTrace();
        } catch (LineUnavailableException e) {  // Virheilmoituksia mahdollisesti monta erilaista
            e.printStackTrace();
        }
    }
    
    

}     
