import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInputField;
    private JButton addTaskButton;
    private JButton removeTaskButton;

    public MainFrame() {
        setTitle("Personal Productivity Dashboard");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        setupLayout();
        setupEvents();
    }

    private void initComponents() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(43, 48, 59));
        sidebarPanel.setPreferredSize(new Dimension(200, 550));
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        taskListModel = new DefaultListModel<>();
        taskListModel.addElement("Welcome to your dashboard!");
        taskListModel.addElement("Click a task and press Remove to clear it.");
        
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        taskInputField = new JTextField(20);
        taskInputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        addTaskButton = new JButton("Add Task");
        removeTaskButton = new JButton("Remove Selected");
    }

    private void setupLayout() {
        sidebarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JLabel titleLabel = new JLabel("WORKSPACE");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarPanel.add(titleLabel);

        String[] menuItems = {" Dashboard", " Tasks", " Analytics", " Settings"};
        for (String item : menuItems) {
            JButton menuBtn = new JButton(item);
            menuBtn.setPreferredSize(new Dimension(180, 35));
            menuBtn.setFocusPainted(false);
            sidebarPanel.add(menuBtn);
        }

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(245, 246, 248));
        JLabel headerText = new JLabel(" Quick Overview / Task Tracker");
        headerText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(headerText);
        
        contentPanel.add(topBar, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(new Color(245, 246, 248));
        inputPanel.add(new JLabel("New Task:"));
        inputPanel.add(taskInputField);
        inputPanel.add(addTaskButton);
        inputPanel.add(removeTaskButton);
        
        contentPanel.add(inputPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEvents() {
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskText = taskInputField.getText().trim();
                if (!taskText.isEmpty()) {
                    taskListModel.addElement(taskText);
                    taskInputField.setText("");
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        "Task cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        removeTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    taskListModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        "Please select a task to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }
}