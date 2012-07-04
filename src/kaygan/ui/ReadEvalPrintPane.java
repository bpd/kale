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

import kaygan.BlockReader;
import kaygan.Function;
import kaygan.Sequence;
import kaygan.atom.Pair;

public class ReadEvalPrintPane extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	final JTextPane output = new JTextPane();
	
	final JTextField input = new JTextField();
	
	volatile Sequence sequence;
	public void setSequence(Sequence sequence)
	{
		this.sequence = sequence;
	}
	
	public ReadEvalPrintPane(Sequence sequence)
	{
		this.sequence = sequence;
		
		setLayout( new BorderLayout() );
		
		// text output
		output.setEditable(false);
		output.setPreferredSize( new Dimension(-1, 150) );
		
		JScrollPane scrollOutput = new JScrollPane(output);
		scrollOutput.setPreferredSize( new Dimension(-1, 150) );
		
		add( scrollOutput, BorderLayout.CENTER );
		
		// REPL input
		add( input, BorderLayout.SOUTH );
		
		input.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					eval( input.getText() );
					input.setText("");
				}
			}
		});
	}
	
	public void reset()
	{
		output.setText("");
		input.setText("");
	}
	
	public void focus()
	{
		input.requestFocusInWindow();
	}
	
	public void eval( String input )
	{
		try
		{
			Sequence result = BlockReader.eval( input );
			
			result.bindTo(this.sequence);
			
			for( Function f : result )
			{
				this.sequence.add( f );
			}
			
			addResult( result.eval() );
		}
		catch(RuntimeException re)
		{
			addError(re.getMessage());
		}
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
	
	public void addResult( Sequence sequence )
	{
		StyledDocument doc = output.getStyledDocument();
		
		StringBuilder sb = new StringBuilder();
		
		for( Function f : sequence )
		{
			if( !(f instanceof Pair) )
			{
				sb.append("=> ");
				sb.append( f.toString() );
				sb.append('\n');
			}
		}
		
		try
		{
			doc.insertString(doc.getLength(), sb.toString(), null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
