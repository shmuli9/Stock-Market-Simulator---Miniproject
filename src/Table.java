//todo save table to file & sql integration (sqlite easiest)
//todo switch all arrays in table function to ArrayList at first oppertunity

import javax.swing.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Formatter;

public class Table {
    private int rows;  //rows of data (+1 to include column row & +1 to include columns letters)
    private int columns; //columns of data (+1 to include row numbers)
    private int actCol; //actual number of columns (including standard output, eg. column heading)
    private int actRow; //actual number of rows (including standard output, eg. row number)
    private int oldRows;    //used for maintanance
    private int oldColumns; //used for maintanance
    private boolean hasColumns;
    private String[][] tableData;
    private int numDecPlaces = 2;

    public Table(String[] columnData) {
        setRows(0);
        setColumns(columnData.length);
        setHasColumns(true);

        this.tableData = newTable();
        this.tableData[0] = genTable(columnData);  //first row (0th index) is column data ([0][0] is "Row#")
    }

    public Table(Class<?> C) {
        setRows(0);
        setColumns(genColumns(C).length);
        setHasColumns(false);

        this.tableData = newTable();
        this.tableData[0] = genTable(genColumns(C));
    }

    public void clear() {
        setRows(0); //clear rows
        this.oldRows = 0;
        String[] columnBackup = new String[getActCol() - 1];
        System.arraycopy(getTableData()[0], 1, columnBackup, 0, getActCol() - 1);
        this.tableData = newTable(); //clear rows
        this.tableData[0] = genTable(columnBackup);
    }

    public void printTable(String title) {
        print(title);
        printTable();
    }

    public void printTable() {
        int[] colLengths = findColumnLengths();
        String[] seperators = buildSep(colLengths);

        for (int rowNum = 0; rowNum < getActRow(); rowNum++) {    //i counts rows (0th row is column names)
            getTableData()[rowNum][0] = (rowNum != 0 ? String.valueOf(rowNum) : "Row#"); //row number
            printRow(rowNum, colLengths, seperators);
        }
        pad(1);
    }

    public void printRow(int rowNum, int[] colLengths, String[] seperators) {
        String sepDouble = seperators[0]; // seperator line =====
        String sepSingle = seperators[1]; // seperator line -----

        StringBuilder row = new StringBuilder();
        Formatter frmtr = new Formatter(row);

        for (int col = 0; col < colLengths.length; col++) { //col counts columns (0th column is first logical column)
            String cell = getTableData()[rowNum][col];
            String colName = getTableData()[0][col];

            //cell = col != 0 ? (isNumber(cell) ? fpNotToReg(cell) : cell) : cell;  //column zero is row numbers, no rounding necessary
            int indq = colName.indexOf('q');
            int indQ = colName.indexOf('Q');
            cell = (indQ + indq > -2 ? cell : (col != 0 ? (isNumber(cell) ? fpNotToReg(cell) : cell) : cell));
//            cell = (col != 0) || !(indQ + indq > -2) ? (isNumber(cell) ? fpNotToReg(cell) : cell) : cell;

            frmtr.format("|%-" + colLengths[col] + "s", cell);
        }
        frmtr.format("|");

        if (rowNum == 0) {
            print(sepSingle);
        }

        print(row.toString());

        if (rowNum == 0) {
            print(sepDouble);
        } else {
            print(sepSingle);
        }
    }

    public void printColumn(String columnName) {
        StringBuilder row = new StringBuilder();
        Formatter frmtr = new Formatter(row);

        int col = findColumnIndex(columnName);
        for (int rowNum = 0; rowNum < getActRow(); rowNum++) {    //i counts rows (0th row is column names)
            frmtr.format("|%-" + findColumnLengths()[col] + "s|", getTableData()[rowNum][col]);
            print(row.toString());
        }
    }

    private int findColumnIndex(String columnName) {
        boolean found = false;
        for (int col = 0; col < getActCol(); col++) {
            if (getTableData()[0][col].equalsIgnoreCase(columnName)) {
                found = true;
                return col;
            }
        }
        if (!found) {
            print("Error, column \"" + columnName + "\" not found");
        }
        return -1;
    }

    public boolean sortTable(String sortColumn) {
        int colIndex = findColumnIndex(sortColumn);
        if (colIndex == -1) {
            return false;
        }

        boolean changed = true;

        while (changed) {
            for (int pass = 0; pass < getRows(); pass++) {
                changed = false;
                for (int row = 1; row < getRows() - pass; row++) {
                    int nextRow = row + 1;

                    String s1 = getTableData()[row][colIndex];
                    String s2 = getTableData()[nextRow][colIndex];

                    changed = compare(s1, s2, row, nextRow);
                }
            }
        }
        print("Table sorted by " + sortColumn);
        return true;
    }

    private boolean compare(String t1, String t2, int row, int nextRow) {
        try {
            double d1 = Double.parseDouble(t1);
            double d2 = Double.parseDouble(t2);
            return compareD(d1, d2, row, nextRow);
        } catch (NumberFormatException e) {
            t1 = t1.toLowerCase();
            t2 = t2.toLowerCase();
            return compareS(t1, t2, row, nextRow);
        }
    }

    private boolean compareD(Double d1, Double d2, int row, int nextRow) {
        int i = 0;
        boolean cont = true;
        while (cont) {
            cont = false;
            if (d1 < d2) {
//                print("i = " + i + " : double " + d1 + " < " + d2);
//                print("Switching " + d1 + " with " + d2);
                return swap(row, nextRow);
            }
        }
        return false;
    }

    private boolean swap(int row, int nextRow) {
        String[] temp = getTableData()[row];
        getTableData()[row] = getTableData()[nextRow];
        getTableData()[nextRow] = temp;
        return true;
    }

    private boolean compareS(String t1, String t2, int row, int nextRow) {
        int i = 0;
        boolean cont = true;
        while (cont) {
            cont = false;
            //for (int i = 0; i < Math.min(t1.length(), t2.length()); i++) {
            if (t1.charAt(i) > t2.charAt(i)) {
//                print("i = " + i + " : char " + t1.toLowerCase().charAt(i) + "(" + t1.toLowerCase().codePointAt(i) + ") > " + t2.toLowerCase().charAt(i) + "(" + t2.toLowerCase().codePointAt(i) + ")");
//                print("Switching " + t1 + " with " + t2);
                return swap(row, nextRow);
            } else if (t1.charAt(i) == t2.charAt(i)) {
                cont = true;
                i++;
//                print("i = " + i + " : char " + t1.toLowerCase().charAt(i) + "(" + t1.toLowerCase().codePointAt(i) + ") < " + t2.toLowerCase().charAt(i) + "(" + t2.toLowerCase().codePointAt(i) + ")");
            }
        }
        return false;
    }

    public boolean checkForItem(String searchKey, String searchColumn) {
//        print("checkForItem called...");
        boolean found = false;
        int col = findColumnIndex(searchColumn);

        for (int row = 1; row < getActRow(); row++) {
            String cell = getTableData()[row][col];
            if (cell.equalsIgnoreCase(searchKey)) {
                found = true;
                //print("Item found " + cell);
                break;
            }
        }
        return found;
    }

    public String lookupCell(String searchKey, String searchColumn, String returnColumn) {
//        print("lookupCell called...");
        boolean found = false;
        String returnCell = "";
        int col = findColumnIndex(searchColumn);

        for (int row = 1; row < getActRow(); row++) {
            String cell = getTableData()[row][col];
            if (cell.equalsIgnoreCase(searchKey)) {
                found = true;

                String data = getTableData()[row][findColumnIndex(returnColumn)];
                returnCell = (isNumber(data) ? roundD(data) : data);

//                print("Item found " + cell + ". Data returned: " + returnCell);
                break;
            }
        }
        if (!found) {
            print("Error, " + searchColumn + ": \"" + searchKey + "\" not found");
        }

        return returnCell;
    }

    private boolean isNumber(String s) {
        // todo fix somehow...
        try {
            Double.parseDouble(s.substring(0, s.length() - 3)); //substring necessary to remove % sign from percentages which still need rounding
            return true;
        } catch (NumberFormatException e) {
        } catch (StringIndexOutOfBoundsException a) {
        }
        return false;
    }

    public boolean addRow(String[] rowData) {
        //go to end of table using oldRows&oldColumns, add data there
//        print("addRow called");
        if (rowData.length != getColumns()) {
            JOptionPane.showMessageDialog(null, "Error: rows must contain same number of columns as table. This table has " /*+ getColumns() + " columns. You provided " + rowData.length*/);
            return false;
        }

        resizeTable();
        getTableData()[oldRows][0] = ""; //row number column
        System.arraycopy(rowData, 0, getTableData()[oldRows], 1, rowData.length);
        return true;
    }

    public void addMultRows(String[][] rowData) {
        //go to end of table using oldRows&oldColumns, add data there
        if (rowData[0].length != getColumns()) {
            JOptionPane.showMessageDialog(null, "Error: rows must contain same number of columns as table. This table has " + getColumns() + " columns");
        } else {
            for (int j = 0; j < rowData.length; j++) {
                addRow(rowData[j]);
            }
        }
    }

    private int[] findColumnLengths() {
        int[] widths = new int[getActCol()];

        String[][] temp = getTableData().clone();

        for (int col = 0; col < getActCol(); col++) {
            for (int row = 0; row < getActRow(); row++) {
                String cell = getTableData()[row][col];
                int dec = cell.indexOf('.'); // if string contains '.' its a decimal point which is rounded to two places in my algo
                String trueL = dec != -1 ? cell.substring(0, dec + ((dec + numDecPlaces < cell.length() ? numDecPlaces + 1 : numDecPlaces))) : cell;
                trueL = isNumber(cell) ? fpNotToReg(cell) : cell;

                if (trueL.length() > widths[col]) {
                    widths[col] = trueL.length();
                }
            }
        }
        return widths;
    }

    private String fpNotToReg(String cell) {
        String[] zeroToABC = {"", "", "", "", "", "", "M", "", "", "B", "", "", "T"};
        //                    0   1   2   3   4   5    6   7   8    9  10   11  12
        String s = cell;
        int indPC = s.indexOf('%');

        double numToD = Double.parseDouble(indPC != -1 ? cell.substring(0, indPC) : cell); //this string is a number
        boolean formatted = false;

        int indE = s.indexOf('E');

        if (indE != -1) { //codeblock formats large number from style of 2.3*10^9 to 2.3B(illion)
            String digit = s.substring(0, indE);
            int exp = Integer.parseInt(s.substring(indE + 1));
            int shift = exp % 3;

            if (!(exp == 6 || exp == 9 || exp == 12)) {     //exponent isnt 6,9 or 12, which is m, b or t
                double num = Double.parseDouble(digit) * (Math.pow(10, shift)); //multiply digit to get it to M or B
                exp = exp - shift;    //now exponent is subtracted itself mod 3, so it will get to a number of M or B

                cell = roundD(num) + zeroToABC[exp];
                formatted = true;
            } else { //exp is 6, 9 or 12 so simply need to round and add letter
                cell = roundD(digit) + zeroToABC[exp];
                formatted = true;
            }
        }
        cell = ((!formatted ? roundD(numToD) : cell) + (indPC != -1 ? "%" : ""));
        return cell;
    }

    private String roundD(Double d) {
        BigDecimal roundedNum = new BigDecimal(d).setScale(numDecPlaces, RoundingMode.HALF_UP);
        return roundedNum.toString();
    }

    private String roundD(String s) {
        BigDecimal roundedNum = new BigDecimal(s).setScale(numDecPlaces, RoundingMode.HALF_UP);
        return roundedNum.toString();
    }

    private String[] genTable(String[] userColumns) {
        int newArrSize = getActCol();
        String[] tableColumns = new String[newArrSize];

        tableColumns[0] = "Row#";

        System.arraycopy(userColumns, 0, tableColumns, 1, userColumns.length);

        return tableColumns;
    }

    private void resizeTable() {  //add row to table
        updOldData();
        setRows(getRows() + 1); //increment row counters for new data

        String[][] newTable = newTable(); //create new larger table
        System.arraycopy(getTableData(), 0, newTable, 0, tableData.length);

        this.tableData = newTable;
    }

    private String[][] newTable() {
        String[][] tableData = new String[this.actRow][this.actCol];
        return tableData;
    }

    private void calcCentre(int[] colLengths) {
        //this.tableData;
    }

    private String[] buildSep(int[] colLengths) {
        String sepDouble = "+"; // build seperator line =====
        String sepSingle = "+"; // build seperator line -----

        for (int col = 0; col < colLengths.length; col++) { //col counts columns (0th column is first logical column)
            {
                for (int j = 0; j < colLengths[col]; j++) {
                    sepDouble += "=";
                    sepSingle += "-";
                }
                sepDouble += "+";
                sepSingle += "+";
            }
        }

        String[] seperators = new String[2];
        seperators[0] = sepDouble;
        seperators[1] = sepSingle;

        return seperators;
    }

    private void pad(int num) {
        for (int i = 0; i < num; i++) {
            print("");
        }
    }

    private void updOldData() {
        this.oldRows = getActRow();
        this.oldColumns = getActCol();
    }

    private int getActCol() {
        return actCol;
    }

    private int getActRow() {
        return actRow;
    }

    protected String[][] getTableData() {
        if (this.tableData != null) {
            return this.tableData;
        }
        return null;
    }

    protected int getRows() {
        return this.rows;
    }

    private void setRows(int rows) {
        this.rows = rows;
        this.actRow = rows + 1;
    }

    private int getColumns() {
        return this.columns;
    }

    private void setColumns(int columns) {
        this.columns = columns;
        this.actCol = columns + 1;
    }

    private boolean hasColumns() {
        return hasColumns;
    }

    private void setHasColumns(boolean hasColumns) {
        this.hasColumns = hasColumns;
    }

    /**
     * Prints a string to the console.
     *
     * @param msg
     */
    private void print(String msg) {
        System.out.println(msg);
    }// END print

    private String[] addToArray(String[] array, String info) {
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = info;
        return newArr;
    }

    public String[] genColumns(Class C) {
        Field[] allFields = C.getDeclaredFields();
        String[] columnHeaders = new String[0];

        for (Field classField : allFields) {
            if (classField.getName().charAt(0) == '_') {
                String fieldName = classField.getName().substring(1);
                char[] field = fieldName.toCharArray();
                field[0] = Character.toUpperCase(field[0]);
                int count = 0;

                for (int j = 1; j < fieldName.length(); j++) {
                    if (Character.isUpperCase(fieldName.charAt(j))) {
                        field = insertChar(field, ' ', j + count);
                        count++;
                    }
                }
                columnHeaders = addToArray(columnHeaders, String.copyValueOf(field));
            }
        }
        return columnHeaders;
    }

    private char[] insertChar(char[] chars, char ch, int pos) {
        char[] chrs = new char[chars.length + 1];
        // p r i c e s s - array
        // 1 2 3 4 5 6 7 - length
        // 0 1 2 3 4 5 6 - index
        // 0 5 6         - caparray
        int posi = chars.length - pos; //2
        System.arraycopy(chars, 0, chrs, 0, pos);
        chrs[pos] = ch;
        System.arraycopy(chars, pos, chrs, pos + 1, posi);
        return chrs;
    }
}
