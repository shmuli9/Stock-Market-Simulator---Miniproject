import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    Table t1;
    Table t2;
    String[] cols = {"Name", "Address", "PhoneN."};

    String[] row1 = {"Me", "9 Address", "12345"};
    String[] row2 = {"Apple", "AAPL", "1","1","1","1","1"};

    @BeforeEach
    void setUp() throws ClassNotFoundException{
        t1 = new Table(cols);
        t2 = new Table(Class.forName(Stock.class.getName()));
        t1.addRow(row1);
        t2.addRow(row2);
    }

    @Test
    void t1clear() {
        t1.clear();
        String[] ColumnRow = t1.getTableData().get(0);
        String[] originalCols = new String[ColumnRow.length - 1];
        System.arraycopy(ColumnRow, 1, originalCols, 0, originalCols.length);

        assertArrayEquals(originalCols, cols);
    }

    @Test
    void t2clear(){
        t2.clear();
        String[] cols2 = {"Name", "Symbol", "Price", "Open", "High", "Low", "Market Cap"};
        String[] ColumnRow = t2.getTableData().get(0);
        String[] originalCols = new String[ColumnRow.length - 1];
        System.arraycopy(ColumnRow, 1, originalCols, 0, originalCols.length);

        assertArrayEquals(originalCols, cols2);
    }

    @Test
    void printRow() {
    }

    @Test
    void sortTable() {
    }

    @Test
    void checkForItem() {
    }

    @Test
    void addRow() {

    }

    @Test
    void getTableData() {
    }

    @Test
    void genColumns() {
    }
}