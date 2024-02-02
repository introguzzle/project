package ru.grapher;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MathFunctionsTest {

    @Test
    public void functionMatch() throws Exception {

        ArrayList<String> f1 = new ArrayList<>(MathFunctions.FUNCTION_MAP.keySet());
        ArrayList<String> f2 = new ArrayList<>(MathFunctions.REQUIRED_ARGS.keySet());
        ArrayList<String> f3 = new ArrayList<>(MathFunctionsDescriptions.DESCRIPTIONS.keySet());

        f1.sort(new Comparator<>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() > o2.length())
                    return -1;
                if (o1.length() == o2.length())
                    return 0;
                else
                    return 1;
            }
        });

        f2.sort(new Comparator<>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() > o2.length())
                    return -1;
                if (o1.length() == o2.length())
                    return 0;
                else
                    return 1;
            }
        });

        System.out.println(f1);
        System.out.println(f2);

        assertEquals(f1.size(), f2.size());
        assertEquals(f2.size(), f3.size());

        assertTrue(f1.containsAll(f2));
        assertTrue(f2.containsAll(f1));

        assertTrue(f2.containsAll(f3));
        assertTrue(f3.containsAll(f2));

        assertTrue(f1.containsAll(f3));
        assertTrue(f3.containsAll(f1));
    }

}