import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingManagementSystem extends Application {
    private Stage stage;
    private Scene loginScene;
    private Scene homeScene;
    private TextField usernameTextField;
    private PasswordField passwordField;
    private Label balanceLabel;
    private ListView<String> transactionListView;
    private CheckBox checkbox;

    // Simulated user data
    private final String username = "user123";
    private final String password = "password";
    private double balance = 0.0;
    private ObservableList<String> transactionHistory;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Transaction history
        transactionHistory = FXCollections.observableArrayList();

        // Login Scene
        Label welcomeLabel = new Label("Welcome to XYZ Bank");
        welcomeLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        usernameTextField = new TextField();
        passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(20));
        loginLayout.setVgap(10);
        loginLayout.setHgap(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        loginLayout.add(welcomeLabel, 0, 0, 2, 1);
        loginLayout.add(usernameLabel, 0, 1);
        loginLayout.add(usernameTextField, 1, 1);
        loginLayout.add(passwordLabel, 0, 2);
        loginLayout.add(passwordField, 1, 2);
        loginLayout.add(loginButton, 1, 3);
        loginScene = new Scene(loginLayout, 300, 200);

        // Home Scene
        Button createAccountButton = new Button("Create Account");
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button checkBalanceButton = new Button("Check Balance");
        Button viewHistoryButton = new Button("View Transaction History");
        Button logoutButton = new Button("Logout");

        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(20));
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        homeLayout.getChildren().addAll(createAccountButton, depositButton, withdrawButton, checkBalanceButton,
                viewHistoryButton, logoutButton);
        balanceLabel = new Label("Balance: $0.00");
        homeLayout.getChildren().add(balanceLabel);

        transactionListView = new ListView<>();
        transactionListView.setPrefHeight(150);
        checkbox = new CheckBox("Enable Dark Mode");
        homeLayout.getChildren().addAll(transactionListView, checkbox);

        homeScene = new Scene(homeLayout, 300, 400);

        // Button Actions
        loginButton.setOnAction(e -> login());
        createAccountButton.setOnAction(e -> createAccount());
        depositButton.setOnAction(e -> deposit());
        withdrawButton.setOnAction(e -> withdraw());
        checkBalanceButton.setOnAction(e -> checkBalance());
        viewHistoryButton.setOnAction(e -> viewTransactionHistory());
        logoutButton.setOnAction(e -> logout());

        // Dark Mode Checkbox
        checkbox.setOnAction(e -> {
            if (checkbox.isSelected()) {
                homeLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
            } else {
                homeLayout.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            }
        });

        // Initial Scene
        stage.setScene(loginScene);
        stage.setTitle("Banking Management System");
        stage.show();
    }

    private void login() {
        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordField.getText();

        if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
            stage.setScene(homeScene);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password!");
            alert.showAndWait();
        }
    }

    private void createAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Account Creation");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter initial balance:");
        dialog.showAndWait().ifPresent(balanceInput -> {
            double initialBalance = Double.parseDouble(balanceInput);
            balance = initialBalance;
            updateBalanceLabel();
            addTransactionToHistory("Account Created: Initial Balance $" + initialBalance);
        });
    }

    private void deposit() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter deposit amount:");
        dialog.showAndWait().ifPresent(amount -> {
            double depositAmount = Double.parseDouble(amount);
            balance += depositAmount;
            updateBalanceLabel();
            addTransactionToHistory("Deposit: $" + depositAmount);
        });
    }

    private void withdraw() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Withdraw");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField toWhomTextField = new TextField();
        TextField amountTextField = new TextField();

        grid.add(new Label("Enter user name:"), 0, 0);
        grid.add(toWhomTextField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountTextField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return toWhomTextField.getText() + " - " + amountTextField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(withdrawal -> {
            String[] parts = withdrawal.split(" - ");
            String toWhom = parts[0];
            double withdrawalAmount = Double.parseDouble(parts[1]);

            if (withdrawalAmount <= balance) {
                balance -= withdrawalAmount;
                updateBalanceLabel();
                addTransactionToHistory("Withdrawal to " + toWhom + ": $" + withdrawalAmount);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Insufficient Balance");
                alert.setHeaderText(null);
                alert.setContentText("Withdrawal amount exceeds the available balance!");
                alert.showAndWait();
            }
        });
    }

    private void checkBalance() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Balance");
        alert.setHeaderText(null);
        alert.setContentText("Current Balance: $" + String.format("%.2f", balance));
        alert.showAndWait();
    }

    private void viewTransactionHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction History");
        alert.setHeaderText(null);
        alert.setContentText(transactionHistory.isEmpty() ? "No transactions found." :
                String.join("\n", transactionHistory));
        alert.showAndWait();
    }

    private void logout() {
        stage.setScene(loginScene);
        balance = 0.0;
        usernameTextField.clear();
        passwordField.clear();
        updateBalanceLabel();
        transactionHistory.clear();
    }

    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", balance));
    }

    private void addTransactionToHistory(String transaction) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);
        transactionHistory.add(formattedTimestamp + " - " + transaction);
    }
}


