import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    Table t1;
    Table t2;
    Table t3;
    String[] cols = {"Name", "Address", "PhoneN"};
    String[][] rows = {{"Me", "72 Address", "12345"}, {"Shm", "E 42nd Address", "54321"}, {"Dan", "Golders", "76821"}, {"Zac", "93", "1234500"}};
    String[][] rowssortedbyname = {{"Dan", "Golders", "76821"}, {"Me", "72 Address", "12345"}, {"Shm", "E 42nd Address", "54321"}, {"Zac", "93", "1234500"}};
    String[][] rows2 = {{"Google", "GOOG", "3", "1", "1", "1", "3000"}, {"Apple", "AAPL", "1", "1", "1", "1", "1000"}, {"Microsoft", "MSFT", "2", "1", "1", "1", "2000"}, {"Facebook", "FB", "4", "1", "1", "1", "4000"}};
    String[][] rows2sortedbyname = {{"Apple", "AAPL", "1", "1", "1", "1", "1000"}, {"Facebook", "FB", "4", "1", "1", "1", "4000"}, {"Google", "GOOG", "3", "1", "1", "1", "3000"}, {"Microsoft", "MSFT", "2", "1", "1", "1", "2000"}};
    String[][] rows2sortedbyMC = {{"Facebook", "FB", "4", "1", "1", "1", "4000"}, {"Google", "GOOG", "3", "1", "1", "1", "3000"}, {"Microsoft", "MSFT", "2", "1", "1", "1", "2000"}, {"Apple", "AAPL", "1", "1", "1", "1", "1000"}};
    String[][] rows3 = {{"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000"}, {"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000"}, {"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000"}};
    String[][] rows3sortedbyname = {{"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000"}, {"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000"}, {"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000"}};
    String[][] rows3sortedbyMC = {{"Facebook", "FB", "1.0", "1.0", "4.0", "1.0", "4000"}, {"Google", "GOOG", "1.0", "1.0", "3.0", "1.0", "3000"}, {"Microsoft", "MSFT", "1.0", "1.0", "2.0", "1.0", "2000"}, {"Apple", "AAPL", "1.0", "1.0", "1.0", "1.0", "1000"}};

    @BeforeEach
    void setUp() {
        t1 = new Table(cols);
//        t1.addMultRows(rows);
        for (int i = 0; i < rows.length; i++) {
            t1.addRow(rows[i]);
        }
        try {
            t2 = new Table(Class.forName(Stock.class.getName()));
//            t2.addMultRows(rows2);
            for (int i = 0; i < rows2.length; i++) {
                t2.addRow(rows2[i]);
            }

            t3 = new Market();
//            t2.addMultRows(rows2);
            for (int i = 0; i < rows3.length; i++) {
                t3.addRow(rows3[i]);
            }
        } catch (ClassNotFoundException e) {
        }
    }

    @Test
    void testPrintTable() {
        System.out.println("Table 1");
        t1.printTable();
        System.out.println("Table 2");
        t2.printTable();
        System.out.println("Table 3");
        t3.printTable();
    }

    @Test
    void t1clear() {
        t1.clear();

        ArrayList<String> ColumnRow = t1.getTableData().get(0);
        String[] originalCols = new String[ColumnRow.size() - 1];
        for (int i = 0; i < ColumnRow.size() - 1; i++) {
            originalCols[i] = ColumnRow.get(i + 1);
        }

        assertArrayEquals(cols, originalCols);
    }

    @Test
    void t2clear() {
        t2.clear();
        String[] cols2 = {"Name", "Symbol", "Price", "Open", "High", "Low", "Market Cap"};
        ArrayList<String> ColumnRow = t2.getTableData().get(0);
        String[] originalCols = new String[ColumnRow.size() - 1];

        for (int i = 0; i < ColumnRow.size() - 1; i++) {
            originalCols[i] = ColumnRow.get(i + 1);
        }

        assertArrayEquals(cols2, originalCols);
    }

    @Test
    void t3clear() {
        t3.clear();
        String[] cols3 = {"Name", "Symbol", "Price", "Open", "High", "Low", "Market Cap"};
        ArrayList<String> ColumnRow = t3.getTableData().get(0);
        String[] originalCols = new String[ColumnRow.size() - 1];

        for (int i = 0; i < ColumnRow.size() - 1; i++) {
            originalCols[i] = ColumnRow.get(i + 1);
        }

        assertArrayEquals(cols3, originalCols);
    }

    @Test
    void testArrListtoArr() {
        System.out.println("Original:");
        print2darray(rows);
        System.out.println("From Table:");
        print2darray(Table.ArrayListto2DArray(t1.getTableRows()));
        assertArrayEquals(rows, Table.ArrayListto2DArray(t1.getTableRows()));

        System.out.println("Original:");
        print2darray(rows2);
        System.out.println("From Table:");
        print2darray(Table.ArrayListto2DArray(t2.getTableRows()));
        assertArrayEquals(rows2, Table.ArrayListto2DArray(t2.getTableRows()));

        System.out.println("Original:");
        print2darray(rows3);
        System.out.println("From Table:");
        print2darray(Table.ArrayListto2DArray(t3.getTableRows()));
        assertArrayEquals(rows3, Table.ArrayListto2DArray(t3.getTableRows()));
    }

    @Test
    void sortTable() {
        t1.sortTable("Name");
        assertArrayEquals(Table.ArrayListto2DArray(t1.getTableRows()), rowssortedbyname);
        System.out.println("Finished Table 1");

        t2.sortTable("Market Cap");
        assertArrayEquals(Table.ArrayListto2DArray(t2.getTableRows()), rows2sortedbyMC);

        t2.sortTable("Name");
        assertArrayEquals(Table.ArrayListto2DArray(t2.getTableRows()), rows2sortedbyname);

        t2.sortTable("Market Cap");
        assertArrayEquals(Table.ArrayListto2DArray(t2.getTableRows()), rows2sortedbyMC);
        System.out.println("Finished Table 2");

        t3.sortTable("Market Cap");
        assertArrayEquals(rows3sortedbyMC, Table.ArrayListto2DArray(t3.getTableRows()));

        t3.sortTable("Name");
        assertArrayEquals(rows3sortedbyname, Table.ArrayListto2DArray(t3.getTableRows()));

        t3.sortTable("Market Cap");
        assertArrayEquals(rows3sortedbyMC, Table.ArrayListto2DArray(t3.getTableRows()));
        System.out.println("Finished Table 3");
    }

    private void print2darray(String[][] arr) {
        for (int rowNum = 0; rowNum < arr.length; rowNum++) {    //i counts rows (0th row is column names)
            for (int col = 0; col < arr[0].length; col++) { //col counts columns (0th column is first logical column)
                System.out.println(arr[rowNum][col]);
            }
        }
    }

    @Test
    void checkForItem() {

    }

    @Test
    void getTableData() {
        print2darray(t1.toArray());
        print2darray(t2.toArray());
    }

    @Test
    void testGenColumns() {

    }
}