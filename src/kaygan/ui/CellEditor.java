package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kaygan.BlockReader;
import kaygan.Sequence;

public class CellEditor extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private volatile Sequence sequence = new Sequence();

	private final CellTextEditor text = new CellTextEditor();
	
	private final ReadEvalPrintPane repl = new ReadEvalPrintPane(sequence);
	
	public CellEditor()
	{
		setLayout( new BorderLayout() );
		
		// text editor
		text.setPreferredSize( new Dimension(400, 400) );
		
		JScrollPane scrollText = new JScrollPane(text);
		
		add( scrollText, BorderLayout.CENTER );
		
		// REPL
		repl.setPreferredSize( new Dimension(400, 200) );
		add( repl, BorderLayout.SOUTH );
		
		// listeners
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_F5 )
				{
					eval( text.getText() );
					
					repl.validate();
					repl.repaint();
				}
			}	
		});
	}
	
	protected void eval( String input )
	{
		try
		{
			Sequence result = BlockReader.eval( input );
			
			result.bind();
			
			// the result of the text eval is the new environment sequence,
			// so set it here and on the REPL
			this.sequence = result;
			
			repl.setSequence(result);
			
			repl.reset();
			repl.addResult(result);
			
			repl.addInfo("Loaded.");
			repl.focus();
		}
		catch(RuntimeException re)
		{
			repl.add( new JLabel("Error: " + re.getMessage()) );
		}
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
