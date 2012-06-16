package kaygan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Environment extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	class Cell
	{
		volatile boolean visible = false;
		
		volatile Color fill = Color.LIGHT_GRAY;
		
		final RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(100, 100, 240, 160, 20, 20);
		
		public void onMouseMove(MouseEvent e)
		{
			Color currentFill = fill;
			
			if( roundedRectangle.contains(e.getPoint()) )
			{
				fill = Color.BLUE;
			}
			else
			{
				fill = Color.LIGHT_GRAY;
			}
			
			// repaint if we changed the fill
			if( !currentFill.equals(fill) )
			{
				Environment.this.repaint();
			}
		}
		
		public void render(Graphics2D g)
		{
			if( visible )
			{
				g.setBackground(fill);
		        g.setPaint(fill);
		        
		        g.draw(roundedRectangle);
		        g.fill(roundedRectangle);
			}
		}
	}
	
	Cell cell = new Cell();
	
	
	

	public Environment()
	{
		super("Kaygan Development Environment");
		
		MouseAdapter mouse = new MouseAdapter()
		{
			
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				cell.onMouseMove(e);
			}	
		};

		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				char key = e.getKeyChar();
				
				if( e.isControlDown() && key == ' ' )
				{
					cell.visible = !cell.visible;
					repaint();
				}
			}	
		});
	}
	
	
	@Override
	public void paint(Graphics g)
	{
        Graphics2D graphics2 = (Graphics2D) g;

        graphics2.setColor(Color.BLACK);
        graphics2.setBackground(Color.BLACK);
        graphics2.setPaint(Color.BLACK);
        
        graphics2.drawString("Hello", 20, 20);
        
        // clear drawing surface of the window
        graphics2.clearRect(0, 0, getWidth(), getHeight());
        
        // render cells
        cell.render(graphics2);
        
    }


	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Environment frame = new Environment();
		        
		        frame.setSize(800, 600);
		        
		        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
		        
		        //frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        
		        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		        frame.setVisible(true);
			}
		});
		
	}
}
