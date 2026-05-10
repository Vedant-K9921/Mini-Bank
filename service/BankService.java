package service;
import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class BankService {

    public static int getAccountId(int userId) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT account_id FROM accounts WHERE user_id=?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);

        return -1;
    }

    public static void deposit(int userId, Scanner sc) {
        try {
            System.out.print("Enter amount: ");
            double amount = sc.nextDouble();

            if (amount <= 0) {
                System.out.println("Invalid amount!");
                return;
            }

            int accId = getAccountId(userId);
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE account_id=?"
            );
            ps.setDouble(1, amount);
            ps.setInt(2, accId);
            ps.executeUpdate();

            // Record transaction
            PreparedStatement txn = con.prepareStatement(
                "INSERT INTO transactions(account_id, type, amount) VALUES (?, 'DEPOSIT', ?)"
            );
            txn.setInt(1, accId);
            txn.setDouble(2, amount);
            txn.executeUpdate();

            System.out.println("Deposit successful!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void withdraw(int userId, Scanner sc) {
        try {
            System.out.print("Enter amount: ");
            double amount = sc.nextDouble();

            int accId = getAccountId(userId);
            Connection con = DBConnection.getConnection();

            PreparedStatement check = con.prepareStatement(
                "SELECT balance FROM accounts WHERE account_id=?"
            );
            check.setInt(1, accId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getDouble("balance") >= amount) {

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE account_id=?"
                );
                ps.setDouble(1, amount);
                ps.setInt(2, accId);
                ps.executeUpdate();

                PreparedStatement txn = con.prepareStatement(
                    "INSERT INTO transactions(account_id, type, amount) VALUES (?, 'WITHDRAW', ?)"
                );
                txn.setInt(1, accId);
                txn.setDouble(2, amount);
                txn.executeUpdate();

                System.out.println("Withdrawal successful!");

            } else {
                System.out.println("Insufficient balance!");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void viewTransactions(int userId) {
        try {
            int accId = getAccountId(userId);
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM transactions WHERE account_id=?"
            );
            ps.setInt(1, accId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getString("type") + " | " +
                    rs.getDouble("amount") + " | " +
                    rs.getTimestamp("date")
                );
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void transfer(int userId, Scanner sc) {
    try {
        sc.nextLine(); // clear buffer

        System.out.print("Enter receiver email: ");
        String email = sc.nextLine();

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();

        Connection con = DBConnection.getConnection();

        // Get receiver account
        PreparedStatement ps1 = con.prepareStatement(
            "SELECT a.account_id FROM accounts a JOIN users u ON a.user_id = u.id WHERE u.email=?"
        );
        ps1.setString(1, email);
        ResultSet rs1 = ps1.executeQuery();

        if (!rs1.next()) {
            System.out.println("Receiver not found!");
            return;
        }

        int receiverAccId = rs1.getInt(1);
        int senderAccId = getAccountId(userId);

        if (senderAccId == receiverAccId) {
            System.out.println("Cannot transfer to yourself!");
            return;
        }

        // Check balance
        PreparedStatement check = con.prepareStatement(
            "SELECT balance FROM accounts WHERE account_id=?"
        );
        check.setInt(1, senderAccId);
        ResultSet rs = check.executeQuery();

        if (rs.next() && rs.getDouble("balance") >= amount) {

            // Deduct
            PreparedStatement deduct = con.prepareStatement(
                "UPDATE accounts SET balance = balance - ? WHERE account_id=?"
            );
            deduct.setDouble(1, amount);
            deduct.setInt(2, senderAccId);
            deduct.executeUpdate();

            // Add
            PreparedStatement add = con.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE account_id=?"
            );
            add.setDouble(1, amount);
            add.setInt(2, receiverAccId);
            add.executeUpdate();

            System.out.println("Transfer successful!");

        } else {
            System.out.println("Insufficient balance!");
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
    }
    public static void showBalance(int userId) {
    try {
        int accId = getAccountId(userId);

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT balance FROM accounts WHERE account_id=?"
        );

        ps.setInt(1, accId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double balance = rs.getDouble("balance");

            System.out.println("\nCurrent Balance: ₹" + balance);
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
//ADMIN
public static void viewAllUsers() {
    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT id, name, email FROM users"
        );

        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== USERS =====");

        while (rs.next()) {
            System.out.println(
                "ID: " + rs.getInt("id") +
                " | Name: " + rs.getString("name") +
                " | Email: " + rs.getString("email")
            );
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
public static void viewAllAccounts() {
    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM accounts"
        );

        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== ACCOUNTS =====");

        while (rs.next()) {
            System.out.println(
                "Account ID: " + rs.getInt("account_id") +
                " | User ID: " + rs.getInt("user_id") +
                " | Balance: ₹" + rs.getDouble("balance")
            );
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
public static void viewAllTransactions() {
    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM transactions"
        );

        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== TRANSACTIONS =====");

        while (rs.next()) {
            System.out.println(
                "Txn ID: " + rs.getInt("txn_id") +
                " | Account ID: " + rs.getInt("account_id") +
                " | Type: " + rs.getString("type") +
                " | Amount: ₹" + rs.getDouble("amount") +
                " | Date: " + rs.getTimestamp("date")
            );
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

}