package kaygan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Environment extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	// FIXME: coordinates and sizes should be handled by a cell layout engine
	//        individual cells/cell groups should have hooks into how they are displayed
	class Cell
	{
		volatile boolean visible = true;
		
		volatile Color fill = Color.LIGHT_GRAY;
		
		int padding = 4;
		
		int x = 100;
		
		int y = 100;
		
		int borderRadius = 10;
		
		Shape cellBounds;
		
		final StringBuffer value = new StringBuffer("Hello World");
		
		public void onMouseMove(MouseEvent e)
		{
			Color currentFill = fill;
			
			if( cellBounds != null )
			{
				if( cellBounds.contains(e.getPoint()) )
				{
					fill = Color.BLUE;
				}
				else
				{
					fill = Color.LIGHT_GRAY;
				}
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
		        
		        String val = value.toString();
		        
		        FontMetrics fontMetrics = g.getFontMetrics();
		        Rectangle2D textBounds = fontMetrics.getStringBounds(val, g);
		        
		        cellBounds = new RoundRectangle2D.Float(
									x, y, 
									(int)(textBounds.getWidth())+padding*2,
									(int)(textBounds.getHeight())+padding*2,
									borderRadius,
									borderRadius);
		        
		        g.draw(cellBounds);
		        g.fill(cellBounds);
		        
		        // draw the text within the cell we just created...
		        // the x/y need to be based on the baseline, so the y coordinate
		        // has to be adjusted based on how many pixels can be above the baseline
		        g.setColor(Color.WHITE);
		        g.drawString(val, x+padding, y+padding+fontMetrics.getMaxAscent());
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
        
        // clear drawing surface of the window
        graphics2.setColor(Color.BLACK);
        graphics2.fillRect(0, 0, getWidth(), getHeight());
       
        
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
