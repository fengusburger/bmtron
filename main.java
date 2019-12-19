import javax.swing.*;
public class main
{
    public static void main(String[] args)
    {
        //Frame
        JFrame frame = new JFrame("BMTRON");
        frame.setSize(1090, 710);
        
        //Game
        Bmtron tron = new Bmtron();
        frame.add(tron);
        frame.addKeyListener(tron);
        tron.addMouseListener(tron);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
