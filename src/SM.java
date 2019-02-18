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
//        if ((isInt(inp) && type.equals(INT)) || (isDouble(inp) && type.equals(DOUBLE)) || (!isDouble(inp) && type.equals(STR))) {
//            return true;
//        } else {
//            //System.out.println("Invalid input, please enter a " + type);
//            return false;
//        }
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

    private static void print(String msg) {
        System.out.println(msg);
    }// END print
}

