package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.StringReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import kaygan.Interpreter;
import kaygan.Parser;
import kaygan.Scope;
import kaygan.ast.Program;

public class CodeEditor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CodePane text = new CodePane();
	
	private final ReadEvalPrintPane repl = new ReadEvalPrintPane()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void onRequestEval(String input)
		{
			CodeEditor.this.eval(input);
		}
	};
	
	private transient Scope scope = new Scope();
	
	private long lastParseTime = 0;
	
	/** in milliseconds */
	private static final long PARSE_INTERVAL = 500;
	
	private volatile boolean dirty = true;
	
	public CodeEditor()
	{
		setLayout( new BorderLayout() );
		
		// GO button
		JButton goButton = new JButton("Run (F5)");
		goButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				execute();
			}	
		});
		add( goButton, BorderLayout.NORTH );
		
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
				int c = e.getKeyCode();
				if( c == KeyEvent.VK_F5 )
				{
					execute();
				}
				else if( c != KeyEvent.VK_LEFT
						&& c != KeyEvent.VK_RIGHT
						&& c != KeyEvent.VK_UP
						&& c != KeyEvent.VK_DOWN
						&& c != KeyEvent.VK_HOME
						&& c != KeyEvent.VK_END
						&& c != KeyEvent.VK_PAGE_UP
						&& c != KeyEvent.VK_PAGE_DOWN )
				{
					dirty = true;
				}
			}	
		});
		
		text.requestFocusInWindow();
		text.setCaretPosition( text.getStyledDocument().getLength() - 1 );
		
		// schedule the parser
	    new Timer().scheduleAtFixedRate(new TimerTask()
	    {
			@Override
			public void run()
			{
				long nextParseTime = lastParseTime + PARSE_INTERVAL;
				
				if( dirty 
					&& System.currentTimeMillis() > nextParseTime )
				{
					dirty = false;			

					parse( text.getText() );
				}
			}
	    	
	    }, 0, 500);
	}
	
	protected void execute()
	{
		Program program = parse( text.getText() );
		
		if( program != null )
		{
			scope = new Scope();
			
			repl.reset();
			
			eval( program );
			
			text.setAST(program);
			
			lastParseTime = System.currentTimeMillis();
		}
		
		repl.validate();
		repl.repaint();
	}
	
	protected void eval( String input )
	{
		Program program = parse(input);
		
		if( program != null )
		{
			eval(program);
		}
	}
	
	protected void eval( Program program )
	{
		try
		{
			// interpret the program AST within the scope
			List<Object> results = Interpreter.interpret( program, scope );
			
			for( Object result : results )
			{
				repl.addResult( result );
			}
		}
		catch(Interpreter.InterpretException ie)
		{
			text.error(	ie.getMessage(), 
						ie.astNode.getOffset(), 
						ie.astNode.getLength() );

			repl.addError( "Error: " + ie.getMessage() );
		}
		catch(RuntimeException re)
		{
			repl.addError( "Error: " + re.getMessage() );
		}
	}
	
	public synchronized Program parse( String text )
	{
		try
		{
			Parser parser = new Parser( new StringReader( text ) );
			
			// first reset all style
			Program program = parser.program();
			
			if( program != null )
			{
				program.verify();
				program.inferTypes();
				
				// hand the AST over to the code pane to highlight
				this.text.setAST(program);
			}
			
			return program;
		}
		catch(Parser.ParseException pe)
		{
			// parsing probably failed because the person was
			// in the middle of typing
			
			System.out.println("Exception occured parsing: " + pe);

//			if( pe.token != null)
//			{
//				doc.setCharacterAttributes(
//						pe.token.beginOffset, 
//						pe.token.endOffset - pe.token.beginOffset,
//						errorStyle,
//						false);
//			}
		}
		catch(RuntimeException e)
		{
			System.out.println("Exception occured parsing");
		}
		
		
		return null;
	}
	
	
	static
	{
		// Install the look and feel
		try
		{
		    UIManager.setLookAndFeel(
		    		UIManager.getSystemLookAndFeelClassName() );
		}
		catch (Throwable e)
		{
			// can't do anything
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
				
				CodeEditor editor = new CodeEditor();
				
				frame.add( editor );
				
				frame.pack();
				
				editor.text.requestFocusInWindow();
				
				frame.setVisible(true);
				
			}
		});
	}
}
