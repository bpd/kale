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

import kaygan.cell.Cons;
import kaygan.cell.CellReader;

public class ReadEvalPrintPane extends JPanel
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
			Object o = CellReader.parse( input );
			
			addResult( o );
			
			// TODO cleaner way of duplicating/pulling in function bindings
			//this.sequence = result.clone(this.sequence);			
			
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
	
	public void addResult( Object f )
	{
		StyledDocument doc = output.getStyledDocument();
		
		try
		{
			if( f instanceof Cons )
			{
				doc.insertString(doc.getLength(), ((Cons)f).toCellString(), null);
			}
			else
			{
				doc.insertString(doc.getLength(), f.toString(), null);
			}
			doc.insertString(doc.getLength(), "\n", null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
