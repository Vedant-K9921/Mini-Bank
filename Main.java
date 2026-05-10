import service.UserService;
import service.BankService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int userId = -1;

        while (true) {

            // LOGIN / REGISTER MENU
            if (userId == -1) {
                System.out.println("\n1. Register\r\n" + //
                                        "2. Login\r\n" + //
                                        "3. Admin Login\r\n" + //
                                        "4. Exit");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
    userId = UserService.register(sc);

} else if (choice == 2) {
    userId = UserService.login(sc);

} else if (choice == 3) {

    sc.nextLine();

    System.out.print("Enter admin username: ");
    String adminUser = sc.nextLine();

    System.out.print("Enter admin password: ");
    String adminPass = sc.nextLine();

    if (adminUser.equals("admin") && adminPass.equals("admin123")) {

        while (true) {

            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. View Users");
            System.out.println("2. View Accounts");
            System.out.println("3. View Transactions");
            System.out.println("4. Exit Admin Panel");

            int adminChoice = sc.nextInt();

            switch (adminChoice) {

                case 1:
                    BankService.viewAllUsers();
                    break;

                case 2:
                    BankService.viewAllAccounts();
                    break;

                case 3:
                    BankService.viewAllTransactions();
                    break;

                case 4:
                    System.out.println("Exiting Admin Panel...");
                    break;

                default:
                    System.out.println("Invalid option!");
            }

            if (adminChoice == 4)
                break;
        }

    } else {
        System.out.println("Invalid admin credentials!");
    }

} else {
    System.out.println("Exiting...");
    break;
}
            }

            // USER MENU AFTER LOGIN
            while (userId != -1) {
                System.out.println("\n1. Deposit\n2. Withdraw\n3. Transactions\n4. Transfer\n5. Show Balance\n6. Logout");
                int opt;
                try {
                    opt = sc.nextInt();
                } catch (Exception e) {
                    System.out.println("Invalid input!");
                    sc.nextLine(); // clear buffer
                    continue;
                }

                switch (opt) {
                    case 1:
                        BankService.deposit(userId, sc);
                        break;
                    case 2:
                        BankService.withdraw(userId, sc);
                        break;
                    case 3:
                        BankService.viewTransactions(userId);
                        break;
                    case 4:
                        BankService.transfer(userId, sc);
                        break;
                    case 5:
                        BankService.showBalance(userId);
                        break;

                    case 6:
                        userId = -1;
                        System.out.println("Logged out!");
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            }
        }

        sc.close();
    }
}