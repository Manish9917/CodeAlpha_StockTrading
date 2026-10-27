import java.io.Serializable;

public class Holding implements Serializable {

    private Stock stock;
    private int quantity;
    private double totalInvested;

    public Holding(Stock stock, int quantity) {
        if (stock == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Stock must not be null and quantity must be greater than zero."
            );
        }

        this.stock = stock;
        this.quantity = quantity;
        this.totalInvested = stock.getPrice() * quantity;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalInvested() {
        return totalInvested;
    }

    public double getAveragePurchasePrice() {
        if (quantity == 0) {
            return 0;
        }

        return totalInvested / quantity;
    }

    public void addQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        totalInvested += stock.getPrice() * quantity;
        this.quantity += quantity;
    }

    public boolean removeQuantity(int quantity) {
        if (quantity <= 0 || quantity > this.quantity) {
            return false;
        }

        double averagePurchasePrice = getAveragePurchasePrice();

        totalInvested -= averagePurchasePrice * quantity;
        this.quantity -= quantity;

        if (this.quantity == 0) {
            totalInvested = 0;
        }

        return true;
    }

    public double getCurrentValue() {
        return stock.getPrice() * quantity;
    }
}
