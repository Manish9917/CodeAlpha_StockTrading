import java.util.ArrayList;
import java.util.List;

public class StockMarket {

    private final List<Stock> stocks;

    public StockMarket() {

        stocks = new ArrayList<>();

        stocks.add(new Stock("AAPL", "Apple Inc.", 185.42, 183.11));
        stocks.add(new Stock("MSFT", "Microsoft Corp.", 412.17, 407.35));
        stocks.add(new Stock("GOOGL", "Alphabet Inc.", 142.56, 140.63));
        stocks.add(new Stock("AMZN", "Amazon.com Inc.", 178.32, 180.77));
        stocks.add(new Stock("TSLA", "Tesla Inc.", 248.67, 242.46));
        stocks.add(new Stock("NVDA", "NVIDIA Corp.", 921.38, 908.91));
        stocks.add(new Stock("META", "Meta Platforms Inc.", 512.76, 507.44));
        stocks.add(new Stock("NFLX", "Netflix Inc.", 623.14, 627.35));
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks);
    }

    public Stock findStock(String symbol) {

        for (Stock stock : stocks) {

            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }

        return null;
    }
}
