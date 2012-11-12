package kale.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProjectBrowser extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public ProjectBrowser()
	{
		setLayout( new BorderLayout() );
		
		setPreferredSize( new Dimension(100, -1) );
		
		add( new JLabel("[Browse Project]"), BorderLayout.CENTER );
	}
}
