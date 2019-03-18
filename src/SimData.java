import java.io.IOException;
import java.util.Random;

/***
 * Class SimData : Author: Shmuel Margulies, 16/01/19
 *
 * This is an abstract data type to carry all info for a player on StockMarketSim
 * This class is also responsible for loading data into the stock market,
 * either randomly or by reading from a file. It also controls the save
 * to file functionality
 */
class SimData {
    private Portfolio portfolio;
    private Market market;
    private String playerName;
    private double funds;

    public SimData(String playerName, boolean loadFromFile) {
        try {
            setPortfolio(new Portfolio());
            setMarket(new Market());
        } catch (ClassNotFoundException e) {
            print(e.toString());
        }

        if (loadFromFile) {
            getMarket().loadFromFile();
            getPortfolio().loadFromFile();
        } else {
            genRandomStocks(4);
        }
        setPlayerName(playerName);
    }

    protected void saveToFile() {
        try {
            getMarket().saveToFile("stocks");
            getPortfolio().saveToFile("portf");
            getPortfolio().getLedger().saveToFile("ledger");
        } catch (IOException e) {
            print(e.toString());
        }
    }

    private void genRandomStocks(int numStocks) {
        String[] StockNames = {"Apple", "Microsoft", "Facebook", "Google"};
        String[] StockSymbols = {"AAPL", "MSFT", "FB", "GOOG"};
        int randomI = randomWork(StockNames.length);

        while (getMarket().getRows() < numStocks) {
//            print("genRandomStocks loop");
            addRandomStocks(StockNames[randomI], StockSymbols[randomI]);
            randomI = randomWork(StockNames.length);
        }
    }

    private void addRandomStocks(String name, String symbol) {
        double price, open, low, high;

        boolean success = false;
        int i = 0;
        while (!success) {
//            print("addRandomStocks loop");
            price = randomWork(500, 1500);
            open = randomWork(500, 1500);
            high = randomWork(500, 1500);
            low = randomWork(500, 1500);

            success = getMarket().addStock(name, symbol, price, open, high, low, 1000000000);
        }
    }

    public void updRandomStocks() {
        double price, open, low, high;

        for (Stock s : getMarket().getStocks()) {
            boolean success = false;

            while (!success) {
                price = randomWork(500, 1500);
                open = randomWork(500, 1500);
                high = randomWork(500, 1500);
                low = randomWork(500, 1500);
//                print("updRandomStocks");
                success = getMarket().updStock(s.getSymbol(), price, open, high, low);
            }
        }
    }

    /***********************************
     * Limit defines max+1 number returned (from 0) ie if limit equals 2, possible
     * values are 0 and 1
     */
    private double randomWork(int min, int max) {
        Random rgn = new Random();
        int range = max - min;

        return min + rgn.nextDouble() * range;
    }

    public int randomWork(int max) {
        Random rgn = new Random();

        return rgn.nextInt(max);
    }

    public String getPlayerName() {
        return playerName;
    }

    private void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    private static void print(String msg) {
        System.out.println(msg);
    }// END print

    public Portfolio getPortfolio() {
        return portfolio;
    }

    private void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Market getMarket() {
        return market;
    }

    private void setMarket(Market market) {
        this.market = market;
    }

}
