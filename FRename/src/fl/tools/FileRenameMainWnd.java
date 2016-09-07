package fl.tools;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;

import javax.swing.UIManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DirectoryDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import fl.tools.DirectoryChooser;
import org.eclipse.swt.graphics.Point;


@SuppressWarnings("unused")
public class FileRenameMainWnd 
{

	protected Shell shell;
	private Text _directory;
	private Text _filtreFichiers;
	private Text _ExpressionRenommage;
	private Text _message;
	private ProgressBar _progressBar; 
	private Button btnUndo;
	
	public String GetDirectory() { return _directory.getText(); } 
	public String GetFiltre() { return _filtreFichiers.getText(); } 
	public String GetExpression() { return _ExpressionRenommage.getText(); } 

    private Pattern _pattern;
    private Matcher _matcher;
    
    private List<String> _undoSource = null;
    private List<String> _undoTarget = null;
    private int _undoNumber = 0;
       
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			FileRenameMainWnd window = new FileRenameMainWnd();
			window.open();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() 
	{
		Display display = Display.getDefault();
		
		createContents();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch()) 
			{
				display.sleep();
			}
		}
	}
	
	private void CreateUndo()
	{
			_undoSource = null;
			_undoTarget = null;
			_undoNumber = 0;
			
			_undoSource = new ArrayList<String>();
			_undoTarget = new ArrayList<String>();
	}
	
	private void AddUndo(String old, String New)
	{
		_undoSource.add(old);
		_undoTarget.add(New);
		_undoNumber++;
	}
	
	private void PerformUndo()
	{
		if(_undoNumber > 0)
		{
			_progressBar.setMinimum(0);
			_progressBar.setMaximum(_undoNumber);
			
			String buffer="";
			
			for(int i=0; i < _undoNumber; i++)
			{
				_progressBar.setSelection(i+1);
				
				File ancienFichier = new File(_undoSource.get(i));
			    File nouveau_nom = new File(_undoTarget.get(i));
	    
			    if (nouveau_nom.renameTo(ancienFichier))
			    {
					buffer += _undoTarget.get(i) + " -> " + _undoSource.get(i) + "\n"; 
			    }
			    else
			    {
			    	buffer += "ERROR : " + _undoTarget.get(i) + "\n";
			    }			    				
				
				_message.setText(buffer);			    
			}
			
			btnUndo.setEnabled(false);			
		}
	}
	


	/**
	 * Create contents of the window.
	 */
	protected void createContents() 
	{
		shell = new Shell();
		shell.setMinimumSize(new Point(818, 536));
		shell.setSize(818, 536);
		shell.setText("SWT Application");

		shell.addListener (SWT.Resize,  new Listener () {
		    public void handleEvent (Event e) 
		    {
		    	int dx, dy;
		    	
		    	Rectangle rect = shell.getClientArea ();
		    	
		    	dx = rect.width - 20;
		    	dy = rect.height  - 272;
		    	
		    	_message.setSize(dx, dy);
		    	
		    }
		  });
		
		
		 try 
		 {
		        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		        
		 }catch(Exception ex) 
		 {
		        ex.printStackTrace();
		 }		
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFichiers = new MenuItem(menu, SWT.CASCADE);
		mntmFichiers.setText("Fichiers");
		
		Menu menu_1 = new Menu(mntmFichiers);
		mntmFichiers.setMenu(menu_1);
		
		MenuItem mntmRepertoire = new MenuItem(menu_1, SWT.NONE);
		mntmRepertoire.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
			    DirectoryDialog dialog = new DirectoryDialog(shell);
			    dialog.setMessage("Directory");
			    dialog.setFilterPath(_directory.getText()); // Windows specific
			    
			    String selectedDirectoryName = dialog.open();
			    
			    System.out.println("RESULT=" + selectedDirectoryName);
			    
			    if (selectedDirectoryName != null) {
			    	_directory.setText(selectedDirectoryName);
				}
			    
			}

				
				
		});
		mntmRepertoire.setText("Repertoire");
		
		MenuItem mntmQuitter = new MenuItem(menu_1, SWT.NONE);
		mntmQuitter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				shell.dispose();
				System.exit(0);
			}
		});
		mntmQuitter.setText("Quitter");
		
		Label lblRepertoire = new Label(shell, SWT.NONE);
		lblRepertoire.setBounds(10, 29, 55, 15);
		lblRepertoire.setText("Repertoire");
		
		_directory = new Text(shell, SWT.BORDER);
		_directory.setBounds(72, 29, 541, 21);
		_directory.setText(".");
		
		Button btnParcourir = new Button(shell, SWT.NONE);
		btnParcourir.addSelectionListener(new SelectionAdapter() 
		 {
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				
			    DirectoryDialog dialog = new DirectoryDialog(shell);
			    dialog.setMessage("Directory");
			    dialog.setFilterPath(_directory.getText()); // Windows specific
			    
			    String selectedDirectoryName = dialog.open();
			    
			    System.out.println("RESULT=" + selectedDirectoryName);
			    
			    if (selectedDirectoryName != null) {
			    	_directory.setText(selectedDirectoryName);
				}
				
			}
		 }
		);
		
		
		btnParcourir.setBounds(643, 29, 75, 25);
		btnParcourir.setText("Parcourir");
		
		Label lblFiltreDesFichiers = new Label(shell, SWT.NONE);
		lblFiltreDesFichiers.setBounds(10, 85, 149, 38);
		lblFiltreDesFichiers.setText("Filtre des fichiers en entr\u00E9e\r\n(Expression r\u00E9guli\u00E8re)");
		
		_filtreFichiers = new Text(shell, SWT.BORDER);
		_filtreFichiers.setBounds(165, 85, 448, 21);
		_filtreFichiers.setText(".*");
		
		Button btnVerifierFiltre = new Button(shell, SWT.NONE);
		btnVerifierFiltre.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				// parcourir les fichiers du répertoire en fonction du filtre regexp
				_pattern = Pattern.compile(_filtreFichiers.getText());
				
				File rep = new File(_directory.getText());
				File[] fichiers = rep.listFiles(new FilenameFilter()
				{
				  public boolean accept(File dir, String name) 
				  {
					  _matcher = _pattern.matcher(name);
				    return _matcher.find();
				  }
				});				
				
				String buffer = "";
				
				for(int i = 0; i < fichiers.length; i++)
				{
					buffer += fichiers[i].getName() + "\n"; 
				}
				
				_message.setText(buffer);
			}
		});
		btnVerifierFiltre.setBounds(643, 85, 75, 25);
		btnVerifierFiltre.setText("Tester");
		
		Label lblExpressionDeRenommage = new Label(shell, SWT.NONE);
		lblExpressionDeRenommage.setBounds(10, 148, 149, 38);
		lblExpressionDeRenommage.setText("Expression de renommage\r\n(Expression R\u00E9guli\u00E8re)");
		
		_ExpressionRenommage = new Text(shell, SWT.BORDER);
		_ExpressionRenommage.setBounds(165, 148, 448, 21);
		
		Button btnVerifierExpression = new Button(shell, SWT.NONE);
		btnVerifierExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// parcourir les fichiers du répertoire en fonction du filtre regexp
				_pattern = Pattern.compile(_filtreFichiers.getText());
				
				File rep = new File(_directory.getText());
				File[] fichiers = rep.listFiles(new FilenameFilter()
				{
				  public boolean accept(File dir, String name) 
				  {
					  _matcher = _pattern.matcher(name);
				    return _matcher.find();
				  }
				});				
				
				String buffer = "";
				
				for(int i = 0; i < fichiers.length; i++)
				{
					Pattern p = Pattern.compile(_filtreFichiers.getText());
					// création du moteur associé à la regex sur la chaîne "J'aime le thé."
					Matcher m = p.matcher(fichiers[i].getName());
					// remplacement de toutes les occurrences de "thé" par "chocolat"
					String Strings = m.replaceFirst(_ExpressionRenommage.getText());
					
					buffer += fichiers[i].getName() + " -> " + Strings + "\n"; 
				}
				
				_message.setText(buffer);
			}
		});
		btnVerifierExpression.setBounds(643, 148, 75, 25);
		btnVerifierExpression.setText("Tester");
		
		Button btnRenommer = new Button(shell, SWT.NONE);
		
		btnRenommer.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				_pattern = Pattern.compile(_filtreFichiers.getText());
				
				File rep = new File(_directory.getText());
				File[] fichiers = rep.listFiles(new FilenameFilter()
				{
				  public boolean accept(File dir, String name) 
				  {
					  _matcher = _pattern.matcher(name);
				    return _matcher.find();
				  }
				});				
				
				String buffer = "";
				
				_progressBar.setMinimum(0);
				_progressBar.setMaximum(fichiers.length);
				
				CreateUndo();
				
				for(int i = 0; i < fichiers.length; i++)
				{
					_progressBar.setSelection(i+1);
					Pattern p = Pattern.compile(_filtreFichiers.getText());
					// création du moteur associé à la regex sur la chaîne "J'aime le thé."
					Matcher m = p.matcher(fichiers[i].getName());
					// remplacement de toutes les occurrences de "thé" par "chocolat"
					String Strings = m.replaceFirst(_ExpressionRenommage.getText());
					
				    
				    buffer += "GetName : " + fichiers[i].getName() + "\n";
				    buffer += "GetPath : " + fichiers[i].getPath() + "\n";
				    
				    String path = fichiers[i].getPath();
				    String filePath = path.substring(0,path.lastIndexOf(File.separator));
				    buffer += "filePath : " + filePath + "\n";

				    File nouveau_nom = new File(filePath + "\\" + Strings);
				    
					AddUndo(fichiers[i].getPath(), filePath  + "\\" + Strings);
				   				    
				    
				    if (fichiers[i].renameTo(nouveau_nom))
				    {
						buffer += "OK :" + fichiers[i].getName() + " -> " + Strings + "\n"; 
				    }
				    else
				    {
				    	buffer += "ERROR : " + fichiers[i].getName() + "\n";
				    }
				    
				}
				
				_message.setText(buffer);
				btnUndo.setEnabled(true);
				
			}
		});
		btnRenommer.setBounds(10, 209, 99, 38);
		btnRenommer.setText("Renommer !");
		
		btnUndo = new Button(shell, SWT.NONE);
		btnUndo.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				PerformUndo();
			}
		});
		btnUndo.setEnabled(false);
		btnUndo.setBounds(132, 209, 99, 38);
		btnUndo.setText("Undo");
		
		_progressBar = new ProgressBar(shell, SWT.SMOOTH);
		_progressBar.setBounds(242, 209, 371, 38);
		
		_message = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		_message.setBounds(10, 266, 781, 201);
		
		shell.addListener (SWT.Resize,  new Listener () 
			{
				    public void handleEvent (Event e) 
				    {
				      Rectangle rect = shell.getClientArea ();				      
				    }
			}
		);

	}
}
