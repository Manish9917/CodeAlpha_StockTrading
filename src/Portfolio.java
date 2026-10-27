import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Portfolio implements Serializable {

    private double cashBalance = 10000.00;

    private final List<Holding> holdings = new ArrayList<>();

    private List<Transaction> transactions = new ArrayList<>();

    public double getCashBalance() {
        return cashBalance;
    }

    public List<Holding> getHoldings() {
        return Collections.unmodifiableList(holdings);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public boolean buyStock(Stock stock, int quantity) {

        if (stock == null || quantity <= 0) {
            return false;
        }

        double price = stock.getPrice();
        double totalCost = price * quantity;

        if (totalCost > cashBalance) {
            return false;
        }

        cashBalance -= totalCost;

        boolean stockExists = false;

        for (Holding holding : holdings) {
            if (holding.getStock().getSymbol()
                    .equalsIgnoreCase(stock.getSymbol())) {

                holding.addQuantity(quantity);
                stockExists = true;
                break;
            }
        }

        if (!stockExists) {
            holdings.add(new Holding(stock, quantity));
        }

        // Record successful BUY transaction
        transactions.add(
                new Transaction(stock.getSymbol(), "BUY", quantity, price));

        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {

        if (stock == null || quantity <= 0) {
            return false;
        }

        for (int i = 0; i < holdings.size(); i++) {

            Holding holding = holdings.get(i);

            if (holding.getStock().getSymbol()
                    .equalsIgnoreCase(stock.getSymbol())) {

                if (quantity > holding.getQuantity()) {
                    return false;
                }

                double price = stock.getPrice();

                holding.removeQuantity(quantity);
                cashBalance += price * quantity;

                // Record successful SELL transaction
                transactions.add(
                        new Transaction(stock.getSymbol(), "SELL", quantity, price));

                if (holding.getQuantity() == 0) {
                    holdings.remove(i);
                }

                return true;
            }
        }

        return false;
    }

    public double getPortfolioValue() {

        double totalValue = 0;

        for (Holding holding : holdings) {
            totalValue += holding.getCurrentValue();
        }

        return totalValue;
    }

    // Calculate the total purchase cost of current holdings
    public double getTotalInvestedAmount() {

        double totalInvested = 0;

        for (Holding holding : holdings) {
            totalInvested += holding.getTotalInvested();
        }

        return totalInvested;
    }

    // Calculate unrealized profit or loss
    public double getProfitLoss() {
        return getPortfolioValue() - getTotalInvestedAmount();
    }

    // Calculate profit or loss percentage
    public double getProfitLossPercentage() {

        double investedAmount = getTotalInvestedAmount();

        if (investedAmount == 0) {
            return 0;
        }

        return (getProfitLoss() / investedAmount) * 100;
    }

    public double getTotalAccountValue() {
        return cashBalance + getPortfolioValue();
    }

    // Create a separate portfolio file for each username
    private static String getPortfolioFileName(String username) {
        if (username == null ||
                !username.matches("[a-zA-Z0-9_]{3,30}")) {
            throw new IllegalArgumentException("Invalid username.");
        }

        return "portfolio_" + username.toLowerCase() + ".dat";
    }

    public void saveToFile(String username) {
        String fileName = getPortfolioFileName(username);

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(fileName))) {

            output.writeObject(this);
            System.out.println(
                    "Portfolio saved successfully for " + username + ".");

        } catch (IOException e) {
            System.err.println(
                    "Could not save portfolio: " + e.getMessage());
        }
    }

    public static Portfolio loadFromFile(String username) {
        String fileName = getPortfolioFileName(username);

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(fileName))) {

            Object savedObject = input.readObject();

            if (savedObject instanceof Portfolio) {
                System.out.println(
                        "Portfolio loaded successfully for " + username + ".");
                return (Portfolio) savedObject;
            }

        } catch (java.io.FileNotFoundException e) {
            System.out.println(
                    "No saved portfolio found for " + username
                            + ". Starting a new portfolio.");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(
                    "Could not load portfolio: " + e.getMessage());
        }

        return new Portfolio();
    }

}
