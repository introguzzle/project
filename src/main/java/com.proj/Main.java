package com.proj;

// import java.util.*;
import java.util.Scanner;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Scanner in = new Scanner(System.in);
        //int x = in.nextInt();
        // System.out.println(j_factorial(x));

        Scanner in = new Scanner(System.in);
        String s = in.next();
        String res = j_delete_char(s, 0);
        // String res2 = Arrays.toString(s.toCharArray());
        System.out.println(j_delete_all_chars(s, '2'));

        in.close();
    }

    public static float j_avg(int a, int b) {

        return ((float)a + (float)b) / (float)2;
    }

    public static int j_factorial(int n) {
        if (n == 0 | n == 1) {
            return 1;
        }
        return n * j_factorial(n - 1);
    }

    public static String j_delete_char(String s, int index) {
        if (index >= s.length()) {
            return s;
        }

        char[] s_array = s.toCharArray();
        char[] ret_s_array = new char[s.length() - 1];

        for (int i = index; i < s.length() - 1; i++) {
            s_array[i] = s_array[i + 1];
        }

        for (int i = 0; i < s.length() - 1; i++) {
            ret_s_array[i] = s_array[i];
        }

        return Arrays.toString(ret_s_array);
    }

    public static String j_delete_all_chars(String s, char to_del) {
        char[] s_array = s.toCharArray();
        int to_del_cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s_array[i] == to_del) {
                to_del_cnt += 1;
            }
        }

        String[] ds = new String[to_del_cnt + 1];
        ds[0] = s;

        for (int StringIndex = 0; StringIndex < to_del_cnt; StringIndex++) {
            char[] ds_char_array = ds[StringIndex].toCharArray();
            int i = 0;
            boolean found_to_del = false;
            while (!found_to_del) {
                if (ds_char_array[i] == to_del) {
                    ds[StringIndex + 1] = j_delete_char(ds[StringIndex], i);
                    found_to_del = true;
                }
                i++;
            }

        }
        return Arrays.toString(ds[to_del_cnt].toCharArray());
    }


//        for (int i = 0; i < dslen; i++) {
//            if (cs[j] != to_del) {
//                new_cs[i] = cs[j];
//            }
//            if (cs[j] == to_del) {
//                j++;
//            }
//            j++;
//        }

//        for (int i = 0; i < s.length(); i++) {
//            if (cs[i] == to_del) {
//                for (int p = i; p < s.length() - 1; p++) {
//                    cs[p] = cs[p + 1];
//                }
//            }
//        }
//        return Arrays.toString(new_cs);
}
