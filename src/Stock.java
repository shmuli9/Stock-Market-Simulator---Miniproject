public class Stock implements Row{
    final private String _name;
    final private String _symbol;
    private double _price;
    private double _open;
    private double _high;
    private double _low;
    private int sharesOutstanding;
    private double _marketCap;


    public Stock(String name, String symbol, int numShares) {
        this._name = name;
        this._symbol = symbol;
        this.sharesOutstanding = numShares;
    }

    public String[] toStringArr() {
        String[] toString = new String[7]; //size of array si number of fields

        toString[0] = this._name;
        toString[1] = this._symbol;
        toString[2] = String.valueOf(this._price);
        toString[3] = String.valueOf(this._open);
        toString[4] = String.valueOf(this._high);
        toString[5] = String.valueOf(this._low);
        toString[6] = String.valueOf(this._marketCap);

        return toString;
    }

    public boolean updateStock(double price, double open, double high, double low) {
        if ((low > high) || (low > price) || (price > high)) {
            return false;
        }

        this._price = price;
        this._high = high;
        this._low = low;
        this._open = open;
        this._marketCap = this.sharesOutstanding * price;
        return true;
    }

    @Override
    public String toString() {
        return this._name;
    }

    public String getSymbol() {
        return this._symbol;
    }

    public double getPrice() {
        return _price;
    }

    public String getName() {
        return _name;
    }

    private double get_open() {
        return _open;
    }

    private double get_high() {
        return _high;
    }

    private double get_low() {
        return _low;
    }
}

