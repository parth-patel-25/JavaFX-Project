package application;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;



public class Main extends Application {

    private static final String USERS_FILE = "users.csv";
    private static final String STUDENTS_FILE = "students.csv";
    
	private static final String STUDENTS_DATA = "students.csv";

	private ObservableList<Student> studentsList;
	private ListView<Student> studentsListView;
	
    private static class Student {
        private String name;
        private int age;
        private double grade;

        public Student(String name, int age, double grade) {
            this.name = name;
            this.age = age;
            this.grade = grade;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public double getGrade() {
            return grade;
        }

        public void setGrade(double grade) {
            this.grade = grade;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    private GridPane createHomeGridPane1() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // Create student list view
        studentsListView = new ListView<>(studentsList);
        studentsListView.setPrefSize(200, 300);
        GridPane.setConstraints(studentsListView, 0, 0, 1, 4);

        // Create student form controls
        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 1, 0);
        TextField nameTextField = new TextField();
        GridPane.setConstraints(nameTextField, 2, 0);

        Label ageLabel = new Label("Age:");
        GridPane.setConstraints(ageLabel, 1, 1);
        TextField ageTextField = new TextField();
        GridPane.setConstraints(ageTextField, 2, 1);

        Label gradeLabel = new Label("Grade:");
        GridPane.setConstraints(gradeLabel, 1, 2);
        TextField gradeTextField = new TextField();
        GridPane.setConstraints(gradeTextField, 2, 2);

        // Create action buttons
        Button addButton = new Button("Add");
        GridPane.setConstraints(addButton, 1, 3);
        addButton.setOnAction(e -> {
            String name = nameTextField.getText();
            int age = Integer.parseInt(ageTextField.getText());
            double grade = Double.parseDouble(gradeTextField.getText());

            Student student = new Student(name, age, grade);
            addStudent(student);

            nameTextField.clear();
            ageTextField.clear();
            gradeTextField.clear();
        });

        Button updateButton = new Button("Update");
        GridPane.setConstraints(updateButton, 2, 3);
        updateButton.setOnAction(e -> {
            Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                String newName = nameTextField.getText();
                int newAge = Integer.parseInt(ageTextField.getText());
                double newGrade = Double.parseDouble(gradeTextField.getText());

                selectedStudent.setName(newName);
                selectedStudent.setAge(newAge);
                selectedStudent.setGrade(newGrade);

                studentsListView.refresh();

                nameTextField.clear();
                ageTextField.clear();
                gradeTextField.clear();
            }
        });

        Button deleteButton = new Button("Delete");
        GridPane.setConstraints(deleteButton, 3, 3);
        deleteButton.setOnAction(e -> {
            Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                deleteStudent(selectedStudent);
                studentsListView.getSelectionModel().clearSelection();
                nameTextField.clear();
                ageTextField.clear();
                gradeTextField.clear();
            }
        });

        // Create student details display area
        HBox detailsBox = new HBox();
        detailsBox.setSpacing(10);
        GridPane.setConstraints(detailsBox, 3, 0, 1, 3);

        Label nameLabelDisplay = new Label("Name: ");
        Label ageLabelDisplay = new Label("Age: ");
        Label gradeLabelDisplay = new Label("Grade: ");
        detailsBox.getChildren().addAll(nameLabelDisplay, ageLabelDisplay, gradeLabelDisplay);

        // Update details display when a student is selected
        studentsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameLabelDisplay.setText("Name: " + newVal.getName());
                ageLabelDisplay.setText("Age: " + newVal.getAge());
                gradeLabelDisplay.setText("Grade: " + newVal.getGrade());
            }
        });

        gridPane.getChildren().addAll(studentsListView, nameLabel, nameTextField, ageLabel, ageTextField,
                gradeLabel, gradeTextField, addButton, updateButton, deleteButton, detailsBox);

        return gridPane;
    }
    
    private void loadStudentsData() {
        List<Student> students = readStudentsFromFile();
        studentsList.addAll(students);
    }

    private List<Student> readStudentsFromFile() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_DATA))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int age = Integer.parseInt(data[1]);
                double grade = Double.parseDouble(data[2]);

                Student student = new Student(name, age, grade);
                students.add(student);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void addStudent(Student student) {
        studentsList.add(student);
        writeStudentToFile(student);
    }

    private void writeStudentToFile(Student student) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_DATA, true))) {
            writer.write(student.getName() + "," + student.getAge() + "," + student.getGrade() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent(Student student) {
        studentsList.remove(student);
        deleteStudentFromFile(student);
    }

    private void deleteStudentFromFile(Student student) {
        try {
            File inputFile = new File(STUDENTS_DATA);
            File tempFile = new File("temp.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = student.getName() + "," + student.getAge() + "," + student.getGrade();
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.equals(lineToRemove)) {
                    writer.write(currentLine + "\n");
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Management App");

        // Create login page UI
        GridPane loginGridPane = createLoginGridPane();
        Scene loginScene = new Scene(loginGridPane, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private GridPane createLoginGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameTextField = new TextField();
        GridPane.setConstraints(usernameTextField, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        
        loginButton.setOnAction(e -> {
            boolean isValid = authenticateUser(usernameTextField.getText(), passwordField.getText());
            if (isValid) {
            	studentsList = FXCollections.observableArrayList();
                loadStudentsData();
                // Create home page UI
                GridPane homeGridPane = createHomeGridPane1();
                Scene homeScene = new Scene(homeGridPane, 600, 400);
                Stage homeStage = new Stage();
//                homeStage.setTitle("Home");
                homeStage.setScene(homeScene);
                homeStage.show();

                // Close login stage
                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                loginStage.close();

            	homeStage.setTitle("Student Management App");

                // Initialize student list
             

               
                
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }
        });

        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 0, 2);
        registerButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            registerUser(username, password);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully!");
            alert.showAndWait();
        });

        gridPane.getChildren().addAll(usernameLabel, usernameTextField, passwordLabel, passwordField, loginButton, registerButton);

        return gridPane;
    }

    private GridPane createHomeGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // TODO: Add CRUD functionality for student data
        // Example: Add labels, text fields, buttons, etc.

        return gridPane;
    }

    private boolean authenticateUser(String username, String password) {
        // TODO: Implement user authentication logic (e.g., check against CSV file)
        // Return true if authentication succeeds, false otherwise
    	
    	 try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
    	        String line;
    	        while ((line = reader.readLine()) != null) {
    	            String[] user = line.split(",");
    	            if (user.length >= 2) {
    	                String storedUsername = user[0];
    	                String storedPassword = user[1];

    	                if (storedUsername.equals(username) && storedPassword.equals(password)) {
    	                    return true; // Authentication succeeds
    	                }
    	            }
    	        }
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }

    	    return false; // Authentication fails
    	}
    
    private void registerUser(String username, String password) {
        try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
            writer.write(username + "," + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






//public class Main extends Application {
//    @Override
//    public void start(Stage primaryStage) {
//    	Circle circle = new Circle();
//    	
//    	StackPane root = new StackPane();
//    	root.getChildren().add(circle);
//    	
//    	circle.setCenterX(100);
//    	circle.setCenterY(100);
//    	circle.setRadius(50);
//    	circle.setStroke(Color.BLACK);
//    	
//    	Scene scene = new Scene(root, 300, 200);
//    	
//    	
//    	primaryStage.setScene(root);
//    	primaryStage.setTitle("Circle Example");
//        primaryStage.show();   	
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}