package turner;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class DictionaryApp {

	private JFrame frame;
	private JTextField textFieldWord;
	private JTextArea textAreaParagraph;
	private JTextField textFieldResult;

	// Shared by every button handler (was a local variable in initialize())
	private final Dictionary dict = new Dictionary();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DictionaryApp window = new DictionaryApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DictionaryApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(224, 224, 224));
		panel.setBounds(0, 0, 450, 22);
		frame.getContentPane().add(panel);

		JLabel lblHeader = new JLabel("Dictionary App");
		lblHeader.setToolTipText(
				"<html>Left: SpellCheck a word to see if it's in the dictionary<br><br>"
						+ "Right: Upload the entire contents of the dictionary to be checked</html>");
		lblHeader.setFont(new Font("Georgia", Font.BOLD, 15));
		panel.add(lblHeader);

		JLabel lblSpellChecker = new JLabel("spellChecker");
		lblSpellChecker.setFont(new Font("Georgia", Font.PLAIN, 13));
		lblSpellChecker.setToolTipText("Enter a word here, then press Check spelling to see if it's in the dictionary");
		lblSpellChecker.setBounds(57, 30, 81, 16);
		frame.getContentPane().add(lblSpellChecker);

		textFieldWord = new JTextField();
		textFieldWord.setBounds(6, 46, 195, 26);
		frame.getContentPane().add(textFieldWord);
		textFieldWord.setColumns(10);
		// Pressing Enter in the word field does the same thing as clicking Check spelling
		textFieldWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkSpelling();
			}
		});

		// Paragraph input: wrapped and scrollable so long paragraphs don't run off the edge
		textAreaParagraph = new JTextArea();
		textAreaParagraph.setLineWrap(true);
		textAreaParagraph.setWrapStyleWord(true);
		JScrollPane scrollPaneParagraph = new JScrollPane(textAreaParagraph);
		scrollPaneParagraph.setBounds(249, 46, 195, 140);
		frame.getContentPane().add(scrollPaneParagraph);

		JButton btnCheckSpelling = new JButton("Check spelling");
		btnCheckSpelling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkSpelling();
			}
		});
		btnCheckSpelling.setBounds(45, 73, 117, 29);
		frame.getContentPane().add(btnCheckSpelling);

		JButton btnUpdateDictionary = new JButton("Update Dictionary");
		btnUpdateDictionary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDictionary();
			}
		});
		btnUpdateDictionary.setBounds(285, 190, 141, 26);
		frame.getContentPane().add(btnUpdateDictionary);

		textFieldResult = new JTextField();
		textFieldResult.setEditable(false);
		textFieldResult.setBounds(6, 102, 195, 140);
		frame.getContentPane().add(textFieldResult);
		textFieldResult.setColumns(10);

		JLabel lblDictionary = new JLabel("Dictionary");
		lblDictionary.setFont(new Font("Georgia", Font.PLAIN, 13));
		lblDictionary.setToolTipText(
				"Enter a paragraph of text to use as the Dictionary for this session, then hit the \"Update Dictionary\" button to add each word in your paragraph to the Dictionary");
		lblDictionary.setBounds(312, 30, 82, 16);
		frame.getContentPane().add(lblDictionary);

		JButton btnChooseFile = new JButton("Choose File");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFileIntoParagraph();
			}
		});
		btnChooseFile.setBounds(295, 217, 117, 26);
		frame.getContentPane().add(btnChooseFile);

		JButton btnSaveFile = new JButton("Save File");
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveToFile();
			}
		});
		btnSaveFile.setBounds(295, 244, 117, 26);
		frame.getContentPane().add(btnSaveFile);
	}

	// ------------------------------------------------------------------
	// Button handlers
	// ------------------------------------------------------------------

	/** Check spelling button / Enter key in the word field. */
	private void checkSpelling() {
		String word = textFieldWord.getText().trim();
		if (word.isEmpty()) {
			textFieldResult.setText("Please enter a word to check.");
			return;
		}
		if (dict.size() == 0) {
			textFieldResult.setText("The dictionary is empty. Update the dictionary first.");
			return;
		}
		if (dict.isValidWord(word)) {
			textFieldResult.setText("\"" + word + "\" is a valid word.");
		} else {
			textFieldResult.setText("\"" + word + "\" is NOT a valid word.");
		}
	}

	/** Update Dictionary button: rebuilds the dictionary from the paragraph box. */
	private void updateDictionary() {
		String paragraph = textAreaParagraph.getText();
		if (paragraph.trim().isEmpty()) {
			textFieldResult.setText("Please enter a paragraph first.");
			return;
		}
		dict.loadParagraph(paragraph);
		if (dict.size() == 0) {
			textFieldResult.setText("No words found in that paragraph.");
		} else {
			textFieldResult.setText("Dictionary updated with " + dict.size() + " unique words.");
		}
	}

	/** Choose File button: loads a text file into the paragraph box (does not update the dictionary). */
	private void loadFileIntoParagraph() {
		String text = requestInputFile();
		if (text != null) {
			textAreaParagraph.setText(text);
			textAreaParagraph.setCaretPosition(0);
			textFieldResult.setText("File loaded. Click Update Dictionary to use it.");
		}
	}

	// ------------------------------------------------------------------
	// File helpers
	// ------------------------------------------------------------------

	/**
	 * Lets the user pick a text file and returns its contents, or null if the user
	 * cancelled or the file could not be read.
	 */
	private String requestInputFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open Text File");
		chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));

		if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
			return null; // user cancelled
		}

		File file = chooser.getSelectedFile();
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(frame,
					"Could not read " + file.getName() + ":\n" + ex.getMessage(),
					"Read Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/** Lets the user choose a destination and saves the dictionary's words (one per line, sorted). */
	private void saveToFile() {
		if (dict.size() == 0) {
			textFieldResult.setText("Nothing to save. Update the dictionary first.");
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save Dictionary");
		chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
		chooser.setSelectedFile(new File("dictionary.txt"));

		if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
			textFieldResult.setText("Save cancelled.");
			return;
		}

		File file = chooser.getSelectedFile();
		// JFileChooser doesn't add the extension for you
		if (!file.getName().toLowerCase().endsWith(".txt")) {
			file = new File(file.getParentFile(), file.getName() + ".txt");
		}

		if (file.exists()) {
			int choice = JOptionPane.showConfirmDialog(frame,
					file.getName() + " already exists. Overwrite it?",
					"Confirm Overwrite", JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION) {
				textFieldResult.setText("Save cancelled.");
				return;
			}
		}

		try {
			Files.write(file.toPath(), dict.getWords(), StandardCharsets.UTF_8);
			textFieldResult.setText("Saved " + dict.size() + " words to " + file.getName());
		} catch (IOException ex) {
			textFieldResult.setText("Could not save file: " + ex.getMessage());
		}
	}

}