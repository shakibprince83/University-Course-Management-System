import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentManagementSystem extends JFrame {
    private ArrayList<Student> students;
    private JTextArea tA;

    private Map<Integer, ArrayList<String>> studentCourses;

    public StudentManagementSystem() {
        students = new ArrayList<>();
        studentCourses = new HashMap<>();
        loadStudentsFromFile();
        loadCoursesFromFile();

        setTitle("Student Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tA = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(tA);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewStudents();
            }
        });

        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCourse();
            }
        });

        JButton viewCourseButton = new JButton("View Courses");
        viewCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCourses();
            }
        });

        JButton withdrawCourseButton = new JButton("Withdraw Course");
        withdrawCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                withdrawCourse();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(addCourseButton);
        buttonPanel.add(viewCourseButton);
        buttonPanel.add(withdrawCourseButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStudentsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int idIndex = parts[0].indexOf(':');
                        if (idIndex != -1) {
                            int id = Integer.parseInt(parts[0].substring(idIndex + 1).trim());
                            String name = parts[1].trim();
                            String department = parts[2].trim();
                            double cgpa = Double.parseDouble(parts[3].substring(parts[3].indexOf(':') + 1).trim());
                            students.add(new Student(id, name, department, cgpa));
                            studentCourses.put(id, new ArrayList<>());
                        } else {
                            System.out.println("Invalid ID format in file: " + line);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format in file: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    private void loadCoursesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("courses.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        ArrayList<String> courses = studentCourses.getOrDefault(id, new ArrayList<>());
                        courses.add(parts[1].trim());
                        studentCourses.put(id, courses);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format in course file: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in course file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void saveStudentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("students.txt"))) {
            for (Student student : students) {
                writer.println("ID: " + student.getId() + ", Name: " + student.getName() + ", Department: " + student.getDepartment() + ", CGPA: " + student.getCgpa());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveCoursesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("courses.txt"))) {
            for (Map.Entry<Integer, ArrayList<String>> entry : studentCourses.entrySet()) {
                int id = entry.getKey();
                ArrayList<String> courses = entry.getValue();
                for (String course : courses) {
                    writer.println(id + ", " + course);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addStudent() {
        String idInput = JOptionPane.showInputDialog("Enter student ID:");
        int id = Integer.parseInt(idInput);
        String name = JOptionPane.showInputDialog("Enter student name:");
        String department = JOptionPane.showInputDialog("Enter student department:");
        String cgpaInput = JOptionPane.showInputDialog("Enter student CGPA:");
        double cgpa = Double.parseDouble(cgpaInput);
        students.add(new Student(id, name, department, cgpa));
        studentCourses.put(id, new ArrayList<>());
        saveStudentsToFile();
    }


    private void deleteStudent() {
        String input = JOptionPane.showInputDialog("Enter student ID to delete:");
        int id = Integer.parseInt(input);
        for (Student student : students) {
            if (student.getId() == id) {
                students.remove(student);
                studentCourses.remove(id);
                break;
            }
        }
        saveStudentsToFile();
        saveCoursesToFile();
    }

    private void updateStudent() {
        String input = JOptionPane.showInputDialog("Enter student ID to update:");
        int id = Integer.parseInt(input);
        for (Student student : students) {
            if (student.getId() == id) {
                String name = JOptionPane.showInputDialog("Enter new student name:");
                String department = JOptionPane.showInputDialog("Enter new student department:");
                double cgpa = Double.parseDouble(JOptionPane.showInputDialog("Enter new CGPA:"));
                student.setName(name);
                student.setDepartment(department);
                student.setCgpa(cgpa);
                break;
            }
        }
        saveStudentsToFile();
    }


    private void viewStudents() {
        String idInput = JOptionPane.showInputDialog("Enter student ID to view:");
        int id = Integer.parseInt(idInput);
        boolean found = false;
        for (Student student : students) {
            if (student.getId() == id) {
                tA.setText(student.toString());
                found = true;
                break;
            }
        }
        if (!found) {
            tA.setText("Student not found with ID: " + id);
        }
    }


    private void addCourse() {
        String idInput = JOptionPane.showInputDialog("Enter student ID to add course:");
        int id = Integer.parseInt(idInput);
        ArrayList<String> courses = studentCourses.get(id);
        if (courses == null) {
            JOptionPane.showMessageDialog(null, "Student ID not found");
            return;
        }
        if (courses.size() >= 6) {
            JOptionPane.showMessageDialog(null, "Maximum number of courses reached for the student ID");
            return;
        }
        String course = JOptionPane.showInputDialog("Enter course name:");
        courses.add(course);
        studentCourses.put(id, courses);
        saveCoursesToFile();
    }

    private void viewCourses() {
        String idInput = JOptionPane.showInputDialog("Enter student ID to view courses:");
        int id = Integer.parseInt(idInput);
        ArrayList<String> courses = studentCourses.get(id);
        if (courses == null) {
            tA.setText("Student ID not found");
            return;
        }
        StringBuilder courseList = new StringBuilder();
        courseList.append("Courses for Student ID ").append(id).append(":\n");
        for (String course : courses) {
            courseList.append(course).append("\n");
        }
        tA.setText(courseList.toString());
    }

    private void withdrawCourse() {
        String idInput = JOptionPane.showInputDialog("Enter student ID to withdraw course:");
        int id = Integer.parseInt(idInput);
        ArrayList<String> courses = studentCourses.get(id);
        if (courses == null) {
            JOptionPane.showMessageDialog(null, "Student ID not found");
            return;
        }
        String courseToWithdraw = JOptionPane.showInputDialog("Enter course name to withdraw:");
        if (courses.remove(courseToWithdraw)) {
            JOptionPane.showMessageDialog(null, "Course withdrawn successfully");
            saveCoursesToFile();
        } else {
            JOptionPane.showMessageDialog(null, "Course not found for the specified student ID");
        }
    }

    public static void main(String[] args) {
        StudentManagementSystem system = new StudentManagementSystem();
        system.setVisible(true);

    }
}
