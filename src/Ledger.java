/***************************************
 * This class defines the ledger, which records all transcations for a SimData object.
 * Both buy and sell are represented.
 */

import java.util.ArrayList;

public class Ledger extends Table {
    private ArrayList<Trade> trades;

    /***************
     * Constructor
     * @throws ClassNotFoundException
     */
    public Ledger() throws ClassNotFoundException {
        super(Class.forName(Trade.class.getName())); //Autogenerate Table headings
        initTrades();
    }

    public void addTrade(String symbol, int quantity, double averagePurchasePrice, boolean BuyOrSale) {
        Trade trade = new Trade(symbol, quantity, averagePurchasePrice, BuyOrSale);
        getTrades().add(trade);

//        updateTableData(getTrades().toArray(new Trade[0]));
        updateTableData(getTrades());
    }

    @Override
    public void printTable() {
        System.out.println("Ledger:");
        super.printTable();
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }

    public void initTrades() {
        this.trades = new ArrayList<Trade>();
    }
}

class Trade implements Row{
    //vars
    private String _symbol;
    private int _quantity;
    private double _price;
    private double _totalValue;
    private boolean _BuyOrSale;
    private double purchaseDate;  //Stock purchased on
    private double salePrice; //sale price when sold
    private boolean sold; //has this holding been sold


    public Trade(String _symbol, int _quantity, double _averagePrice, boolean _BuyOrSale) {
        this._symbol = _symbol;
        this._quantity = _quantity;
        this._price = _averagePrice;
        this._totalValue = _averagePrice * _quantity;
        this._BuyOrSale = _BuyOrSale;
    }

    public String[] toStringArr() {
        String[] toString = new String[5]; //size of array si number of fields

        toString[0] = this._symbol;
        toString[1] = String.valueOf(this._quantity);
        toString[2] = String.valueOf(this._BuyOrSale ? this._price : "+" + this._price);
        toString[3] = String.valueOf(this._BuyOrSale ? this._totalValue : "+" + this._totalValue);
        toString[4] = this._BuyOrSale ? "Buy" : "Sell";

        return toString;
    }


    //private Date purchaseDate;  //Stock purchased on
    //private double salePrice; //sale price when sold
    //private boolean sold; //has this holding been sold
}
