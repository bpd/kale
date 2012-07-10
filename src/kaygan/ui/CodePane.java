package kaygan.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodePane extends JTextPane
{
	private static final long serialVersionUID = 1L;
	
	SimpleAttributeSet errorStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet normalStyle = new SimpleAttributeSet();

	public CodePane()
	{	
		setFont( new Font("Consolas", Font.PLAIN, 12) );
		
	    StyleConstants.setForeground(errorStyle, Color.RED);
	    StyleConstants.setUnderline(errorStyle, true);

		
//		DefaultHighlighter.DefaultHighlightPainter highlightPainter = 
//				new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//		textPane.getHighlighter().addHighlight(startPos, endPos, 
//		            highlightPainter);
	    
	    StringBuilder code = new StringBuilder();
	    try
	    {
		    InputStream is = CodePane.class.getResourceAsStream("/kaygan/example.lang");
		    if( is != null )
		    {
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    	try
		    	{
			    	String line = null;
			    	while( (line = reader.readLine()) != null )
			    	{
			    		code.append( line ).append('\n');
			    	}
		    	}
		    	finally
		    	{
		    		reader.close();
		    	}
		    }
	    }
	    catch(IOException e)
	    {
	    	// eat it
	    }
	    
		
	    StyledDocument doc = getStyledDocument();
		try
		{
			doc.insertString(0, code.toString(), null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void reset()
	{
		StyledDocument doc = getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), normalStyle, true);
	}
	
	public void error(String message, int offset, int length)
	{
	    StyledDocument doc = getStyledDocument();
	    doc.setCharacterAttributes(offset, length, errorStyle, false);	    
	}
}
