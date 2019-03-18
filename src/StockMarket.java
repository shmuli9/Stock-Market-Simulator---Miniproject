import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.Scanner;

public class StockMarket {
    final static String STR = SM.STR;
    final static String INT = SM.INT;
    final static String DOUBLE = SM.DOUBLE;

    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("gui")) {
                guiInit();
            } else {
                cmdMenu();
            }
        } else {
            String[] opt = {"GUI", "CMD"};
            String inp = SM.printOptions("Would you like to run the simulator from a GUI or the CMD line?", opt, "q");

            if (inp.equalsIgnoreCase("g")) {
                guiInit();
            } else {
                cmdMenu();
            }
        }
//        print("Goodbye!");
//        System.exit(0);
    }

    public static void guiInit() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }
        new GUI();
    }

    public static void cmdMenu() {
        Scanner in = new Scanner(System.in);
        String[] options = {"File", "New"};
        String input = SM.printOptions("Would you like to load stocks from file, or generate new random stock info?", options, "Quit");
        boolean load = input.equals("f");
        SimData sim = new SimData(SM.prompt("Please enter your name: ", STR), load);
        String welc = "Hi, " + sim.getPlayerName() + ", Welcome to Stock Market Simulator 0.3a\nWhat would you like to do?";
        String[] options2 = {"Portfolio", "Stock Market", "Ledger"};
        input = SM.printOptions(welc, options2, "Quit");

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
                    input = SM.printOptions(welc, options2, "q");
                    break;
            }
        }
    }

    public static String ledgerView(Scanner in, SimData sim) {
        printTable(sim.getPortfolio().getLedger());
        return "c";
    }

    public static String stockmarketView(Scanner in, SimData sim) {
        String[] valid = {"Refresh", "Sort", "Preserve", "Menu"};
        String inp = "";
        String sortPref = "Market Cap";

        while (!inp.equals("q")) {
            sim.getMarket().sortTable(sortPref);
            printTable(sim.getMarket());
            inp = SM.printOptions("Options:\nTo refresh stock prices type Refresh\nTo sort, type Sort\nOr to save the current simulation, type Preserve", valid, "q");
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
                case "p":
                    sim.saveToFile();
                    break;
                case "m":
                    return "c";
                default:
                    break;
            }
        }
        return "q";
    }

    public static String portfolioView(Scanner in, SimData sim) {
        String[] options = {"Buy", "Sell", "Ledger", "Menu"};
        String input = "";

        while (!input.equals("q")) {
            printTable(sim.getPortfolio());
            input = SM.printOptions("What would you like to do?", options, "q");

            switch (input) {
                case "b":
                    buy(in, sim);
                    break;
                case "s":
                    sell(in, sim);
                    break;
                case "l":
                    ledgerView(in, sim);
                    break;
                case "m":
                    return "c";
                default:
                    break;
            }
        }
        return "q";
    }

    public static void buy(Scanner in, SimData sim) {
        String sym;
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
            sim.updRandomStocks();
        }
    }

    public static void sell(Scanner in, SimData sim) {
        String sym;
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
            sim.updRandomStocks();
        }

    }

    public static void printTable(Table t) {
        t.printTable();
    }

    public static void print(String msg) {
        System.out.println(msg);
    }// END print

    private static String roundD(Double d) {
        BigDecimal roundedNum = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
        return roundedNum.toString();
    }

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


}







