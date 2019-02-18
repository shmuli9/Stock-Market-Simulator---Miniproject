class Portfolio extends Table {
    private Ledger trades;
    private Holding[] holdings; //rows of the table
    private int oldNum;
    private int numHoldings;
    private double totalCurrVal;
    private double performance; // % change of portfolio

    @Override
    public void printTable() {
//        updateTableData();
        System.out.println("Portfolio:");
        super.printTable();
    }

    public Portfolio() throws ClassNotFoundException {
        super(Class.forName(Holding.class.getName()));
        numHoldings = 0;
        setLedger(new Ledger());
    }

    public void updateTableData() {
        super.clear();
        for (Holding holding : holdings()) {
            addRow(holding.toStringArr());
        }
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
            if (holdings() != null) {
                if (!checkForItem(stockSym, "Symbol")) {  //if this stock hasn't been purchased before then buy as new
                    Holding purchase = new Holding(stockSym, stockPrice, quantity);
                    resizeArray(1)[oldNum] = purchase;
                } else {                                    //else update quantity, and average price
                    findHolding(stockSym).update(stockPrice, quantity);
                }
            } else { //holdings was null, so needs to be initialised here
                resizeArray(1)[0] = new Holding(stockSym, stockPrice, quantity);
            }
            getLedger().addTrade(stockSym, quantity, stockPrice, true);
            updateTableData();
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
                updateTableData();
                return true;
            } else if (quantity == currQty) {
                removeHolding(stockSym);
                getLedger().addTrade(stockSym, quantity, currprice, false);
                updateTableData();
                return true;
            } else {
                print("You do not own enough " + stockSym + " stock to sell " + quantity + " shares");
            }
        } else {
            print("Please enter a quantity greater than 0 to sell");
        }
        return false;
    }

    private Holding[] resizeArray(int diff) {
        Holding[] newHoldings = new Holding[getNumHoldings() + diff];

        if ((getNumHoldings() + diff) > 1) {
            System.arraycopy(holdings(), 0, newHoldings, 0, getNumHoldings());
        }

        setHoldings(newHoldings);
        oldNum = getNumHoldings();
        changeNumHoldings(diff);
        return newHoldings;
    }

    private Holding findHolding(String symbol) {
        for (int i = 0; i < getNumHoldings(); i++) {
            if (holdings()[i].toStringArr()[0].equals(symbol)) {
                return holdings()[i];
            }
        }
        return null;
    }

    private void removeHolding(String symbol) {
        // todo fix this, when there are two holdings, and one is sold completely, it fails
        //  d either with array index out of bounds in the for loop below, or with a null
        //  pointer exception in updateTableData(), in the for loop there
        int numHoldings = getNumHoldings();
        print("numHoldings: " + numHoldings);

        for (int i = 0; i < numHoldings /*&& holdings()[i].toStringArr()[0].equals(symbol)*/; i++) {
            if (holdings()[i].toStringArr()[0].equals(symbol)) {
                holdings()[i] = holdings()[numHoldings - 1];
                resizeArray(-1);
            }
        }
    }

    private int getNumHoldings() {
        return this.numHoldings;
    }

    private void changeNumHoldings(int diff) {
        this.numHoldings += diff;
    }

    private Holding[] holdings() {
        return this.holdings;
    }

    private void setHoldings(Holding[] h) {
        this.holdings = h;
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
