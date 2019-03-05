import java.util.ArrayList;

class Market extends Table {
//    private static Stock[] stocks;
    private static ArrayList<Stock> stocks;

    public Market() throws ClassNotFoundException {
        super(Class.forName(Stock.class.getName()));
    }

    @Override
    public void printTable() {
        System.out.println("Stock Market");
        super.printTable();
    }

    /***
     * Add stock to Market. If already there, update the values
     * Ideally use updStock to intentionally update stock data
     * @param name
     * @param symbol
     * @param price
     * @param open
     * @param high
     * @param low
     * @return status, true = success, false = failure
     */
    public boolean addStock(String name, String symbol, double price, double open, double high, double low, int numShares) {
//        System.out.println("addStock:");
        if (getStocks() != null) {
            if (!checkForItem(symbol, "Symbol")) {  //if this stock hasnt been added, add now
                Stock stock = new Stock(name, symbol, numShares);
                getStocks().add(stock);
            }
        } else { //getStocks() not yet been initialised
            Stock stock = new Stock(name, symbol, numShares);
            ArrayList<Stock> newHoldings = new ArrayList<>();
            newHoldings.add(stock);

            initStocks(newHoldings);
        }
        updateTableData(getStocks());
        return updStock(symbol, price, open, high, low);  //in all cases updStock
    }

    /**
     * Updates stock info. Stock identified by symbol
     *
     * @param symbol
     * @param price
     * @param open
     * @param high
     * @param low
     * @return status
     */
    public boolean updStock(String symbol, double price, double open, double high, double low) {
//        System.out.println("updStock:");
        if (getStocks() != null) {
            if (checkForItem(symbol, "Symbol")) {  //if this stock already exists then update info for it
                boolean success = findStocks(symbol).updateStock(price, open, high, low);
                updateTableData(getStocks());
                return success;
            } else { //Stock doesnt already exist, use addStock to add new
                return false;
            }
        } else { //getStocks() not initialised
            return false;
        }
    }

    public static void initStocks(ArrayList<Stock> stocks) {
        Market.stocks = stocks;
    }

    public static boolean searchStocks(String symbol) {
        for (int i = 0; i < getNumStocks(); i++) {
//            if (getStocks()[i].toStringArr()[1].equalsIgnoreCase(symbol)) {
//                return true;
//            }
            if (getStocks().get(i).getSymbol().equalsIgnoreCase(symbol)){
                return true;
            }
        }
        return false;
    }

    public static Stock findStocks(String symbol) {
        for (int i = 0; i < getNumStocks(); i++) {
//            if (getStocks()[i].toStringArr()[1].equalsIgnoreCase(symbol)) {
//                return getStocks()[i];
//            }
            if (getStocks().get(i).getSymbol().equalsIgnoreCase(symbol)){
                return getStocks().get(i);
            }
        }
        return null;
    }

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }

    public static int getNumStocks() {
        return stocks.size();
    }


}