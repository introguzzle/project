package com.proj;

import com.mathp.MathParser;

public class Main {

    public static void main(String[] args) {

        String function = "f(x) = max(x, A, B) + pow(x, 2)";

        System.out.println(ULTRA_MAGIC_REPLACE(function, 'x', 3));
    }

    public static String ULTRA_MAGIC_REPLACE(String SOURCE, char K, double value) {
        int count = 0;
        StringBuilder _split = new StringBuilder(SOURCE);
        String splitter = "_";

        for (int i = 0; i < SOURCE.length(); i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                count++;
            }
        }

        for (int i = 0; i < count * 2; i++) {
            _split.append(" ");
        }

        for (int i = 0; i < SOURCE.length() + count * 2; i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                _split.insert(i, "y");
            }
        }



        return _split.toString();
    }

}
