//todo save table to file & sql integration (sqlite easiest)

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

interface Row {
    String[] toStringArr();
}

public class Table {
    private int rows;  //rows of data (+1 to include column row & +1 to include columns letters)
    private int columns; //columns of data (+1 to include row numbers)
    private int actCol; //actual number of columns (including standard output, eg. column heading)
    private int actRow; //actual number of rows (including standard output, eg. row number)
    private ArrayList<ArrayList<String>> tableData; //2d string array //String[] represents a row of data, ArrayList<String[]> represents a table of data
    private int numDecPlaces = 2;
    private static final String fileName = "file";

    /*Constructors*/
    public Table(String[] columnData) {
        setRows(0);
        setColumns(columnData.length);

        this.tableData = new ArrayList<>();
        this.tableData.add(genTableHeader(columnData));  //first row (0th index) is column data ([0][0] is "Row#")
    }

    public Table(Class<?> C) {
        setRows(0);
        setColumns(genColumns(C).length);

        this.tableData = new ArrayList<>();
        this.tableData.add(genTableHeader(genColumns(C)));
    }
    //END Constructors

    /*Table Methods*/
    public void clear() {
        setRows(0); //clear rows
        String[] columnBackup = new String[getActCol() - 1]; //first element of ArrList is column headers String[]

        for (int i = 1; i < getActCol(); i++) {
            columnBackup[i - 1] = getTableData().get(0).get(i);
        }
        this.tableData = new ArrayList<>(); //clear rows
        this.tableData.add(genTableHeader(columnBackup));
    }

    /************
     * Adds a row to the table
     * @param rowData
     * @return
     */
    public boolean addRow(String[] rowData) {
        //check that the row matches columns
        if (rowData.length != getColumns()) {
            JOptionPane.showMessageDialog(null, "Error: rows must contain same number of columns as table. This table has " + getColumns() + " columns. You provided " + rowData.length);
            return false;
        }

        setRows(getRows() + 1); //increment row counters for new data
        ArrayList<String> rowToAdd = new ArrayList<String>();
        rowToAdd.add(""); //first cell in row is empty, for row number, on printing
        rowToAdd.addAll(Arrays.asList(rowData)); //add rest of row to ArrayList
        getTableData().add(rowToAdd); //add the row to the TableData ArrayList

        return true;
    }

    /************
     * Adds multiple rows to the table
     * @param rowData
     * @return
     */
    public void addMultRows(String[][] rowData) {
        for (String[] row : rowData) {
            if (addRow(row)) {
            } else {
                break;
            }
        }
    }

    private int findColumnIndex(String columnName) {
        boolean found = false;

        for (int col = 0; col < getActCol(); col++) {
            if (getTableData().get(0).get(col).equalsIgnoreCase(columnName)) {
                found = true;
                return col;
            }
        }
        if (!found) {
            print("Error, column \"" + columnName + "\" not found");
        }
        throw new Error("Column not found");
//        return -1;
    }

    public boolean sortTable(String sortColumn) {
        int colInd = findColumnIndex(sortColumn);
        if (colInd == -1) {
            return false;
        }

        if (isNumber(getTableData().get(1).get(colInd)) && isNumber(getTableData().get(2).get(colInd))) {
            Collections.sort(getTableData().subList(1, getTableData().size()), new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                    double d1 = Double.parseDouble(o1.get(colInd));
                    double d2 = Double.parseDouble(o2.get(colInd));
                    return Double.compare(d2, d1);
                }
            });
        } else {
            Collections.sort(getTableData().subList(1, getTableData().size()), new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                    return o1.get(colInd).compareTo(o2.get(colInd));
                }
            });
        }
        print("Table sorted by " + sortColumn);
        return true;
    }

    /*********
     * Dynamically generate column names for a specific class
     * Column is detected by looking for _ at beginning of instance fields
     * @param C
     * @return
     */
    public String[] genColumns(Class C) {
        String[] columnHeaders = new String[0];

        for (Field classField : C.getDeclaredFields()) { //get all the fields from the class C
            if (classField.getName().charAt(0) == '_') { //check for starting _
                String fieldName = classField.getName().substring(1); //get the name without the _
                char[] field = fieldName.toCharArray();
                field[0] = Character.toUpperCase(field[0]);

                for (int j = 1; j < field.length; j++) {
                    if (Character.isUpperCase(field[j])) {
                        field = insertChar(field, ' ', j);
                        j++;
                    }
                }
                columnHeaders = addToArray(columnHeaders, String.valueOf(field));
            }
        }
        return columnHeaders;
    }

    private ArrayList<String> genTableHeader(String[] userColumns) {
        int newArrSize = getActCol();
        ArrayList<String> tableColumns = new ArrayList<String>();

        tableColumns.add("Row#");
        tableColumns.addAll(Arrays.asList(userColumns));

        //System.arraycopy(userColumns, 0, tableColumns, 1, userColumns.length);

        return tableColumns;
    }

    /************
     * Allows Table subclasses to update TableData with Row interfaced objects
     * @param rows
     */
    protected void updateTableData(Row[] rows) {
        clear();
        for (Row row : rows) {
            addRow(row.toStringArr());
        }
    }

    protected void updateTableData(ArrayList<? extends Row> rows) {
        clear();
        for (Row row : rows) {
            addRow(row.toStringArr());
        }
    }

    public boolean checkForItem(String searchKey, String searchColumn) {
        boolean found = false;
        int col = findColumnIndex(searchColumn);
        for (int row = 1; row < getActRow(); row++) {
            String cell = getTableData().get(row).get(col);

            if (cell.equalsIgnoreCase(searchKey)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public String lookupCell(String searchKey, String searchColumn, String returnColumn) {
        boolean found = false;
        String returnCell = "";
        int col = findColumnIndex(searchColumn);

        for (int row = 1; row < getActRow(); row++) {
            String cell = getTableData().get(row).get(col);
            if (cell.equalsIgnoreCase(searchKey)) {
                found = true;

                String data = getTableData().get(row).get(findColumnIndex(returnColumn));
                returnCell = (isNumber(data) ? roundD(data) : data);
                break;
            }
        }
        if (!found) {
            print("Error, " + searchColumn + ": \"" + searchKey + "\" not found");
        }

        return returnCell;
    }
    //END Table Methods

    /*Print  and formatting methods*/
    public void printTable(String title) {
        print(title);
        printTable();
    }

    public void printTable() {
        int[] colLengths = findColumnLengths();
        String[] seperators = buildSep(colLengths);

        for (int rowNum = 0; rowNum < getActRow(); rowNum++) {    //i counts rows (0th row is column names)
            getTableData().get(rowNum).set(0, (rowNum != 0 ? String.valueOf(rowNum) : "Row#")); //row number
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
            String cell = getTableData().get(rowNum).get(col);
            String colName = getTableData().get(0).get(col);

            int indq = colName.indexOf('q');
            int indQ = colName.indexOf('Q');
            cell = (indQ + indq > -2 ? cell : (col != 0 ? (isNumber(cell) ? FPtoRegular(cell) : cell) : cell));

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

    //    public void printColumn(String columnName) {
//        int[] colLengths = findColumnLengths();
//        String[] seperators = buildSep(colLengths);
//
//        for (int rowNum = 0; rowNum < getActRow(); rowNum++) {    //i counts rows (0th row is column names)
//            getTableData().get(rowNum).set(0, (rowNum != 0 ? String.valueOf(rowNum) : "Row#")); //row number
//            printRow(rowNum, colLengths, seperators);
//        }
//        pad(1);
//
//        String sepDouble = seperators[0]; // seperator line =====
//        String sepSingle = seperators[1]; // seperator line -----
//
//        StringBuilder row = new StringBuilder();
//        Formatter frmtr = new Formatter(row);
//
//        for (int col = 0; col < colLengths.length; col++) { //col counts columns (0th column is first logical column)
//            String cell = getTableData().get(rowNum).get(col);
//            String colName = getTableData().get(0).get(col);
//
//            int indq = colName.indexOf('q');
//            int indQ = colName.indexOf('Q');
//            cell = (indQ + indq > -2 ? cell : (col != 0 ? (isNumber(cell) ? FPtoRegular(cell) : cell) : cell));
//
//            frmtr.format("|%-" + colLengths[col] + "s", cell);
//        }
//        frmtr.format("|");
//
//        if (rowNum == 0) {
//            print(sepSingle);
//        }
//
//        print(row.toString());
//
//        if (rowNum == 0) {
//            print(sepDouble);
//        } else {
//            print(sepSingle);
//        }
//
//        StringBuilder row = new StringBuilder();
//        Formatter frmtr = new Formatter(row);
//
//        int col = findColumnIndex(columnName);
//        for (int rowNum = 0; rowNum < getActRow(); rowNum++) {    //i counts rows (0th row is column names)
//            frmtr.format("|%-" + findColumnLengths()[col] + "s|", getTableData().get(rowNum).get(col));
//            print(row.toString());
//        }
//    }

    /***********************
     * returns integer array of column
     * lengths for use in layout of the table
     * @return
     */
    private int[] findColumnLengths() {
        int[] widths = new int[getActCol()]; //generates widths for all columns

        for (int col = 0; col < getActCol(); col++) {
            for (int row = 0; row < getActRow(); row++) {
                String cell = getTableData().get(row).get(col);
                int dec = cell.indexOf('.'); // if string contains '.' its a decimal point which is rounded to two places in my algo
                String trueL = dec != -1 ? cell.substring(0, dec + ((dec + numDecPlaces < cell.length() ? numDecPlaces + 1 : numDecPlaces))) : cell;
                trueL = isNumber(cell) ? FPtoRegular(cell) : cell;

                if (trueL.length() > widths[col]) {
                    widths[col] = trueL.length();
                }
            }
        }
        return widths;
    }

    /****************************************
     * Convert floating point number notation to
     * manageable decimals to 2 decimal points and
     * adds a letter for Million/Billion
     * @param cell
     * @return
     */
    private String FPtoRegular(String cell) {
        String[] zeroToABC = {"", "", "", "", "", "", "M", "", "", "B", "", "", "T"};
        //                    0   1   2   3   4   5    6   7   8    9  10   11  12
        String s = cell;
        int indPC = s.indexOf('%'); //index of % symbol, index of -1 means no % symbol present
        double numToD = Double.parseDouble(indPC != -1 ? cell.substring(0, indPC) : cell); //if there is a pc symbol then seperate it out and convert to double, otherwise leave and convert to double
        //this string is a number
        boolean formatted = false;
        int indE = s.indexOf('E'); //Large numbers in double are represented by E numbers 6.123E9 = 6,123,000,000 or 6.123 Billion

        /*codeblock formats large number from style of 2.3*10^9 to 2.3B(illion) */
        if (indE != -1) {
            /*digit is substring which contains significant figures of the number*/
            String digit = s.substring(0, indE);
            /*get the integer of the exponent (digit after E)*/
            int exp = Integer.parseInt(s.substring(indE + 1));
            /* mod 3, as every 3 digits, the english name for the number changes.
             * Mod value is how far off exponent is from next english name
             */
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

    /**
     * Prints a string to the console.
     *
     * @param msg
     */
    private void print(String msg) {
        System.out.println(msg);
    }// END print

    private void calcCentre(int[] colLengths) {
        //this.tableData;
    }
    //END print methods

    /*Utility Methods*/
    /********
     * Utility class that converts a two dimensional
     * ArrayList of type String into a String[][]
     */
    public static String[][] ArrayListto2DArray(ArrayList<ArrayList<String>> table) {
        String[][] arrayTable = new String[table.size()][table.get(0).size()]; //first dimension is actRows, 2nd dimension is actCols

        for (int i = 0; i < table.size(); i++) {
            ArrayList<String> el = table.get(i);
            for (int j = 0; j < el.size(); j++) {
                String cell = el.get(j);
                arrayTable[i][j] = cell;
            }
        }
        return arrayTable;
    }

    /************
     * Returns objects TableData as a 2D array
     * @return
     */
    protected String[][] toArray() {
        return ArrayListto2DArray(getTableData());
    }

    private boolean isNumber(String s) {
        // todo fix somehow...
        try {
            Double.parseDouble(s.substring(0, s.length() - 3)); //substring necessary to remove % sign from percentages which still need rounding
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
        }
        return false;
    }

    /*****************
     * Adds an element to an array, by first
     * increasing the array size, then copying
     * the old data into it and then putting
     * the new data at the end.
     * @param array
     * @param info
     * @return
     */
    private String[] addToArray(String[] array, String info) {
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = info;
        return newArr;
    }

    /*************
     * Inserts a character at a specified point in a char array
     * @param oldChar
     * @param ch
     * @param position
     * @return
     */
    private char[] insertChar(char[] oldChar, char ch, int position) {
        char[] newChar = new char[oldChar.length + 1];
        // p r i c e s s - array
        // 1 2 3 4 5 6 7 - length
        // 0 1 2 3 4 5 6 - index
        // 0 5 6         - caparray
        int index = oldChar.length - position; //2
        System.arraycopy(oldChar, 0, newChar, 0, position);
        newChar[position] = ch;
        System.arraycopy(oldChar, position, newChar, position + 1, index);
        return newChar;
    }
    //END Utility

    /*File IO*/
    public void saveToFile() throws IOException {
        int metaData = getColumns();
        String outputLine = String.valueOf(metaData) + "\n";

        for (ArrayList<String> row : getTableData()) {
            for (int i = 1; i < row.size(); i++) {
                outputLine += row.get(i) + "|";
            }
        }

        PrintWriter saveFile = new PrintWriter(new FileWriter(fileName + ".txt")); // use a PrintWriter to save this data to a
        // .txt file
        saveFile.println(outputLine);
        saveFile.close(); // close the connection to the file so other processes can access it
    }

    /**
     * Reads table data from an external file
     *
     * @return
     * @throws IOException
     */
    public static ArrayList<ArrayList<String>> readFile() throws IOException {
        Scanner read = new Scanner(new FileReader(fileName + ".txt"));
        String metaDelim = "\n";
        read.useDelimiter(Pattern.compile(metaDelim));
        int numCols = Integer.parseInt(read.next());

        String delim = "|";
        read.useDelimiter(Pattern.compile(delim));

        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();

        while (read.hasNext()) {
            ArrayList<String> row = new ArrayList<String>();
            for (int i = 0; i < numCols; i++) {
                row.add(read.next());
            }
            tableData.add(row);
        }
        read.close();

        return tableData;
    }
    //END File IO

    /*Getters and Setters*/
    protected ArrayList<ArrayList<String>> getTableData() {
        if (!this.tableData.isEmpty()) {
            return this.tableData;
        }
        print("returned null");
        throw new NullPointerException("TableData has not been initialised");
//        return null;
    }

    protected ArrayList<ArrayList<String>> getTableRows() {
        if (!this.tableData.isEmpty()) {
            ArrayList<ArrayList<String>> newArrList = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> justRows = new ArrayList<ArrayList<String>>(getTableData().subList(1, getTableData().size()));
            for (ArrayList<String> row : justRows) {
                newArrList.add(new ArrayList<String>(row.subList(1, row.size())));
            }
            return newArrList;
        }
        throw new NullPointerException("TableData has not been initialised");
//        return null;
    }

    /**************
     * Actual number of columns. Includes Row# column
     */
    private int getActCol() {
        return actCol;
    }

    /*****
     * Actual number of rows. Includes headings row
     */
    private int getActRow() {
        return actRow;
    }

    /********
     * Number of data rows, excludes headings
     * @return
     */
    protected int getRows() {
        return this.rows;
    }

    /***********
     * Number of data columns, excludes Row# column
     * @return
     */
    private int getColumns() {
        return this.columns;
    }

    /*************
     * Track number of rows in TableData
     * @param rows
     */
    private void setRows(int rows) {
        this.rows = rows;
        this.actRow = rows + 1;
    }

    /*************
     * Track number of columns in TableData
     * @param columns
     */
    private void setColumns(int columns) {
        this.columns = columns;
        this.actCol = columns + 1;
    }
    //END Getter and Setters
    {
//    /*********************************
//     * Deprecated Sort Algorithm:
//     */
//    public boolean oldSortTable(String sortColumn) {
//        int colIndex = findColumnIndex(sortColumn);
//        if (colIndex == -1) {
//            return false;
//        }
//        boolean changed = true;
//
//        while (changed) {
//            for (int pass = 0; pass < getRows(); pass++) {
//                changed = false;
//                for (int row = 1; row < getRows() - pass; row++) {
//                    int nextRow = row + 1;
//
//                    String s1 = getTableData().get(row).get(colIndex);
//                    String s2 = getTableData().get(nextRow).get(colIndex);
//
//                    changed = compare(s1, s2, row, nextRow);
//                }
//            }
//        }
//        print("Table sorted by " + sortColumn);
//        printTable();
//        return true;
//    }
//
//    private boolean compare(String t1, String t2, int row, int nextRow) {
//        try {
//            double d1 = Double.parseDouble(t1);
//            double d2 = Double.parseDouble(t2);
//            return compareD(d1, d2, row, nextRow);
//        } catch (NumberFormatException e) {
//            t1 = t1.toLowerCase();
//            t2 = t2.toLowerCase();
//            return compareS(t1, t2, row, nextRow);
//        }
//    }
//
//    private boolean compareD(Double d1, Double d2, int row, int nextRow) {
//        int i = 0;
//        boolean cont = true;
////        print("Sorting Doubles");
//        while (cont) {
//            cont = false;
//            if (d1 < d2) {
//                return swap(row, nextRow);
//            }
//        }
//        return false;
//    }
//
//    private boolean compareS(String t1, String t2, int row, int nextRow) {
//        int i = 0;
//        boolean cont = true;
////        print("Sorting Strings");
//        while (cont) {
//            cont = false;
//            if (t1.charAt(i) > t2.charAt(i)) {
//                return swap(row, nextRow);
//            } else if (t1.charAt(i) == t2.charAt(i)) {
//                cont = true;
//                i++;
//            }
//        }
//        return false;
//    }
//
//    private boolean swap(int row, int nextRow) {
////        print("Swapping...");
//        Collections.swap((List) getTableData(), row, nextRow);
//        return true;
//    }
//    //END of Sort Algorithm
    }
}

