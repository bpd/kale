package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public abstract class ReadEvalPrintPane extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	final JTextPane output = new JTextPane();
	
	final JTextField input = new JTextField();
	
	public ReadEvalPrintPane()
	{
		setLayout( new BorderLayout() );
		
		// text output
		output.setEditable(false);
		output.setPreferredSize( new Dimension(-1, 150) );
		output.setFont( CodePane.FONT );
		
		JScrollPane scrollOutput = new JScrollPane(output);
		scrollOutput.setPreferredSize( new Dimension(-1, 150) );
		
		add( scrollOutput, BorderLayout.CENTER );
		
		// REPL input
		input.setFont( CodePane.FONT );
		add( input, BorderLayout.SOUTH );
		
		input.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					onRequestEval( input.getText() );
					input.setText("");
				}
			}
		});
	}
	
	public abstract void onRequestEval(String input);
	
	public void reset()
	{
		output.setText("");
		input.setText("");
	}
	
	public void focus()
	{
		input.requestFocusInWindow();
	}
	
	public void addInfo( String message)
	{
		StyledDocument doc = output.getStyledDocument();
		
		try
		{
			doc.insertString(doc.getLength(), message, null);
			doc.insertString(doc.getLength(), "\n", null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void addError( String message )
	{
		StyledDocument doc = output.getStyledDocument();
		
		try
		{
			doc.insertString(doc.getLength(), message, null);
			doc.insertString(doc.getLength(), "\n", null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void addResult( Object f )
	{
		StyledDocument doc = output.getStyledDocument();
		
		try
		{
			doc.insertString(doc.getLength(), f.toString(), null);

			doc.insertString(doc.getLength(), "\n", null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
