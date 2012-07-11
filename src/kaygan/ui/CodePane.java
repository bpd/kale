package kaygan.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import kaygan.Parser;
import kaygan.Token;
import kaygan.ast.ASTNode;
import kaygan.ast.Array;
import kaygan.ast.Bind;
import kaygan.ast.Callsite;
import kaygan.ast.Exp;
import kaygan.ast.Function;
import kaygan.ast.Range;
import kaygan.ast.Str;
import kaygan.ast.Symbol;

public class CodePane extends JTextPane
{
	private static final long serialVersionUID = 1L;
	
	public static final Font FONT = new Font("Consolas", Font.PLAIN, 14);
	
	public static final Color STRING_COLOR = new Color(0, 162, 232);
	
	public static final Color SYMBOL_COLOR = new Color(75, 60, 255);
	
	public static final Color ARG_COLOR = new Color(255, 128, 70);
	
	
	SimpleAttributeSet errorStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet normalStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet stringStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet symbolStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet argStyle = new SimpleAttributeSet();
	
	volatile boolean dirty = true;

	public CodePane()
	{	
		setFont( FONT );
		
	    StyleConstants.setForeground(errorStyle, Color.RED);
	    StyleConstants.setUnderline(errorStyle, true);
	    
	    StyleConstants.setForeground(stringStyle, STRING_COLOR);
	    
	    StyleConstants.setForeground(symbolStyle, SYMBOL_COLOR);

	    StyleConstants.setForeground(argStyle, ARG_COLOR);

		
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
	    
	    // schedule the highlighter
	    new Timer().scheduleAtFixedRate(new TimerTask()
	    {
			@Override
			public void run()
			{
				highlight();
			}
	    	
	    }, 0, 500);
	    
		
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
	
	public void dirty()
	{
		this.dirty = true;
	}
	
	private long lastHighlight = 0;
	
	/** in milliseconds */
	private static final long HIGHLIGHT_INTERVAL = 500;
	
	public void highlight()
	{
		if( dirty 
			&& System.currentTimeMillis() > lastHighlight + HIGHLIGHT_INTERVAL )
		{
			dirty = false;
			highlight( getStyledDocument() );
			lastHighlight = System.currentTimeMillis();
		}
		
	}
	
	protected void highlight(StyledDocument doc)
	{
		try
		{
			Parser parser = new Parser( new StringReader( getText() ) );
			
			// first reset all style
			for( Exp exp : parser.program() )
			{
				highlight(doc, exp);
			}
		}
		catch(Parser.ParseException pe)
		{
			// parsing probably failed because the person was
			// in the middle of typing

			if( pe.token != null)
			{
				doc.setCharacterAttributes(
						pe.token.beginOffset, 
						pe.token.endOffset - pe.token.beginOffset,
						errorStyle,
						false);
			}
		}
		catch(RuntimeException e)
		{
			System.out.println("Exception occured parsing");
		}
	}
	
	protected void highlight(StyledDocument doc, ASTNode node)
	{
		if( node instanceof Str )
		{
			highlight( doc, node, stringStyle );
		}
		else if( node instanceof Bind )
		{
			Bind bind = (Bind)node;
			highlight( doc, bind.symbol );
			highlight( doc, bind.exp );
		}
		else if( node instanceof Symbol )
		{
			highlight( doc, node, symbolStyle );
		}
		else if( node instanceof Function )
		{
			Function f = (Function)node;
			
			highlight( doc, f.open, normalStyle );
			highlight( doc, f.close, normalStyle );
			
			for( Exp e : f.args )
			{
				highlight( doc, e, argStyle );
			}
			for( Exp e : f.contents )
			{
				highlight(doc, e);
			}
		}
		else if( node instanceof Array )
		{
			Array a = (Array)node;
			for( Exp e : a.contents )
			{
				highlight(doc, e);
			}
		}
		else if( node instanceof Callsite )
		{
			Callsite c = (Callsite)node;
			
			highlight( doc, c.open, normalStyle );
			highlight( doc, c.close, normalStyle );
			
			for( Exp e : c.contents )
			{
				highlight(doc, e);
			}
		}
		else if( node instanceof Range )
		{
			Range range = (Range)node;
			highlight( doc, range.from );
			highlight( doc, range.to );
		}
		else
		{
			highlight( doc, node, normalStyle );
		}
	}
	
	protected void highlight(	StyledDocument doc, 
								ASTNode node,
								SimpleAttributeSet style )
	{
		doc.setCharacterAttributes(	node.getOffset(),
									node.getLength(),
									style,
									true );
	}
	
	protected void highlight(	StyledDocument doc, 
								Token token,
								SimpleAttributeSet style )
	{
		doc.setCharacterAttributes(	token.beginOffset,
									token.endOffset - token.beginOffset,
									style,
									true );
	}
	
	public void error(String message, int offset, int length)
	{
	    StyledDocument doc = getStyledDocument();
	    doc.setCharacterAttributes(offset, length, errorStyle, false);	    
	}
}
