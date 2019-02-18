public class Holding {
    private String _symbol;
    private int _quantity;
    private double _averagePurchasePrice;
    private double _totalCurrentValue;
    private double _currentSharePrice;
    private double _performance;

    public Holding(String symbol, double purchasePrice, int quantity) {
        this._symbol = symbol;
        update(purchasePrice, quantity);
    }

    public String[] toStringArr() {
        String[] toString = new String[6]; //size of array is number of fields that will be displayed (define by fields with names begining with _)
        toString[0] = getSymbol();
        toString[1] = String.valueOf(getQuantity());
        toString[2] = String.valueOf(getAPPrice());
        toString[3] = String.valueOf(getTotalV());
        toString[4] = String.valueOf(getCurrentPricePerShare());
        toString[5] = getPerformance() + "%";

        return toString;
    }

    public void update(double purchasePrice, int quantity) {
        int newQty = getQuantity() + quantity;

        this._averagePurchasePrice = ((getAPPrice() * getQuantity()) + (quantity > 0 ? (purchasePrice * quantity) : (getAPPrice() * quantity))) / newQty;
        this._quantity = newQty;
        this._totalCurrentValue = getQuantity() * purchasePrice;   //calc total value from new values
        this._currentSharePrice = purchasePrice;
        this._performance = ((purchasePrice / getAPPrice() * 100) - 100);
    }

    public int getQuantity() {
        return this._quantity;
    }

    public double getAPPrice() {
        return this._averagePurchasePrice;
    }

    public double getTotalV() {
        return this._totalCurrentValue;
    }

    public String getSymbol() {
        return this._symbol;
    }

    private double getCurrentPricePerShare() {
        return _currentSharePrice;
    }

    private double getPerformance() {
        return _performance;
    }

    private void setCurrentPricePerShare(double currentPricePerShare) {
        this._currentSharePrice = currentPricePerShare;
    }

    private void setPerformance(double _performance) {
        this._performance = _performance;
    }
}
