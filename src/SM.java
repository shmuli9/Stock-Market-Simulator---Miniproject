/**************
 * This is a Utility class. It contains
 * classes to validate user input
 */

import java.util.Arrays;
import java.util.Scanner;

public class SM {
    private static Scanner in = new Scanner(System.in);
    final static String STR = "string";
    final static String INT = "int";
    final static String DOUBLE = "number";

    public static String prompt(String prompt, String type) {
        print(prompt);
        String inp = in.nextLine().trim();
        String test = inp.toLowerCase();

        while (!isValid(test, type)){
            print("Invalid input - Expected: " + type);
            print(prompt);
            inp = in.nextLine();
            test = inp.toLowerCase();
        }

        return inp;
    }

    public static String printOptions(String msg, String[] validOptions, String[] exitOptions){
        print(msg);
        String inp = in.nextLine().trim().toLowerCase();

        while (!isValid(inp, validOptions) && (!isValid(inp, exitOptions))){
            print("Invalid input - Expected: " + Arrays.toString(validOptions) + ". Or " + Arrays.toString(exitOptions) + " to exit.");
            print(msg);
            inp = in.nextLine().trim().toLowerCase();
        }

        return String.valueOf(inp.charAt(0));
    }

    public static String printOptions(String msg, String[] validOptions, String exitOption){
        String[] exitOptions = {exitOption};
        return printOptions(msg,validOptions,exitOptions);
    }


    private static boolean isValid(String inp, String[] options) {
        for (String option: options){
            option = option.toLowerCase();
            if (inp.length()>0) {
                if (inp.contains(option) || inp.equalsIgnoreCase(option) || option.startsWith(inp) || inp.startsWith(String.valueOf(option.charAt(0)))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValid(String inp, String type) {
        return ((isInt(inp) && type.equals(INT)) || (isDouble(inp) && type.equals(DOUBLE)) || (!isDouble(inp) && type.equals(STR)));
    }

    private static boolean isInt(String inp) {
        try {
            Integer.parseInt(inp);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String inp) {
        try {
            Double.parseDouble(inp);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void print2darray(String[][] arr) {
        System.out.println("Printing array");
        for (int rowNum = 0; rowNum < arr.length; rowNum++) {    //i counts rows (0th row is column names)
            for (int col = 0; col < arr[0].length; col++) { //col counts columns (0th column is first logical column)
                System.out.println(arr[rowNum][col]);
            }
        }
    }

    private static void print(String msg) {
        System.out.println(msg);
    }// END print
}

