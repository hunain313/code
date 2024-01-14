package examseatingapplication;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.table.DefaultTableModel;
public class ExamSeatingApplication {
    private JFrame frame;
    private JTextField rollNumberField;
    private JTextField nameField;
    private JTextField phoneNumberField;
    private DefaultListModel<String> rollNumberListModel;
    private JList<String> rollNumberList;
    private List<MutableStudent> studentList;
    private JButton showRoomListButton;
    private JButton editButton;
    private final Object lock = new Object(); // Object for synchronization
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public ExamSeatingApplication() {
        studentList = new ArrayList<>();
        initializeFirstPage();
    }

    private void initializeFirstPage() {
        frame = new JFrame();
        frame.setTitle("Exam Seating Application");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 240));
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        placeComponents(panel);

        rollNumberListModel = new DefaultListModel<>();
        rollNumberList = new JList<>(rollNumberListModel);

        JScrollPane scrollPane = new JScrollPane(rollNumberList);
        panel.add(createListHeading("Student Roll Numbers", new Color(128, 0, 128)), BorderLayout.WEST);
        panel.add(scrollPane, BorderLayout.CENTER);

        showRoomListButton = new JButton("Show Rooms List");
        showRoomListButton.setBackground(new Color(128, 128, 0));
        showRoomListButton.setForeground(Color.white);
        showRoomListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRoomList();
            }
        });

        panel.add(showRoomListButton, BorderLayout.SOUTH);

        editButton = new JButton("Edit Info");
        editButton.setBackground(new Color(255, 165, 0));
        editButton.setForeground(Color.white);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editStudentInfo();
            }
        });
        panel.add(editButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void showRoomList() {
        String[] columnNames = {"Room Number", "Roll Numbers"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (int i = 101; i <= 104; i++) {
            StringBuilder rollNumbers = new StringBuilder();

            for (MutableStudent student : studentList) {
                if (student.getRoomNumber().endsWith(Integer.toString(i))) {
                    rollNumbers.append(student.getRollNumber()).append(", ");
                }
            }

            if (rollNumbers.length() > 0) {
                rollNumbers.delete(rollNumbers.length() - 2, rollNumbers.length());
                Object[] rowData = {"Room " + i, rollNumbers.toString()};
                tableModel.addRow(rowData);
            }
        }

        JTable roomListTable = new JTable(tableModel);
        roomListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        roomListTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        JScrollPane tableScrollPane = new JScrollPane(roomListTable);

        JOptionPane.showMessageDialog(frame, tableScrollPane, "Rooms List", JOptionPane.INFORMATION_MESSAGE);

        synchronized (lock1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lock2) {
                System.out.println("Acquired lock1 and lock2");
            }
        }
    }

    private void editStudentInfo() {
        String selectedRollNumber = rollNumberList.getSelectedValue();

        if (selectedRollNumber != null) {
            MutableStudent selectedStudent = findStudent(selectedRollNumber);

            if (selectedStudent != null) {
                nameField = new JTextField(selectedStudent.getName());
                phoneNumberField = new JTextField(selectedStudent.getPhoneNumber());
                rollNumberField = new JTextField(selectedStudent.getRollNumber());

                JPanel editPanel = new JPanel(new GridLayout(4, 2));
                editPanel.add(new JLabel("Roll Number:"));
                editPanel.add(rollNumberField);
                editPanel.add(new JLabel("Name:"));
                editPanel.add(nameField);
                editPanel.add(new JLabel("Phone Number:"));
                editPanel.add(phoneNumberField);

                int result = JOptionPane.showConfirmDialog(frame, editPanel, "Edit Student Information", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    synchronized (lock) {
                        selectedStudent.setRollNumber(rollNumberField.getText());
                        selectedStudent.setName(nameField.getText());
                        selectedStudent.setPhoneNumber(phoneNumberField.getText());
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a student to edit.");
        }

        synchronized (lock2) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lock1) {
                System.out.println("Acquired lock2 and lock1");
            }
        }
    }

    private JLabel createListHeading(String text, Color textColor) {
        JLabel headingLabel = new JLabel(text);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headingLabel.setHorizontalAlignment(JLabel.CENTER);
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headingLabel.setForeground(textColor);
        return headingLabel;
    }

    private void placeComponents(JPanel panel) {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 8));
        inputPanel.setBackground(new Color(240, 255, 240));

        JLabel rollNumberLabel = new JLabel("Enter Roll Number:");
        inputPanel.add(rollNumberLabel);

        rollNumberField = new JTextField();
        inputPanel.add(rollNumberField);

        JLabel nameLabel = new JLabel("Enter Name:");
        inputPanel.add(nameLabel);

        nameField = new JTextField();
        inputPanel.add(nameField);

        JLabel phoneNumberLabel = new JLabel("Enter Phone Number:");
        inputPanel.add(phoneNumberLabel);

        phoneNumberField = new JTextField();
        inputPanel.add(phoneNumberField);

        JButton addButton = new JButton("Add Student");
        addButton.setBackground(new Color(0, 128, 0));
        addButton.setForeground(Color.white);
        inputPanel.add(addButton);

        JButton showInfoButton = new JButton("Show Info");
        showInfoButton.setBackground(new Color(0, 0, 128));
        showInfoButton.setForeground(Color.white);
        inputPanel.add(showInfoButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredRollNumber = rollNumberField.getText();
                String enteredName = nameField.getText();
                String enteredPhoneNumber = phoneNumberField.getText();

                if (!enteredRollNumber.isEmpty() && !enteredName.isEmpty() && !enteredPhoneNumber.isEmpty()) {
                    addStudent(enteredRollNumber, enteredName, enteredPhoneNumber);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter valid information.");
                }
            }
        });

        showInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudentInfo();
            }
        });
    }

    private void addStudent(String rollNumber, String name, String phoneNumber) {
        rollNumberListModel.addElement(rollNumber);

        List<String> rollNumbers = Collections.list(rollNumberListModel.elements());
        Collections.sort(rollNumbers, Collections.reverseOrder());

        rollNumberListModel.clear();
        for (String sortedRollNumber : rollNumbers) {
            rollNumberListModel.addElement(sortedRollNumber);
        }

        Thread addStudentThread = new Thread(() -> {
            synchronized (lock) {
                studentList.add(new MutableStudent(rollNumber, name, phoneNumber));
            }
        });

        addStudentThread.start();
    }

    private void clearFields() {
        rollNumberField.setText("");
        nameField.setText("");
        phoneNumberField.setText("");
    }

    private void showStudentInfo() {
        SwingWorker<ImmutableStudent, Void> worker = new SwingWorker<ImmutableStudent, Void>() {
            @Override
            protected ImmutableStudent doInBackground() throws Exception {
                String selectedRollNumber = rollNumberList.getSelectedValue();
                if (selectedRollNumber != null) {
                    MutableStudent mutableStudent = findStudent(selectedRollNumber);
                    if (mutableStudent != null) {
                        return new ImmutableStudent(mutableStudent.getRollNumber(), mutableStudent.getName(),
                                mutableStudent.getPhoneNumber(), mutableStudent.getRoomNumber());
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    Thread.sleep(1000);

                    ImmutableStudent selectedStudent = get();
                    if (selectedStudent != null) {
                        showInfoDialog(selectedStudent);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Student not found.");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private MutableStudent findStudent(String rollNumber) {
        synchronized (lock) {
            for (MutableStudent student : studentList) {
                if (student.getRollNumber().equals(rollNumber)) {
                    return student;
                }
            }
        }
        return null;
    }

    private void showInfoDialog(ImmutableStudent student) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));
        infoPanel.setBackground(new Color(255, 239, 186));

        JLabel nameLabel = new JLabel("Name: " + student.getName());
        infoPanel.add(nameLabel);

        JLabel rollNumberLabel = new JLabel("Roll Number: " + student.getRollNumber());
        infoPanel.add(rollNumberLabel);

        JLabel phoneNumberLabel = new JLabel("Phone Number: " + student.getPhoneNumber());
        infoPanel.add(phoneNumberLabel);

        JLabel roomNumberLabel = new JLabel("Room Number: " + student.getRoomNumber());
        infoPanel.add(roomNumberLabel);

        int result = JOptionPane.showConfirmDialog(frame, infoPanel, "Student Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Handle OK button action if needed
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExamSeatingApplication();
        });
    }

    private static class MutableStudent {
        private String rollNumber;
        private String name;
        private String phoneNumber;
        private String roomNumber;

        public MutableStudent(String rollNumber, String name, String phoneNumber) {
            this.rollNumber = rollNumber;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.roomNumber = "Room " + (Integer.parseInt(rollNumber) % 4 + 101);
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    private static class ImmutableStudent {
        private final String rollNumber;
        private final String name;
        private final String phoneNumber;
        private final String roomNumber;

        public ImmutableStudent(String rollNumber, String name, String phoneNumber, String roomNumber) {
            this.rollNumber = rollNumber;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.roomNumber = roomNumber;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getRoomNumber() {
            return roomNumber;
        }
    }
}
