import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private String stockSymbol;
    private String action;
    private int quantity;
    private double price;
    private LocalDateTime dateTime;

    public Transaction(String stockSymbol, String action, int quantity, double price) {
        this.stockSymbol = stockSymbol;
        this.action = action;
        this.quantity = quantity;
        this.price = price;
        this.dateTime = LocalDateTime.now();
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getAction() {
        return action;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalAmount() {
        return quantity * price;
    }

    public String getDateTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return dateTime.format(formatter);
    }
}