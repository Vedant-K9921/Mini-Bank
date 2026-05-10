package service;
import service.PasswordUtil;
import db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class UserService {

    public static int register(Scanner sc) {
        try {
            System.out.print("Enter name: ");
            String name = sc.nextLine();

            System.out.print("Enter email: ");
            String email = sc.nextLine();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hashPassword(password));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                // Create account automatically
                PreparedStatement acc = con.prepareStatement(
                    "INSERT INTO accounts(user_id, balance) VALUES (?, 0)"
                );
                acc.setInt(1, userId);
                acc.executeUpdate();

                System.out.println("Registration successful!");
                return userId;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public static int login(Scanner sc) {
    try {
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Connection con = DBConnection.getConnection();

        String query = "SELECT * FROM users WHERE email=? AND password=?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, email);

        // HASH HERE
        String hashedPassword = PasswordUtil.hashPassword(password);
        ps.setString(2, hashedPassword);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Login successful!");
            return rs.getInt("id");
        } else {
            System.out.println("Invalid credentials!");
        }

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    return -1;
}
}