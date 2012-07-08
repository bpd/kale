package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CodeEditor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CodePane text = new CodePane();
	
	private final ReadEvalPrintPane repl = new ReadEvalPrintPane();
	
	public CodeEditor()
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
			repl.addInfo("Load");
			repl.reset();
			repl.eval(input);
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
				JFrame frame = new JFrame("Kaygan Editor");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				frame.add( new CodeEditor() );
				
				frame.pack();
				frame.setVisible(true);
				
			}
		});
	}
}
