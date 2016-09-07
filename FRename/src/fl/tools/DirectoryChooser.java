package fl.tools;

import javax.swing.JFileChooser;

public class DirectoryChooser extends JFileChooser 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2063837137905909491L;

	public DirectoryChooser(String title)
	{
		setCurrentDirectory(new java.io.File("."));
		setDialogTitle(title);
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		setAcceptAllFileFilterUsed(false);
	}

}
