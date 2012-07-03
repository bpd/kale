package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kaygan.BlockReader;
import kaygan.Function;

public class CellEditor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CellTextEditor text = new CellTextEditor();
	
	private final ReadEvalPrintPane repl = new ReadEvalPrintPane();
	
	public CellEditor()
	{
		setLayout( new BorderLayout() );
		
		text.setPreferredSize( new Dimension(400, 400) );
		
		add( text, BorderLayout.CENTER );
		
		add( repl, BorderLayout.SOUTH );
		
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_F5 )
				{
					try
					{
						String input =  text.getText();
						
						Function f = BlockReader.eval( input );
						
						repl.add( new JLabel(f.toString()) );
					}
					catch(RuntimeException re)
					{
						repl.add( new JLabel("Error: " + re.getMessage()) );
					}
					repl.validate();
					repl.repaint();
				}
			}	
		});
	}
	
	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame frame = new JFrame("Cell Editor");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				frame.add( new CellEditor() );
				
				frame.pack();
				frame.setVisible(true);
				
			}
		});
	}
}
