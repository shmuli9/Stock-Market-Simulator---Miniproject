import java.util.Date;

class Market extends Table {
    private static Stock[] stocks;
    private Date today;

    public Market() throws ClassNotFoundException {
//        super();
        super(Class.forName(Stock.class.getName()));
    }

    @Override
    public void printTable() {
        updateTableData();
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
        System.out.println("addStock:");
        if (getStocks() != null) {
            if (!checkForItem(symbol, "Symbol")) {  //if this stock hasnt been added, add now
                Stock stock = new Stock(name, symbol, numShares);
                int currLength = getNumStocks();

                Stock[] newHoldings = new Stock[currLength + 1];
                System.arraycopy(getStocks(), 0, newHoldings, 0, currLength);

                newHoldings[currLength] = stock;
                stocks = newHoldings;
            }
        } else { //getStocks() not yet been initialised
            Stock stock = new Stock(name, symbol, numShares);
            Stock[] newHoldings = new Stock[1];

            newHoldings[0] = stock;
            stocks = newHoldings;
        }
        updateTableData();
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
        System.out.println("updStock:");
        if (getStocks() != null) {
            if (checkForItem(symbol, "Symbol")) {  //if this stock already exists then update info for it
                boolean success = findStocks(symbol).updateStock(price, open, high, low);
                updateTableData();
                return success;
            } else { //Stock doesnt already exist, use addStock to add new
                return false;
            }
        } else { //getStocks() not initialised
            return false;
        }
    }

    private void updateTableData() {
        super.clear();
        for (Stock stock : getStocks()) {
//            System.out.print("updateTableData called ");
            addRow(stock.toStringArr());
        }
    }

    public static boolean searchStocks(String symbol) {
        for (int i = 0; i < getNumStocks(); i++) {
            if (getStocks()[i].toStringArr()[1].equalsIgnoreCase(symbol)) {
                return true;
            }
        }
        return false;
    }

    public static Stock findStocks(String symbol) {
        for (int i = 0; i < getNumStocks(); i++) {
            if (getStocks()[i].toStringArr()[1].equalsIgnoreCase(symbol)) {
                return getStocks()[i];
            }
        }
        return null;
    }

    private static Stock[] getStocks() {
        return stocks;
    }

    public Stock[] getStock() {
        return stocks;
    }

    public static int getNumStocks() {
        return stocks.length;
    }


}