import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {
    Market m1;
    String[][] rows = {{"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000"}, {"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000"}, {"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000"}};
    String[][] rowssortedbyname = {{"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000.0"}, {"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000.0"}, {"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000.0"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000.0"}};
    String[][] rowssortedbyMC = {{"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000"}, {"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000"}, {"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000"}};

    @BeforeEach
    void setUp() {
        try {
            m1 = new Market();
            for (int i = 0; i < rows.length; i++) {
                m1.addStock(rows[i][0], rows[i][1], Double.parseDouble(rows[i][2]), Double.parseDouble(rows[i][3]), Double.parseDouble(rows[i][4]), Double.parseDouble(rows[i][5]), Integer.parseInt(rows[i][6]));
            }
        } catch (ClassNotFoundException e) {
        }
    }

    @Test
    void testPrintTable() {
        m1.printTable();
    }

    @Test
    void testSortMarket(){
        m1.sortTable("Name");
//        print2darray(arrlisttoArr(m1.getTableRows()));
        assertArrayEquals(arrlisttoArr(m1.getTableRows()), rowssortedbyname);

//        m1.sortTable("Market Cap");
//        assertArrayEquals(arrlisttoArr(m1.getTableRows()), rowssortedbyMC);
    }

    private String[][] arrlisttoArr(ArrayList<ArrayList<String>> tableToSort) {
        String[][] table = new String[tableToSort.size()][tableToSort.get(0).size()];

        for (int i = 0; i < tableToSort.size(); i++) {
            ArrayList<String> el = tableToSort.get(i);
            for (int j = 0; j < el.size(); j++) {
                String cell = el.get(j);
                table[i][j] = cell;
            }
        }
        return table;
    }

    private void print2darray(String[][] arr) {
        for (int rowNum = 0; rowNum < arr.length; rowNum++) {    //i counts rows (0th row is column names)
            for (int col = 0; col < arr[0].length; col++) { //col counts columns (0th column is first logical column)
                System.out.println(arr[rowNum][col]);
            }
        }
    }


    @Test
    void searchStocks() {
    }
}