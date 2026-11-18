
import java.io.*;
import java.util.*;

class Transaction {
    int id;
    String type;
    String category;
    double amount;
    String date;

    Transaction(int id, String type, String category,
                double amount, String date) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    void display() {
        System.out.printf(
            "ID: %d | %s | %s | Rs. %.2f | %s%n",
            id, type, category, amount, date
        );
    }
}

public class Main {
    static ArrayList<Transaction> records = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "budget.txt";

    static void loadData() {
        try (BufferedReader br =
                 new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", 5);

                if (data.length == 5) {
                    records.add(new Transaction(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        Double.parseDouble(data[3]),
                        data[4]
                    ));
                }
            }

        } catch (FileNotFoundException e) {
            // File will be created when the first transaction is saved.
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading budget records.");
        }
    }

    static void saveData() {
        try (PrintWriter writer =
                 new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Transaction t : records) {
                writer.println(
                    t.id + "|" + t.type + "|" +
                    t.category + "|" + t.amount + "|" + t.date
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    static int nextID() {
        int maxID = 0;

        for (Transaction t : records) {
            if (t.id > maxID)
                maxID = t.id;
        }

        return maxID + 1;
    }

    static void addTransaction() {
        System.out.println("1. Income");
        System.out.println("2. Expense");
        System.out.print("Choose transaction type: ");

        int choice = readNumber();

        String type;

        if (choice == 1)
            type = "Income";
        else if (choice == 2)
            type = "Expense";
        else {
            System.out.println("Invalid choice!");
            return;
        }

        System.out.print("Enter category: ");
        String category = sc.nextLine().trim();

        System.out.print("Enter amount: ");
        double amount;

        try {
            amount = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount!");
            return;
        }

        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        if (category.isEmpty() || date.isEmpty()
                || category.contains("|")
                || date.contains("|") || amount <= 0) {
            System.out.println("Invalid transaction details!");
            return;
        }

        Transaction t = new Transaction(
            nextID(), type, category, amount, date
        );

        records.add(t);
        saveData();

        System.out.println("Transaction added successfully!");
    }

    static void viewTransactions() {
        if (records.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n===== ALL TRANSACTIONS =====");

        for (Transaction t : records) {
            t.display();
        }
    }

    static void showSummary() {
        double income = 0;
        double expenses = 0;

        HashMap<String, Double> categoryTotals =
            new HashMap<>();

        for (Transaction t : records) {
            if (t.type.equals("Income")) {
                income += t.amount;
            } else if (t.type.equals("Expense")) {
                expenses += t.amount;

                categoryTotals.put(
                    t.category,
                    categoryTotals.getOrDefault(t.category, 0.0)
                        + t.amount
                );
            }
        }

        System.out.println("\n===== BUDGET SUMMARY =====");
        System.out.printf("Total Income: Rs. %.2f%n", income);
        System.out.printf("Total Expenses: Rs. %.2f%n", expenses);
        System.out.printf("Remaining Balance: Rs. %.2f%n",
                          income - expenses);

        System.out.println("\n===== CATEGORY-WISE EXPENSES =====");

        for (String category : categoryTotals.keySet()) {
            System.out.printf(
                "%s: Rs. %.2f%n",
                category,
                categoryTotals.get(category)
            );
        }
    }

    static void searchCategory() {
        System.out.print("Enter category to search: ");
        String category = sc.nextLine().trim();

        boolean found = false;

        for (Transaction t : records) {
            if (t.category.equalsIgnoreCase(category)) {
                t.display();
                found = true;
            }
        }

        if (!found)
            System.out.println("No transactions found.");
    }

    static int readNumber() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        loadData();

        int choice;

        do {
            System.out.println("\n===== PERSONAL BUDGET PLANNER =====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. Budget Summary");
            System.out.println("4. Search by Category");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = readNumber();

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    showSummary();
                    break;
                case 4:
                    searchCategory();
                    break;
                case 5:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 5);

        sc.close();
    }
}
