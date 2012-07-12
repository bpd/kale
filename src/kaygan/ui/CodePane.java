package kaygan.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
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
import kaygan.ast.Num;
import kaygan.ast.Program;
import kaygan.ast.Str;
import kaygan.ast.Symbol;

public class CodePane extends JTextPane
{
	private static final long serialVersionUID = 1L;
	
	public static final Font FONT = new Font("Consolas", Font.PLAIN, 14);
	
	public static final Color STRING_COLOR = new Color(0, 162, 232);
	
	public static final Color SYMBOL_COLOR = new Color(75, 60, 255);
	
	public static final Color ARG_COLOR = new Color(255, 128, 70);
	
	public static final Color NUM_COLOR = new Color(35, 175, 75);
	
	
	SimpleAttributeSet errorStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet normalStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet stringStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet symbolStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet argStyle = new SimpleAttributeSet();
	
	SimpleAttributeSet numStyle = new SimpleAttributeSet();
	
	volatile boolean dirty = true;
	
	volatile Program lastParsed = null;

	public CodePane()
	{	
		setFont( FONT );
		
	    StyleConstants.setForeground(errorStyle, Color.RED);
	    StyleConstants.setUnderline(errorStyle, true);
	    
	    StyleConstants.setForeground(stringStyle, STRING_COLOR);
	    
	    StyleConstants.setForeground(symbolStyle, SYMBOL_COLOR);

	    StyleConstants.setForeground(argStyle, ARG_COLOR);
	    
	    StyleConstants.setForeground(numStyle, NUM_COLOR);

		
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
	    
	    // schedule the parser
	    new Timer().scheduleAtFixedRate(new TimerTask()
	    {
			@Override
			public void run()
			{
				parse();
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
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	
	
	@Override
	public String getToolTipText(MouseEvent event)
	{
		int position = viewToModel(event.getPoint());
		
		if( lastParsed != null )
		{
			ASTNode node = lastParsed.findNode(position);
			if( node != null )
			{
				return node.toString();
			}
		}
		
		return "";
	}



	public void dirty()
	{
		this.dirty = true;
	}
	
	private long lastParseTime = 0;
	
	/** in milliseconds */
	private static final long PARSE_INTERVAL = 500;
	
	public void parse()
	{
		if( dirty 
			&& System.currentTimeMillis() > lastParseTime + PARSE_INTERVAL )
		{
			dirty = false;
			
			StyledDocument doc = getStyledDocument();
			
			try
			{
				Parser parser = new Parser( new StringReader( getText() ) );
				
				// first reset all style
				Program program = parser.program();
				
				if( program != null )
				{
					lastParsed = program;
					
					// first reset style
					highlight( doc, program, normalStyle );
					
					// then recursively highlight nodes
					highlight( doc, program );
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
			
			
			lastParseTime = System.currentTimeMillis();
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
			
			for( Exp e : c.contents )
			{
				highlight(doc, e);
			}
		}
		else if( node instanceof Program )
		{
			for( Exp exp : ((Program)node) )
			{
				highlight( doc, exp );
			}
		}
		else if( node instanceof Num )
		{
			highlight( doc, node, numStyle );
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
