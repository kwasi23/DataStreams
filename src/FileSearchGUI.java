import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileSearchGUI extends JFrame {
    private JTextArea leftTextArea;
    private JTextArea rightTextArea;
    private JTextField searchTextField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path loadedFilePath; // Store the loaded file path

    public FileSearchGUI() {
        setTitle("File Search GUI");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        leftTextArea = new JTextArea();
        leftTextArea.setEditable(false);
        rightTextArea = new JTextArea();
        rightTextArea.setEditable(false);

        JScrollPane leftScrollPane = new JScrollPane(leftTextArea);
        JScrollPane rightScrollPane = new JScrollPane(rightTextArea);

        searchTextField = new JTextField(20);
        loadButton = new JButton("Load Text File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        JPanel controlPanel = new JPanel();
        controlPanel.add(loadButton);
        controlPanel.add(searchTextField);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        searchButton.setEnabled(false); // Initially disabled until a file is loaded

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightScrollPane);
        splitPane.setDividerLocation(400);

        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.NORTH);
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            loadedFilePath = fileChooser.getSelectedFile().toPath();
            try {
                String content = new String(Files.readAllBytes(loadedFilePath));
                leftTextArea.setText(content);
                rightTextArea.setText(""); // Clear previous search
                searchButton.setEnabled(true); // Enable search now that a file is loaded
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFile() {
        String searchText = searchTextField.getText();
        if (loadedFilePath != null && searchText != null && !searchText.trim().isEmpty()) {
            try (Stream<String> stream = Files.lines(loadedFilePath)) {
                String searchResults = stream
                        .filter(line -> line.contains(searchText))
                        .reduce((s1, s2) -> s1 + "\n" + s2)
                        .orElse("No matches found.");
                rightTextArea.setText(searchResults);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error searching file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please load a file and enter a search string.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileSearchGUI().setVisible(true));
    }
}
