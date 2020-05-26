import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
class Bmtron extends JPanel implements ActionListener, KeyListener, MouseListener
{
    Timer timer = new Timer(15, this);
    
    //5 frames per block
    int framesLeft = 5;
    //delay between end round and start round
    int delay = 30;
    
    //info about players
    int numberOfPlayers = 2;
    String[] direction = new String[0];
    String[] direction2 = new String[0];//direction2 to stop from killing from turning too quickly
    boolean[] alive = new boolean[0];
    int[][] x = {{200}, {860}, {200}, {860}};
    int[][] y = {{160}, {160}, {440}, {440}};
    int[] score = {0, 0, 0, 0};
    
    //beginning
    boolean instructions = true;
    //set up stuff
    boolean ready = false;
    Image explosion;
    AudioInputStream audio;
    //design stuff
    Font title = new Font("Garamond", Font.BOLD, 30);
    Font font = new Font("Garamond", Font.PLAIN, 12);
    public void setup()//sets up images and audio clips
    {
        if (!ready)
        {
            ready = true;
            try
            {
                explosion = ImageIO.read(getClass().getResource("explosion.png"));
                
                audio = AudioSystem.getAudioInputStream(this.getClass().getResource("explosion.wav"));
            }
            catch (Exception e){}
        }
    }
    public void start()
    {
        while (alive.length < numberOfPlayers)
        {
            alive = Arrays.copyOf(alive, alive.length + 1);
            alive[alive.length - 1] = true;
            
            direction = Arrays.copyOf(direction, direction.length + 1);
            direction[direction.length - 1] = "none";
            
            direction2 = Arrays.copyOf(direction2, direction2.length + 1);
            direction2[direction2.length - 1] = "none";
        }
        if (numberOfPlayers < 3)//if need 2 rows
        {
            for (int i = 0; i < y.length; i++)
            {
                y[i][0] = 300;
            }
        }
        else
        {
            y[0][0] = 160;
            y[1][0] = 160;
            y[2][0] = 440;
            y[3][0] = 440;
        }
    }
    public void newBlock(int player, int xs, int ys)
    {
        int[] tempX = Arrays.copyOf(x[player], x[player].length + 1);
        int[] tempY = Arrays.copyOf(y[player], y[player].length + 1);
        
        tempX[tempX.length - 1] = xs;
        tempY[tempY.length - 1] = ys;
        
        x[player] = tempX;
        y[player] = tempY;
    }
    public boolean collision(int player, int[] xa, int[] ya)
    {
        int xPoint = xa[xa.length - 1];
        int yPoint = ya[ya.length - 1];
        for (int i = 0; i < xa.length - 1; i++)//hit self
        {
            if (xPoint == xa[i] && yPoint == ya[i])
            {
                return true;
            }
        }
        for (int k = 0; k < alive.length; k++)
        {
            if (player != k)
            {
                for (int i = 0; i < x[k].length; i++)
                {
                    if (xPoint == x[k][i] && yPoint == y[k][i])
                    {
                        return true;
                    }
                }
            }
        }
        if (xPoint == 0 || xPoint == 1060 || yPoint == 0 || yPoint == 600)//hit wall
        {
            return true;
        }
        
        return false;
    }
    public void switchDirection(int playerNumber, String iDirection)
    {
        if (playerNumber <= numberOfPlayers)
        {
            if (iDirection.equals("up") && !direction2[playerNumber - 1].equals("down"))
            {
                direction[playerNumber - 1] = "up";
            }
            else if (iDirection.equals("left") && !direction2[playerNumber - 1].equals("right"))
            {
                direction[playerNumber - 1] = "left";
            }
            else if (iDirection.equals("down") && !direction2[playerNumber - 1].equals("up"))
            {
                direction[playerNumber - 1] = "down";
            }
            else if (iDirection.equals("right") && !direction2[playerNumber - 1].equals("left"))
            {
                direction[playerNumber - 1] = "right";
            }
        }
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        timer.start();
        setup();
        start();
        setBackground(Color.black);
        
        //draw walls
        g.setColor(Color.white);
        g.fillRect(10, 10, 10, 600);//left
        g.fillRect(10, 10, 1060, 10);//up
        g.fillRect(1060, 10, 10, 600);//right
        g.fillRect(10, 600, 1060, 10);//down
        
        //draw players
        Color[] colors = {Color.red, Color.yellow, Color.green, Color.blue};
        for (int i = 0; i < alive.length; i++)
        {
            //draw player snakes
            g.setColor(colors[i]);
            
            //starting block
            g.fillRect(x[i][0], y[i][0], 20, 20);
            //other blocks
            for (int k = 1; k < x[i].length - 1; k++)
            {
                g.fillRect(x[i][k], y[i][k], 20, 20);
            }
            
            //last block
            if (x[i].length > 1)
            {
                int tx = x[i][x[i].length - 1];
                int ty = y[i][y[i].length - 1];
                //5 frames per block
                if (direction2[i].equals("up"))
                {
                    g.fillRect(tx, ty + (framesLeft * 4), 20, 20 - (framesLeft * 4));
                }
                else if (direction2[i].equals("left"))
                {
                    g.fillRect(tx + (framesLeft * 4), ty, 20 - (framesLeft * 4), 20);
                }
                else if (direction2[i].equals("down"))
                {
                    g.fillRect(tx, ty, 20, 20 - (framesLeft * 4));
                }
                else if (direction2[i].equals("right"))
                {
                    g.fillRect(tx, ty, 20 - (framesLeft * 4), 20);
                }
            }
        }
        
        for (int i = 0; i < alive.length; i++)
        {
            if (!alive[i])
            {
                //draw explosion
                try
                {
                    int p = x[i][x[i].length - 1];
                    int oo = y[i][y[i].length - 1];
                    g.drawImage(explosion,
                    p - 20, oo - 20, p + 40, oo + 40,
                    0, 0, 480, 480,
                    this);
                }
                catch (Exception e){}
            }
        }
        
        g.setColor(Color.white);
        for (int i = 0; i < numberOfPlayers; i++)
        {
            //shows dead or alive
            g.setColor(colors[i]);
            if (alive[i])
            {
                g.fillRect(125 + (i * 800 / numberOfPlayers), 635, 20, 20);
            }
            g.setColor(Color.white);
            g.drawRect(125 + (i * 800 / numberOfPlayers), 635, 20, 20);
            
            //score
            g.drawString("Player " + (i + 1) + ": " + Integer.toString(score[i]),  150 + (i * 800 / numberOfPlayers), 650);
            g.drawRect(145 + (i * 800 / numberOfPlayers), 635, 85, 20);
        }
        
        //selected number of players
        g.setColor(Color.orange);
        g.fillRect(820 + 50 * numberOfPlayers, 645, 50, 25);
        
        g.setColor(Color.white);
        g.drawRect(920, 620, 150, 25);
        g.drawString("Number of Players", 940, 637);
        for (int i = 0; i < 3; i++)
        {
            g.drawRect(920 + 50 * i, 645, 50, 25);
            g.drawString(Integer.toString(i + 2), 940 + 50 * i, 662);
        }
        
        
        //show instructions at beginning
        if (instructions)
        {
            //labels
            g.setColor(Color.black);
            g.fillRect(0, 0, 2000, 2000);
            
            //instructions
            String[] keys = {"w", "a" , "s", "d",     "^", "<", "v", ">",     "t", "f", "g", "h",     "i", "j", "k", "l"};
            g.setColor(Color.white);
            for (int i = 0; i < 4; i++)
            {
                g.drawString("Player " + Integer.toString(i + 1) + " Controls", 177 + 200 * i, 200);
                g.drawRect(215 + 200 * i, 225, 30, 30);
                
                g.drawString(keys[i * 4], 227 + 200 * i, 245);
                for (int k = 0; k < 3; k++)
                {
                    g.drawRect(185 + (30 * k) + (200 * i), 255, 30, 30);
                    g.drawString(keys[k + (i * 4) + 1], 197 + (30 * k) + (200 * i), 275);
                }
            }
            g.drawString("Click anywhere to continue", 440, 400);
            
            g.setFont(title);
            g.drawString("BMTRON", 457, 100);
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        //everybody pick direction first
        boolean initialDirectionPicked = true;
        for (String direction : direction)
        {
            if (direction.equals("none"))
            {
                initialDirectionPicked = false;
            }
        }
        //new block
        for (int i = 0; i < alive.length; i++)
        {
            int xx = x[i][x[i].length - 1];
            int yy = y[i][y[i].length - 1];

            if (alive[i] && initialDirectionPicked && delay == 30 && framesLeft == 0)//prevent movement if not all players move and if in end and if instructions still up
            {
                switch (direction[i])//create new blocks
                {
                    case "up":
                        direction2[i] = "up";
                        newBlock(i, xx, yy - 20);
                        break;
                    case "down":
                        direction2[i] = "down";
                        newBlock(i, xx, yy + 20);
                        break;
                    case "right":
                        direction2[i] = "right";
                        newBlock(i, xx + 20, yy);
                        break;
                    case "left":
                        direction2[i] = "left";
                        newBlock(i, xx - 20, yy);
                        break;
                }
            }
        }
        for (int i = 0; i < alive.length; i++)//if lines collide
        {
            if (collision(i, x[i], y[i]))
            {
                //explosion sound effect
                if (alive[i])
                {
                    try
                    {
                        audio = AudioSystem.getAudioInputStream(this.getClass().getResource("explosion.wav"));
                        Clip clip = AudioSystem.getClip();
                        clip.open(audio);
                        clip.start();
                    }
                    catch(Exception d){}
                }
                alive[i] = false;
            }
        }
        
        //check if game finished
        int counter = numberOfPlayers;
        int survivor = -1;
        for (int i = 0; i < alive.length; i++)
        {
            if (!alive[i])
            {
                counter--;
            }
            else
            {
                survivor = i;
            }
        }
        if (delay == 0)//reset and increase score after delay at end
        {
            if (survivor != -1)
            {
                score[survivor]++;
            }
            delay = 30;
            framesLeft = 5;
            counter = numberOfPlayers;
            survivor = -1;
            alive = new boolean[0];
            direction = new String[0];
            for (int i = 0; i < x.length; i++)
            {
                x[i] = Arrays.copyOf(x[i], 1);
                y[i] = Arrays.copyOf(y[i], 1);
            }
            for (int i = 0; i < direction2.length; i++)
            {
                direction2[i] = "none";
            }
        }
        
        if (framesLeft == 0 && counter > 1) framesLeft = 5;//when framesLeft = 0 new block formed (framesLeft for frames per block)
        if (counter <= 1)//after number of alive players is 1 or 0, delay to see end result
        {
            delay--;
            framesLeft = 0;
        }
        else if (initialDirectionPicked) framesLeft--;
        
        repaint();
    }
    public void keyPressed(KeyEvent e)
    {
        //direction
        if (!instructions)
        {
            switch (e.getKeyCode())
            {
                //player 1 red
                case KeyEvent.VK_W: switchDirection(1, "up");
                    break;
                case KeyEvent.VK_A: switchDirection(1, "left");
                    break;
                case KeyEvent.VK_S: switchDirection(1, "down");
                    break;
                case KeyEvent.VK_D: switchDirection(1, "right");
                    break;
                    
                //player 2 yellow
                case KeyEvent.VK_UP: switchDirection(2, "up");
                    break;
                case KeyEvent.VK_LEFT: switchDirection(2, "left");
                    break;
                case KeyEvent.VK_DOWN: switchDirection(2, "down");
                    break;
                case KeyEvent.VK_RIGHT: switchDirection(2, "right");
                    break;
                    
                //player 3 green
                case KeyEvent.VK_T: switchDirection(3, "up");
                    break;
                case KeyEvent.VK_F: switchDirection(3, "left");
                    break;
                case KeyEvent.VK_G: switchDirection(3, "down");
                    break;
                case KeyEvent.VK_H: switchDirection(3, "right");
                    break;
    
                //player 4 blue
                case KeyEvent.VK_I: switchDirection(4, "up");
                    break;
                case KeyEvent.VK_J: switchDirection(4, "left");
                    break;
                case KeyEvent.VK_K: switchDirection(4, "down");
                    break;
                case KeyEvent.VK_L: switchDirection(4, "right");
                    break;
            }
        }
    }
    public void mousePressed(MouseEvent e)
    {
        instructions = false;
        int xco = e.getX();
        int yco = e.getY();
        for (int i = 0; i < 3; i++)
        {
            if (xco > 920 + 50 * i && xco < 970 + 50 * i && yco > 645 && yco < 670)
            {
                numberOfPlayers = i + 2;
                delay = 30;
                alive = new boolean[0];
                direction = new String[0];
                for (int k = 0; k < x.length; k++)
                {
                    x[k] = Arrays.copyOf(x[k], 1);
                    y[k] = Arrays.copyOf(y[k], 1);
                }
                for (int k = 0; k < direction2.length; k++)
                {
                    direction2[i] = "none";
                }
                for (int k = 0; k < score.length; k++)
                {
                    score[k] = 0;
                }
            }
        }
    }
    
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void keyClicked(KeyEvent e){}
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}
