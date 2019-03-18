import java.io.IOException;
import java.util.ArrayList;

class Portfolio extends Table {
    private Ledger trades;
    private ArrayList<Holding> holdings;
    private double totalCurrVal;

    @Override
    public void printTable() {
        System.out.println("Portfolio:");
        super.printTable();
    }

    public Portfolio() throws ClassNotFoundException {
        super(Class.forName(Holding.class.getName()));
        setLedger(new Ledger());
    }


    /************
     * Loads saved file data from portf.text to repopulate the Portfolio table
     * and from ledger.txt to repopulate the Ledger table
     * @return
     */
    protected boolean loadFromFile() {
        try {
            initHoldings();
            for (ArrayList<String> row : readFile("portf")) {
                String symbol = row.get(0);
                int quantity = Integer.parseInt(row.get(1));
                double averagePurchasePrice = Double.parseDouble(row.get(2));

                getHoldings().add(new Holding(symbol, averagePurchasePrice, quantity));
            }
            updateTableData(getHoldings());

            for (ArrayList<String> row : readFile("ledger")) {
                String symbol = row.get(0);
                int quantity = Integer.parseInt(row.get(1));
                double price = Double.parseDouble(row.get(2));
                boolean BuyOrSale = row.get(4).equalsIgnoreCase("Buy");

                getLedger().addTrade(symbol, quantity, price, BuyOrSale);
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /***
     * Buy stock, add to portfolio if not already there and  update if already owned
     * @param stockSym
     * @param quantity
     * @param stockPrice
     * @return status of buy, true = success, false = failure
     */
    public boolean buyStock(String stockSym, int quantity, double stockPrice) { //only able to buy when Market has been initialised AND populated with stocks
        if (!Market.searchStocks(stockSym)) {
            print("No symbol matching " + stockSym + " found in Stock Market");
            return false;
        }

        if (quantity > 0) {
            if (getHoldings() != null) {
                if (!checkForItem(stockSym, "Symbol")) {  //if this stock hasn't been purchased before then buy as new
                    Holding purchase = new Holding(stockSym, stockPrice, quantity);
                    getHoldings().add(purchase);
                } else {                                    //else update quantity, and average price
                    findHolding(stockSym).update(stockPrice, quantity);
                }
            } else { //getHoldings was null, so needs to be initialised here
                initHoldings();
                getHoldings().add(new Holding(stockSym, stockPrice, quantity));
            }

            getLedger().addTrade(stockSym, quantity, stockPrice, true); //add trade to ledger
            updateTableData(getHoldings());
            return true;
        } else {
            print("Please enter a quantity greater than 0.");
            return false;
        }
    }

    public boolean sellStock(String stockSym, int quantity, double currprice) {
        //do we own this stock to sell?
        if (!checkForItem(stockSym, "Symbol")) {
            print("You do not own  " + stockSym + " stock");
            return false;
        }

        Holding thisHolding = findHolding(stockSym);

        if (quantity > 0) {
            int currQty = thisHolding.getQuantity();
            if (quantity < currQty) {
                thisHolding.update(currprice, -quantity); //1. reduce quantity of owned 2. remove entirley
                getLedger().addTrade(stockSym, quantity, currprice, false);
                updateTableData(getHoldings());
                return true;
            } else if (quantity == currQty) {
                getHoldings().remove(findHolding(stockSym));
                getLedger().addTrade(stockSym, quantity, currprice, false);
                updateTableData(getHoldings());
                return true;
            } else {
                print("You do not own enough " + stockSym + " stock to sell " + quantity + " shares");
            }
        } else {
            print("Please enter a quantity greater than 0 to sell");
        }
        return false;
    }

    /************
     * Returns Holding object (or reference to it at least)
     * @param symbol
     * @return
     */
    private Holding findHolding(String symbol) {
        for (int i = 0; i < getNumHoldings(); i++) {
            if (getHoldings().get(i).getSymbol().equals(symbol)) {
                return getHoldings().get(i);
            }
        }
        return null;
    }

    private int getNumHoldings() {
        return this.holdings.size();
    }

    private ArrayList<Holding> getHoldings() {
        return this.holdings;
    }

    private void initHoldings() {
        this.holdings = new ArrayList<Holding>();
    }

    private void print(String msg) {
        System.out.println(msg);
    }// END print

    private void print(int msg) {
        System.out.println(msg);
    }// END print

    public Ledger getLedger() {
        return trades;
    }

    public void setLedger(Ledger trades) {
        this.trades = trades;
    }
}
