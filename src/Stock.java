import java.io.Serializable;

public class Stock implements Serializable {

    private String symbol;
    private String companyName;
    private double price;
    private double previousPrice;

    // Constructor
    public Stock(String symbol, String companyName,
                 double price, double previousPrice) {

        this.symbol = symbol;
        this.companyName = companyName;
        setPrice(price);
        setPreviousPrice(previousPrice);
    }

    // Get stock symbol
    public String getSymbol() {
        return symbol;
    }

    // Get company name
    public String getCompanyName() {
        return companyName;
    }

    // Get current stock price
    public double getPrice() {
        return price;
    }

    // Update current stock price
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Stock price must be greater than zero."
            );
        }

        this.price = price;
    }

    // Get previous stock price
    public double getPreviousPrice() {
        return previousPrice;
    }

    // Update previous stock price
    public void setPreviousPrice(double previousPrice) {
        if (previousPrice <= 0) {
            throw new IllegalArgumentException(
                    "Previous price must be greater than zero."
            );
        }

        this.previousPrice = previousPrice;
    }

    // Calculate price change
    public double getPriceChange() {
        return price - previousPrice;
    }

    // Calculate percentage change
    public double getPriceChangePercentage() {
        return (getPriceChange() / previousPrice) * 100;
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName
                + " | Price: $" + String.format("%.2f", price);
    }
}
