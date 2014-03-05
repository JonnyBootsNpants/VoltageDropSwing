package view;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

import static javax.swing.Action.SHORT_DESCRIPTION;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.*;

import components.VdropModel;
import constants.WireSize;

public class VdropFrame extends JFrame implements Observer {

	static final String DEFAULT_FILENAME = "calc.vdrop";
	static final Path DEFAULT_DIRECTORY = Paths.get(System.getProperty("user.home")).resolve("Vdrop");
	private ExtensionFilter calcFilter = new ExtensionFilter(".vdrop", "Voltage Drop Calculation Files (*.vdrop)");

	private FileAction newAction, openAction, saveAction, saveAsAction, printAction, exitAction;
	private final static String frameTitle = "Voltage Drop Calculator";
	private FileAction[] fileActions;
	private JMenuBar menuBar = new JMenuBar();
	private boolean calcChanged = false;
	private Path currentCalcFile;
	private JFileChooser fileChooser;

	private VdropModel vdropModel;
	private VDropPanel vdropPanel;

	public Path getFileName()
	{
			return currentCalcFile.getFileName();
	}
	public boolean isFileNameNull()
	{
		return currentCalcFile == null;
	}
	private void createFileMenu() {
		JMenu fileMenu = new JMenu("File"); // Create File menu
		fileMenu.setMnemonic('F'); // Create shortcut
		createFileMenuActions(); // Create Actions for File menu item

		// Create print setup menu item
		/*
		 * JMenuItem printSetupItem = new JMenuItem("Print Setup...");
		 * printSetupItem.setToolTipText("Setup the page for the printer");
		 * printSetupItem.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { // update the page format PageFormat
		 * pf = printJob.pageDialog(printAttr); if(pf != null) { pageFormat =
		 * pf; // update the page format } } });
		 * 
		 * // Menu item to print the application window JMenuItem
		 * printWindowItem = new JMenuItem("Print Window");
		 * printWindowItem.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { if(printer == null) {
		 * JOptionPane.showMessageDialog(SketcherFrame.this,
		 * "No default printer available.", "Printer Error",
		 * JOptionPane.ERROR_MESSAGE); return; } // The app window is the page
		 * source printJob.setPrintable(SketcherFrame.this, pageFormat); try {
		 * printJob.print(); } catch(PrinterException pe) {
		 * System.out.println(pe);
		 * JOptionPane.showMessageDialog(SketcherFrame.this,
		 * "Error printing the application window.", "Printer Error",
		 * JOptionPane.ERROR_MESSAGE); } } });
		 */

		// Construct the file drop-down menu
		fileMenu.add(newAction); // New Sketch menu item
		fileMenu.add(openAction); // Open sketch menu item
		fileMenu.addSeparator(); // Add separator
		fileMenu.add(saveAction); // Save sketch to file
		fileMenu.add(saveAsAction); // Save As menu item
		fileMenu.addSeparator(); // Add separator
		fileMenu.add(printAction); // Print sketch menu item
		// fileMenu.add(printSetupItem); // Print page setup menu item
		// fileMenu.add(printWindowItem); // Print window menu item
		fileMenu.addSeparator(); // Add separator
		fileMenu.add(exitAction); // Print sketch menu item
		menuBar.add(fileMenu); // Add the file menu
	}

	private void createFileMenuActions() {
		newAction = new FileAction("New", 'N', CTRL_DOWN_MASK);
		openAction = new FileAction("Open", 'O', CTRL_DOWN_MASK);
		saveAction = new FileAction("Save", 'S', CTRL_DOWN_MASK);
		saveAsAction = new FileAction("Save As...");
		printAction = new FileAction("Print", 'P', CTRL_DOWN_MASK);
		exitAction = new FileAction("Exit", 'X', CTRL_DOWN_MASK);

		// Initialize the array
		FileAction[] actions = { openAction, saveAction, saveAsAction, printAction, exitAction };
		fileActions = actions;

		// Add tooltip text
		newAction.putValue(SHORT_DESCRIPTION, "Start a new Voltage Drop Calculation");
		openAction.putValue(SHORT_DESCRIPTION, "Read a Voltage Drop Calculation from a file");
		saveAction.putValue(SHORT_DESCRIPTION, "Save the current Voltage Drop Calculation to file");
		saveAsAction.putValue(SHORT_DESCRIPTION, "Save the current Voltage Drop Calculation to a new file");
		printAction.putValue(SHORT_DESCRIPTION, "Print the current Voltage Drop Calculation");
		exitAction.putValue(SHORT_DESCRIPTION, "Exit Voltage Drop Calculation");
	}

	private Path showDialog(String dialogTitle,
			String approveButtonText,
			String approveButtonTooltip,
			ExtensionFilter filter,
			Path file) { // Current file path – if any
		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setApproveButtonText(approveButtonText);
		fileChooser.setApproveButtonToolTipText(approveButtonTooltip);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(filter); // Add the filter
		fileChooser.setFileFilter(filter); // and select it

		fileChooser.rescanCurrentDirectory();
		Path selectedFile = null;
		if (file == null) {
			selectedFile = Paths.get(
					fileChooser.getCurrentDirectory().toString(), DEFAULT_FILENAME);
		} else {
			selectedFile = file;
		}
		fileChooser.setSelectedFile(selectedFile.toFile());

		// Show the file save dialog
		int result = fileChooser.showDialog(this, null);
		return (result == JFileChooser.APPROVE_OPTION) ?
				Paths.get(fileChooser.getSelectedFile().getPath()) : null;
	}

	class FileAction extends AbstractAction 
	{
		// Create action with a name
		FileAction(String name) {
			super(name);
		}

		// Create action with a name and accelerator
		FileAction(String name, char ch, int modifiers) {
			super(name);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ch, modifiers));

			// Now find the character to underline
			int index = name.toUpperCase().indexOf(ch);
			if (index != -1) {
				putValue(DISPLAYED_MNEMONIC_INDEX_KEY, index);
			}
		}

		// Event handler
		public void actionPerformed(ActionEvent e) {
			if (this == saveAction) {
				saveOperation();
				return;
			} else if (this == saveAsAction) {
				saveAsOperation();
				return;
			} else if (this == openAction) {
				// Save current sketch if we need to
				checkForSave();

				// Now open a sketch file
				Path file = showDialog(
						"Open Voltage Drop Calculation File", // Dialog window
																// title
						"Open", // Button label
						"Read a calculation from file", // Button tooltip text
						calcFilter, // File filter
						null); // No file selected
				if (file != null) { // If a file was selected
					if (openCalc(file)) { // ...then read it
						currentCalcFile = file; // Success!
						setTitle(frameTitle + " - " + currentCalcFile.getFileName());
						calcChanged = false;
					}
					return;
				}
			} else if (this == newAction) {
				checkForSave();
				newCalc();
				currentCalcFile = null; // No file for it
				setTitle(frameTitle);
				calcChanged = false; // Not changed yet
				return;
			} else if (this == printAction) {
				showPrintOutput();
			}
			else if (this == exitAction) {
				System.exit(0);
			}
		}
	}
	private void showPrintOutput() {
		if (vdropPanel.isSingleSelected() && vdropPanel.isSingleCalcValid() ||
				!vdropPanel.isSingleSelected() && vdropPanel.isSeriesCalcValid())
			new PrintDialog(this, vdropPanel, vdropModel);
		else 
			JOptionPane.showMessageDialog(this, "Please enter information, calculation not valid.", "Warning", 
					JOptionPane.WARNING_MESSAGE);
	}

	private void checkDirectory(Path directory) {
		if (Files.notExists(directory)) {
			/*
			 * JOptionPane.showMessageDialog(null, "Creating directory: " +
			 * directory, "Directory Not Found",
			 * JOptionPane.INFORMATION_MESSAGE);
			 */
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				JOptionPane.showMessageDialog(null,
						"Cannot create: " + directory + ".",
						"Directory Creation Failed",
						JOptionPane.ERROR_MESSAGE);
				// System.exit(1);
			}
		}
	}

	private void newCalc() {
		boolean isSingle = vdropPanel.isSingleSelected();
		vdropModel = new VdropModel();
		vdropPanel = new VDropPanel(vdropModel);
		vdropModel.addObserver(this);
		setContentPane(vdropPanel);
		revalidate();
		repaint();
		vdropPanel.setSingle(isSingle);
	}

	VdropFrame() {
		super(frameTitle);

		checkDirectory(DEFAULT_DIRECTORY);

		fileChooser = new JFileChooser(DEFAULT_DIRECTORY.toString());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		vdropModel = new VdropModel();
		vdropPanel = new VDropPanel(vdropModel);

		vdropModel.addObserver(this);
		createFileMenu();

		add(vdropPanel);
		setJMenuBar(menuBar);

		setResizable(true);
		pack();
		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int taskBarHeight = scrnSize.height - winSize.height;

		final int width = getWidth();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double height = screenSize.getHeight();
		setMaximizedBounds(new Rectangle(new Dimension(width, (int) height - taskBarHeight)));
		setVisible(true);
	}

	public boolean openCalc(Path file) {
		try (ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(Files.newInputStream(file)))) {
			vdropPanel.setSingle(in.readBoolean());
			vdropPanel.setKVA(in.readBoolean());
			vdropPanel.setFeet(in.readBoolean());
			vdropPanel.setSeriesCopy((WireSize) in.readObject());
			VdropModel newModel = (VdropModel) in.readObject();
			vdropModel = newModel;
			vdropModel.addObserver(this);
			vdropPanel.insertModel(newModel);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			JOptionPane.showMessageDialog(this,
					"Error reading the calculation file.",
					"File Input Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public void checkForSave() {
		if (calcChanged && JOptionPane.YES_OPTION ==
				JOptionPane.showConfirmDialog(this,
						"Current file has changed. Save current file?",
						"Confirm Save Current File",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE)) {
			saveOperation();
		}
	}

	public void saveAsOperation() {
		Path file = showDialog("Save calculation as",
				"Save",
				"Save the calculation",
				calcFilter,
				currentCalcFile == null ?
						Paths.get(DEFAULT_FILENAME) : currentCalcFile);

		if (file == null) { // No file selected...
			return; // ...so we are done.
		}

		file = setFileExtension(file, "vdrop"); // Make sure extension is .ske

		if (Files.exists(file) &&
				!file.equals(currentCalcFile) &&
				JOptionPane.NO_OPTION == // Overwrite warning
				JOptionPane.showConfirmDialog(this,
						file.getFileName() + " exists. Overwrite?",
						"Confirm Save As",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE)) {
			return; // No file selected
		}

		if (saveCalc(file)) { // Save the sketch
			currentCalcFile = file; // Save successful
			setTitle(frameTitle + " - " + currentCalcFile.getFileName()); // Update title bar
			calcChanged = false; // Sketch now unchanged
		}

	}

	private void saveOperation() {
		if (!calcChanged) { // If the sketch is unchanged...
			return; // ... do nothing
		}

		if (currentCalcFile != null) { // If the sketch has been saved...
			if (saveCalc(currentCalcFile)) { // ... just save it.
				calcChanged = false; // Write successful
			}
			return;
		}

		// Here, the sketch was never saved...
		Path file = showDialog("Save Calculation", // ...so display Save dialog
				"Save",
				"Save the Voltage Drop Calculation",
				calcFilter,
				Paths.get(DEFAULT_FILENAME));
		if (file == null) { // No file selected...
			return; // ... so we are done.
		}

		file = setFileExtension(file, "vdrop"); // Make sure extension is .ske

		if (Files.exists(file) && // If the path exists and...
				JOptionPane.NO_OPTION == // .. NO selected in dialog...
				JOptionPane.showConfirmDialog(
						this,
						file.getFileName() + " exists. Overwrite?",
						"Confirm Save As",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE)) {
			return; // ...do nothing
		}
		if (saveCalc(file)) { // Save the sketch
			currentCalcFile = file; // Save successful
			setTitle(frameTitle + " - " + currentCalcFile.getFileName()); // Update title bar
			calcChanged = false; // Sketch now unchanged
		}
	}

	private Path setFileExtension(Path file, String extension) {
		StringBuffer fileName = new StringBuffer(file.getFileName().toString());
		if (fileName.indexOf(".") >= 0) {
			return file;
		}
		int index = fileName.lastIndexOf(".");
		if (index < 0) { // No extension
			fileName.append(".").append(extension); // so append one
		}
		return file.getParent().resolve(fileName.toString());
	}

	private boolean saveCalc(Path file) {
		try (ObjectOutputStream out =
				new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
			out.writeBoolean(vdropPanel.isSingleSelected());
			out.writeBoolean(vdropPanel.isKVASelected());
			out.writeBoolean(vdropPanel.isFeetSelected());
			out.writeObject(vdropPanel.getSeriesCopy());
			out.writeObject(vdropModel);
		} catch (IOException e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(this,
					"Error writing the calculation to " + file,
					"File Output Error",
					JOptionPane.ERROR_MESSAGE);
			return false; // Serious error - file not written
		}
		return true;
	}

	private static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new VdropFrame();
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				createAndShowGUI();
			}
		});
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		calcChanged = true;
	}
}
