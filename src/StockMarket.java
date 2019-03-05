import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.Scanner;

public class StockMarket {
    final static String STR = SM.STR;
    final static String INT = SM.INT;
    final static String DOUBLE = SM.DOUBLE;

    public static void main(String[] args) {
        menu();
//        TEST();

        print("Goodbye!");

        //todo make sure after every transaction stock prices update
    }

    public static void menu() {
        Scanner in = new Scanner(System.in);
        String input;
        SimData sim = new SimData(SM.prompt("Please enter your name: ", STR));
        String welc = "Hi, " + sim.getPlayerName() + ", Welcome to Stock Market Simulator 0.3a\nWhat would you like to do?";
        String[] options = {"Portfolio", "Stock Market", "Ledger"};
        input = SM.printOptions(welc, options, "Quit");
        try {
            sim.getMarket().saveToFile();
        } catch (IOException e) {
        }


        while (!input.equals("q")) {
            switch (input) {
                case "p":
                    input = portfolioView(in, sim);
                    break;
                case "s":
                    input = stockmarketView(in, sim);
                    break;
                case "l":
                    input = ledgerView(in, sim);
                    break;
                default:
                    input = SM.printOptions(welc, options, "q");
                    break;
            }
        }
    }

    public static String ledgerView(Scanner in, SimData sim) {
        //sim.getPortfolio().getLedger().printTable();
        printTable(sim.getPortfolio().getLedger());
        return "c";
    }

    public static String stockmarketView(Scanner in, SimData sim) {
        String[] valid = {"Refresh", "Sort", "Menu"};
        String inp = "";
        String sortPref = "Market Cap";

        while (!inp.equals("q")) {
            sim.getMarket().sortTable(sortPref);
            printTable(sim.getMarket());
            inp = SM.printOptions("Options:\nTo refresh stock prices type R\nTo sort, type S", valid, "q");
            switch (inp) {
                case "r":
                    sim.updRandomStocks();
                    break;
                case "s":
                    sortPref = SM.prompt("Please enter a column to sort by:", STR);
                    if (sortPref.equals("q")) {
                        return "q";
                    }
                    break;
                case "m":
                    return "c";
                default:
                    break;
            }
        }
        return "q";
        //sim.updRandomStocks();
    }

    public static String portfolioView(Scanner in, SimData sim) {
        String[] options = {"Buy", "Sell", "Ledger", "Menu"};
        String input = "";

        while (!input.equals("q")) {
            printTable(sim.getPortfolio());
            //sim.getPortfolio().printTable(/*sim.getPlayerName() + "'s portfolio"*/);
            input = SM.printOptions("What would you like to do?", options, "q");

            switch (input) {
                case "b":
                    buy(in, sim);
                    sim.updRandomStocks();
                    break;
                case "s":
                    sell(in, sim);
                    sim.updRandomStocks();
                    break;
                case "l":
                    ledgerView(in, sim);
                    break;
                case "m":
                    //menu();
                    return "c";
                default:
                    break;
            }
        }
        return "q";

//        while (!input.equals("q")) {
//            sim.getPortfolio().printTable(sim.getPlayerName() + "'s portfolio");
//            print("To buy a stock press b, or to sell an existing holding press se");
//            input = in.nextLine();
//
//            if (input.equals("b")) {
//                buy(in, sim);
//                sim.updRandomStocks();
//            } else if (input.equals("se")) {
//                sell(in, sim);
//                sim.updRandomStocks();
//            }
//            print("To buy a stock press b, or to sell an existing holding press se");
//            input = in.nextLine();
//        }
    }

    public static void buy(Scanner in, SimData sim) {
        String sym /*= in.nextLine()*/;
        sym = SM.prompt("Enter the stock symbol that you would like to purchase:", STR);

        while (!Market.searchStocks(sym)) {
            sym = SM.prompt("Stock symbol not found\nEnter the stock symbol that you would like to purchase:", STR);
        }

        String tprice = sim.getMarket().lookupCell(sym, "Symbol", "Price");
        print("Stock " + sym + " found, price per share: " + tprice);
        int qty;
        qty = Integer.parseInt(SM.prompt("Please enter quantity of this stock that you would like to buy", INT));

        String totalPurchPrice = roundD(Double.parseDouble(tprice) * qty);

        String inp = SM.prompt("Total purchase will be " + totalPurchPrice + "\nTo confirm type y", STR);

        if (!inp.equals("y")) {
            print("OK, purchase cancelled");
        } else {
            sim.getPortfolio().buyStock(sym, qty, Double.parseDouble(tprice));
            //sim.getPortfolio().getLedger().printTable();
        }
    }

    public static Object menuItem(String msg, Scanner in, Class<?> type) {
        print(msg);
        String input = in.nextLine();

        Class<?> x = type.getClass();/*? (() input) : null;*/
        try {
            return type.cast(input);
        } catch (Error e) {
            print(e.toString());
            return "Not of type " + type;
        }

        //return input;
    }

    public static void sell(Scanner in, SimData sim) {
        //print("Enter symbol of holding to sell");
        String sym /*= in.nextLine()*/;
        sym = SM.prompt("Enter the stock symbol that you would like to sell:", STR);
        String input = sym;

        while (!sim.getPortfolio().checkForItem(sym, "Symbol")) {
            sym = SM.prompt("You do not own shares with symbol: " + sym + "\nEnter the stock symbol of the holding you would like to sell: ", STR);
        }

        double amt = Double.parseDouble(sim.getPortfolio().lookupCell(sym, "Symbol", "Quantity"));
        int amt2sell = Integer.parseInt(SM.prompt("You own " + amt + " shares of this stock. How many would you like to sell?", INT));

        while (amt2sell > amt || amt2sell < 1) {
            amt2sell = Integer.parseInt(SM.prompt("Please enter an amount, between 1 and " + amt, INT));
        }

        double salePrice = Market.findStocks(sym).getPrice();

        String inp = SM.prompt("Total sale will net " + roundD(salePrice * amt2sell) + "\nTo confirm type y", STR);

        if (!inp.equals("y")) {
            print("OK, sale executed");
        } else {
            sim.getPortfolio().sellStock(sym, amt2sell, salePrice);
            //sim.getPortfolio().getLedger().printTable();
        }
//        while (!input.equals("q")) {
//            if (sim.getPortfolio().checkForItem(sym, "Symbol")) {
//                double amt = Double.parseDouble(sim.getPortfolio().lookupCell(sym, "Symbol", "Quantity"));
//                print("You own " + amt + " shares of this stock. How many would you like to sell?");
//                input = in.nextLine();
//                if (Integer.parseInt(input) <= amt) {
//                    sim.getPortfolio().sellStock(sym, Integer.parseInt(input));
//                    input = "q";
//                } else {
//                    print("Please enter an amount, no more than " + amt);
//                }
//            } else {
//                print("You do not own shares from " + input);
//                print("Enter symbol of holding to sell");
//                sym = in.nextLine();
//            }
//        }
    }

    public static void print(String msg) {
        System.out.println(msg);
    }// END print

    private static String roundD(Double d) {
        BigDecimal roundedNum = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
        return roundedNum.toString();
    }

//    public static boolean isQ(String s){
//        if (isQ){
//            return true;
//        }
//        return false;
//    }

//    public static void TESTS() {
//        String[] cols = {"Col1", "Col2", "Col3"};
//        Table testTable = new Table(cols);
//        //testTable.printTable();
//
//        String[] newRow = {"data1", "data2", "data3"};
//        testTable.addRow(newRow);
//
//        String[][] newRows = {{"data4", "dat        a5", "data6"}, {"rows      1", "rows2", "rows3"}};
//        testTable.addMultRows(newRows);
//        testTable.printTable("TEST");
//        //testTable.printColumn("Col2");
//
//        String[] cols2 = {"ID", "Name", "Date"};
//        Table testTable2 = new Table(cols2);
//    }

    /***********************************
     * Limit defines max+1 number returned (from 0) ie if limit equals 2, possible
     * values are 0 and 1
     */
    public static int randomWork(int min, int max) {
        Random rgn = new Random();
        int range = max - min;
        int randomNumber = min + rgn.nextInt(range);

        return randomNumber;
    }

    public static int randomWork(int max) {
        Random rgn = new Random();
        int randomNumber = rgn.nextInt(max);

        return randomNumber;
    }

    public static void printTable(Table t) {
        t.printTable();
    }
}







