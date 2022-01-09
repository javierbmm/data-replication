package utils;

public class Parser {

    public static int[] valuesFromString(String data) {
        String[] string = data.replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll(" ", "")
                .split(",");

        // declaring an array with the size of string
        int[] values = new int[string.length];

        // parsing the String argument as a signed decimal
        // integer object and storing that integer into the
        // array
        for (int i = 0; i < string.length; i++) {
            values[i] = Integer.parseInt(string[i]);
        }

        return values;
    }

}
