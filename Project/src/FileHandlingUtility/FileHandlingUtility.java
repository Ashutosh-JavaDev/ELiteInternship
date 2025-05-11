package FileHandlingUtility;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

public class FileHandlingUtility extends JFrame {
    private JTextArea editor;
    private JTabbedPane fileTabs;
    private JLabel statusBar;
    private JPanel fileInfoPanel;
    private Map<String, File> openFiles;
    private File currentFile;
    
    public FileHandlingUtility() {
        super("File Handling Utility");
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupListeners();
        
        openFiles = new HashMap<>();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setMargin(new Insets(10, 10, 10, 10));
        
        fileTabs = new JTabbedPane();
        statusBar = new JLabel(" Ready");
        fileInfoPanel = new JPanel(new BorderLayout());
        
        // Set up file info panel
        fileInfoPanel.setBorder(BorderFactory.createTitledBorder("File Information"));
        fileInfoPanel.setPreferredSize(new Dimension(250, 0));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main split pane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            fileInfoPanel,
            new JScrollPane(editor)
        );
        splitPane.setResizeWeight(0.2);
        
        // Add components to frame
        add(fileTabs, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());
        
        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveFile());
        
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem findItem = new JMenuItem("Find", KeyEvent.VK_F);
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findItem.addActionListener(e -> showFindDialog());
        
        JMenuItem replaceItem = new JMenuItem("Replace", KeyEvent.VK_R);
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        replaceItem.addActionListener(e -> showReplaceDialog());
        
        editMenu.add(findItem);
        editMenu.add(replaceItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }
    
    private void setupListeners() {
        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStatus(); }
            public void removeUpdate(DocumentEvent e) { updateStatus(); }
            public void changedUpdate(DocumentEvent e) { updateStatus(); }
        });
        
        fileTabs.addChangeListener(e -> {
            int selectedIndex = fileTabs.getSelectedIndex();
            if (selectedIndex != -1) {
                String title = fileTabs.getTitleAt(selectedIndex);
                currentFile = openFiles.get(title);
                updateFileInfo();
            }
        });
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                editor.setText(content);
                currentFile = file;
                openFiles.put(file.getName(), file);
                
                // Add new tab
                if (fileTabs.indexOfTab(file.getName()) == -1) {
                    fileTabs.addTab(file.getName(), null);
                }
                fileTabs.setSelectedIndex(fileTabs.indexOfTab(file.getName()));
                
                updateFileInfo();
                statusBar.setText(" File opened: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error reading file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return;
            }
        }
        
        try {
            Files.write(currentFile.toPath(), editor.getText().getBytes());
            statusBar.setText(" File saved: " + currentFile.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving file: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showFindDialog() {
        JDialog dialog = new JDialog(this, "Find", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField searchField = new JTextField(20);
        JButton findButton = new JButton("Find Next");
        
        panel.add(new JLabel("Find what:"), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(findButton, BorderLayout.EAST);
        
        dialog.add(panel, BorderLayout.CENTER);
        
        findButton.addActionListener(e -> {
            String searchText = searchField.getText();
            String content = editor.getText();
            int caretPosition = editor.getCaretPosition();
            
            Pattern pattern = Pattern.compile(Pattern.quote(searchText));
            Matcher matcher = pattern.matcher(content);
            
            if (matcher.find(caretPosition)) {
                editor.setCaretPosition(matcher.start());
                editor.select(matcher.start(), matcher.end());
                editor.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "No more occurrences found.",
                    "Find",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showReplaceDialog() {
        JDialog dialog = new JDialog(this, "Replace", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        JTextField findField = new JTextField(20);
        JTextField replaceField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Find what:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(findField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Replace with:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(replaceField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");
        
        buttonPanel.add(replaceButton);
        buttonPanel.add(replaceAllButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        replaceButton.addActionListener(e -> {
            String findText = findField.getText();
            String replaceText = replaceField.getText();
            String content = editor.getText();
            
            Pattern pattern = Pattern.compile(Pattern.quote(findText));
            Matcher matcher = pattern.matcher(content);
            
            if (matcher.find(editor.getCaretPosition())) {
                editor.replaceRange(replaceText, matcher.start(), matcher.end());
                editor.setCaretPosition(matcher.start() + replaceText.length());
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "No more occurrences found.",
                    "Replace",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        replaceAllButton.addActionListener(e -> {
            String findText = findField.getText();
            String replaceText = replaceField.getText();
            String content = editor.getText();
            
            String newContent = content.replaceAll(Pattern.quote(findText), replaceText);
            editor.setText(newContent);
            
            dialog.dispose();
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void updateFileInfo() {
        if (currentFile != null) {
            fileInfoPanel.removeAll();
            
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            
            info.add(createInfoLabel("Name:", currentFile.getName()));
            info.add(Box.createVerticalStrut(5));
            info.add(createInfoLabel("Size:", formatFileSize(currentFile.length())));
            info.add(Box.createVerticalStrut(5));
            info.add(createInfoLabel("Last Modified:", 
                sdf.format(new Date(currentFile.lastModified()))));
            
            fileInfoPanel.add(info, BorderLayout.NORTH);
            fileInfoPanel.revalidate();
            fileInfoPanel.repaint();
        }
    }
    
    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.PLAIN));
        valueLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private void updateStatus() {
        int lines = editor.getLineCount();
        int chars = editor.getText().length();
        statusBar.setText(String.format(" Lines: %d | Characters: %d", lines, chars));
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            FileHandlingUtility app = new FileHandlingUtility();
            app.setVisible(true);
        });
    }
}