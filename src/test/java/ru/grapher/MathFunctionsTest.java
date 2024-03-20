package ru.grapher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ru.mathparser.MathFunctions;
import ru.mathparser.MathFunctionsDescriptions;

public class MathFunctionsTest {

    @Test
    public void functionMatch() {

        List<String> f1 = new ArrayList<>(MathFunctions.FUNCTION_MAP.keySet());
        List<String> f2 = new ArrayList<>(MathFunctions.REQUIRED_ARGS.keySet());
        List<String> f3 = new ArrayList<>(MathFunctionsDescriptions.get().keySet());

        f1.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));

        f2.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));

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