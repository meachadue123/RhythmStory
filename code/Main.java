import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Font;
import java.util.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
public class Main extends JFrame implements KeyListener
{
    boolean isLeftPressed, isRightPressed;
    
    boolean drawn = false; //whether bg drawn or not.
    boolean hit = false; //whether the beat was hit or not
    
    int time = 0; //how much time has passed
   
    BufferStrategy bs;
    
    DrawPanel panel = new DrawPanel();
    
    int stringTime = 400; //keeps track of how long to display the accuracy string
    
    String accuracyString = ""; //bad/good/perfect
    
    int points; //the player's points
    
    int pressedPosition;
    
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    ArrayList<Beat> beats = new ArrayList<Beat>(); //arraylist to store all of the beats created
    int[] intervals = new int[1000]; //array to store all the pregenerated intervals
    
    int beatTime = 0;
    
    int interval = 0;
    
    int intervalCounter = 0; //counter that goes through the intervals array
    
    boolean generating = true; //if a beat has been added yet or not
    
    BufferedImage image; //stores the image
    
    int health; //the player's health
    
    int beatCounter = 0;
    
    Graphics2D g;
    
    
    public Main()
    {
        setIgnoreRepaint(true);
        setTitle("RhythmStory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024,720);
        setResizable(false);
        setVisible(true);
        createBufferStrategy(2);
        bs = getBufferStrategy();
        getContentPane().add(panel);
        panel.setIgnoreRepaint(true);
        addKeyListener(this);
        generateBeats();
        storeImages();
        g = (Graphics2D)bs.getDrawGraphics();
        beats.add(new Beat(1));
    }
    
    public void startNow()
    {
        //playMusic();
        loadBackground();
        panel.drawStuff();
    }
    public void keyTyped(KeyEvent e)
    {
        
    }
    public synchronized void keyPressed(KeyEvent e)
    { 
       stringTime = 0;
       if (e.getKeyCode() == (KeyEvent.VK_LEFT))
        {
            if (beats.get(beatCounter).getColor() != 1 || isRightPressed == true)
            {
                accuracyString = "Miss";
            }
            else
            {
                time = beats.get(beatCounter).getX();
                if (calculatePosition() <= 105 && calculatePosition() >= 25)
                {
                    beats.get(beatCounter).hit();
                    ++beatCounter;
                    
                }
                hit = true;
            }
        }
        if (e.getKeyCode() == (KeyEvent.VK_RIGHT))
        {  
            if (beats.get(beatCounter).getColor() != 2 || isLeftPressed == true)
            {
                accuracyString = "Miss";
            }
            else
            {
                time = beats.get(beatCounter).getX();
                if (calculatePosition() <= 105 && calculatePosition() >= 25)
                {
                    beats.get(beatCounter).hit();
                    ++beatCounter;
                    
                }
                hit = true;
            }
        
            }
        switch(e.getKeyCode()) 
        {
            case KeyEvent.VK_LEFT: isLeftPressed = true; break;
            case KeyEvent.VK_RIGHT: isRightPressed = true; break;
        }
        if(isLeftPressed && isRightPressed)
        {
             if (beats.get(beatCounter).getColor() != 3)
            {
                accuracyString = "Miss";
            }
            else
            {
                time = beats.get(0).getX();
                if (calculatePosition() <= 105 && calculatePosition() >= 25)
                {
                    beats.get(beatCounter).hit();
                    ++beatCounter;
                    
                }
                hit = true;
            }
        }   
       
    }
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode()) 
        {
            case KeyEvent.VK_LEFT: isLeftPressed = false; break;
            case KeyEvent.VK_RIGHT: isRightPressed = false; break;
        }
    }
    public class DrawPanel extends JPanel
    {
        public void drawStuff()
        {
            while(true)
            {
                try
                {
                     //refreshes the screen
                    g = (Graphics2D)bs.getDrawGraphics();
                    Font font = new Font("Serif", Font.PLAIN, 96);
                    g.setFont(font);
                    for (int i = beatCounter; i < beats.size(); i++) //moves every beat across the screen
                    {
                        beats.get(i).move();
                    }
          
                    stringTime = stringTime + 20; //increments how long the string has been displayed for
                   
                    nextBeat(); //creates the next beat
                    
                    g.setColor(Color.BLACK);
                    g.fillRect(0,575,1024,178);
                    g.drawImage(images.get(3), 0, 0 ,null); //draws the target
                    g.drawImage(images.get(4), 40, 600,null); //draws the target
                        
                    for (int i = beatCounter; i < beats.size(); i++) //draws each beat in the beat list
                    {
                        if (beats.get(i).getHit() == false)
                        {
                            g.drawImage(images.get(beats.get(i).getColor() - 1), beats.get(i).getX(), beats.get(i).getY(),null);
                        }
                    }
                    if (stringTime < 400)
                    {
                        g.drawString(accuracyString, 500, 120);
                        g.drawString(time + "", 200, 120);
                    }             
                    if (beats.get(beatCounter).getX() <= 0)
                    {
                        beats.get(beatCounter).hit();
                        accuracyString = "Miss";
                        stringTime = 0;
                        ++beatCounter;
                    }
                    bs.show();
                    Toolkit.getDefaultToolkit().sync();
                    g.dispose();
                    Thread.sleep(10);
                }
                catch (Exception e)
                {
                    System.exit(0);
                }
            }
        }
     }
    public void generateBeats()  //fills in the interval array with random inervals
    {
        for (int i = 0; i < intervals.length; i++)
        {
            int roll = (int)(Math.random()*10 + 1);
            if (roll >= 8)
            {
                intervals[i] = 20;
            }
            if (roll < 4)
            {
                intervals[i] = 100;
            }
            else
            {
                intervals[i] = 200;
            }
        }
    }
    public int generateColor() //when making a beat, this determines if it's red blue or both
    {
        int roll = (int)(Math.random()*10 + 1);
        if (roll < 5)
        {
            return 1;
        }
        else if (roll > 8)
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }
    public void nextBeat() //adds a beat to the beatlist
    {
        beatTime = beatTime + 5;
        if (beatTime == intervals[intervalCounter])
        {
            beats.add(new Beat(generateColor()));
            beatTime = 0;
            ++intervalCounter;
        }
    }
    public int calculatePosition() //all the calculations when a button is pressed
    {
        int position = beats.get(beatCounter).getX();
        if (position <= 45 && position >= 35)
        {
            accuracyString = "Perfect";
            points = points + 300;
        }
        else if ((position > 45 && position <= 55) || (position >= 30 && position < 35))
        {
            accuracyString = "Good";
            points = points + 50;
        }
        else if ((position < 30 && position >= 25) || (position > 55 && position <= 105))
        {
            points = points + 100;
            accuracyString = "Bad";
        }
        else
        {
            accuracyString = "Miss";
        }
        return position;
    }
    public void playMusic()
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets\\music\\TimeTemple.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } 
        catch(Exception ex) 
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
    public void storeImages()
    {
        try
        {
            images.add(ImageIO.read(new File("assets\\beatRed.png")));
            images.add(ImageIO.read(new File("assets\\beatBlue.png")));
            images.add(ImageIO.read(new File("assets\\beatBoth.png")));
            images.add(ImageIO.read(new File("assets\\backgroundTop.jpg")));
            images.add(ImageIO.read(new File("assets\\target.png")));
        }
        catch (Exception e)
        {
        }
    }
    public static void main(String[] args)
    {
        Main main = new Main();
        main.startNow();
    }
    
    
    public void loadBackground() //stores an image 
    {
        try
        {
            g.drawImage(ImageIO.read(new File("assets\\background.jpg")), 0, 0,null);
            g.drawImage(ImageIO.read(new File("assets\\floor.png")), 0, 477,null);
            
        }
        catch (Exception e)
        {
        }
    }
    
}
