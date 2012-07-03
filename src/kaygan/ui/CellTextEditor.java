package kaygan.ui;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class CellTextEditor extends JTextPane
{
	private static final long serialVersionUID = 1L;

	public CellTextEditor()
	{	
		setFont( new Font("Consolas", Font.PLAIN, 12) );
		
		StyledDocument doc = getStyledDocument();
		
		try
		{
			doc.insertString(0, "Hello World", null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
