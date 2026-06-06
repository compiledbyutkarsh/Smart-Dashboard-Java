import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private DefaultListModel<String> taskListModel;
    private JLabel clockLabel;
    private JProgressBar efficiencyBar;
    private JLabel pendingCountLabel;
    private final String FILE_PATH = "tasks.txt";

    public MainFrame() {
        setTitle("Personal Productivity Suite v3.0 - Ultimate Edition");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        
        taskListModel = new DefaultListModel<>();
        loadTasksFromFile();

        
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(43, 47, 54));
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("WORKSPACE Pro");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        sidebar.add(titleLabel);

        sidebar.add(createNavButton("Dashboard", "DashboardPanel"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createNavButton("Tasks Tracker", "TasksPanel"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createNavButton("Analytics", "AnalyticsPanel"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(createNavButton("Settings", "SettingsPanel"));

        add(sidebar, BorderLayout.WEST);

        
        mainContentPanel.add(createDashboardPanel(), "DashboardPanel");
        mainContentPanel.add(createTasksPanel(), "TasksPanel");
        mainContentPanel.add(createAnalyticsPanel(), "AnalyticsPanel");
        mainContentPanel.add(createSettingsPanel(), "SettingsPanel");

        add(mainContentPanel, BorderLayout.CENTER);

        
        startClockEngine();
        updateAnalyticsMetrics();
    }

    private JButton createNavButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, panelName));
        return btn;
    }

    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel welcomeLabel = new JLabel("<html><center><h1 style='font-family:Arial; margin:0;'>Welcome to Your Productivity Suite</h1></center></html>", JLabel.CENTER);
        panel.add(welcomeLabel, gbc);

        JLabel subLabel = new JLabel("<html><center><p style='font-family:Arial; font-size:12px; color:gray; margin:0;'>Track your progress, build your brand, stay focused.</p></center></html>", JLabel.CENTER);
        panel.add(subLabel, gbc);

        panel.add(Box.createVerticalStrut(15), gbc);

        clockLabel = new JLabel("", JLabel.CENTER);
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        clockLabel.setForeground(new Color(25, 118, 210));
        panel.add(clockLabel, gbc);

        return panel;
    }

    
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Quick Overview / Task Tracker", JLabel.LEFT);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(header, BorderLayout.NORTH);

        JList<String> taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        
        JLabel inputLabel = new JLabel("New Task:");
        inputLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        JTextField taskField = new JTextField(25);
        
        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Selected");

        addButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                taskListModel.addElement(task);
                taskField.setText("");
                saveTasksToFile();
                updateAnalyticsMetrics();
            }
        });

        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.remove(selectedIndex);
                saveTasksToFile();
                updateAnalyticsMetrics();
            }
        });

        inputPanel.add(inputLabel);
        inputPanel.add(taskField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel mainTitle = new JLabel("📊 Real-time Workspace Analytics", JLabel.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(mainTitle, gbc);

        pendingCountLabel = new JLabel("Active Tasks Currently Pending: 0", JLabel.CENTER);
        pendingCountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pendingCountLabel.setForeground(Color.RED);
        panel.add(pendingCountLabel, gbc);

        JLabel infoLabel = new JLabel("Fewer pending tasks ensures high workspace optimization scores.", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);

        efficiencyBar = new JProgressBar(0, 100);
        efficiencyBar.setPreferredSize(new Dimension(300, 25));
        efficiencyBar.setStringPainted(true);
        panel.add(efficiencyBar, gbc);

        return panel;
    }

    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Choose Workspace Theme:");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        JButton darkBtn = new JButton("Classic Dark Sidebar");
        JButton blueBtn = new JButton("Ocean Blue Sidebar");

        panel.add(darkBtn);
        panel.add(blueBtn);

        return panel;
    }

    
    private void startClockEngine() {
        Thread clockThread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm:ss a");
            while (true) {
                try {
                    String currentTicks = sdf.format(new Date());
                    SwingUtilities.invokeLater(() -> clockLabel.setText(currentTicks));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();
    }

    
    private void updateAnalyticsMetrics() {
        int activeTasks = taskListModel.size();
        if (pendingCountLabel != null && efficiencyBar != null) {
            pendingCountLabel.setText("Active Tasks Currently Pending: " + activeTasks);
            
            int efficiencyScore = Math.max(0, 100 - (activeTasks * 10));
            efficiencyBar.setValue(efficiencyScore);
            efficiencyBar.setString("Workspace Efficiency: " + efficiencyScore + "%");
        }
    }

    private void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                writer.println(taskListModel.get(i));
            }
        } catch (IOException e) {
            System.out.println("Storage backup writing pipeline failure.");
        }
    }

    private void loadTasksFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                taskListModel.addElement(line);
            }
        } catch (IOException e) {
            System.out.println("Storage recovery pipeline failure.");
        }
    }
}
