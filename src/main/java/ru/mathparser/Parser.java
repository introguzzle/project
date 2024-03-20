package ru.mathparser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {

    static boolean present(final String expression,
                           final String target) {
        return find(expression, List.of(), 0, expression.length()).contains(target);
    }

    static Set<String> find(final String expression,
                            final List<String> excluded,
                            final int start,
                            final int end) {
        Set<String> result = new HashSet<>();

        int from = Math.max(0, start);
        int to   = Math.min(end, expression.length());

        String  regex   = "(?<![a-zA-Z])\\b\\w\\b(?!\\w)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression.substring(from, to));

        while (matcher.find()) {
            String matched = matcher.group();

            boolean add = true;

            for (String e: excluded) {
                if (matched.equals(e)) {
                    add = false;
                    break;
                }
            }

            if (add && !matched.equals("f") && Character.isLetter(matched.charAt(0)))
                result.add(matched);
        }

        return result;
    }

    static String replace(final String expression,
                          final String key,
                          final double value,
                          final int start,
                          final int end) {
        return replace(expression, Map.of(key, value), start, end);
    }

    public static String replace(final String expression,
                                 final Map<String, Double> map,
                                 final int start,
                                 final int end) {
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(expression);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            int variableStart = matcher.start();

            if (variableStart >= start && variableStart <= end) {
                String variable = matcher.group();

                if (map.containsKey(variable)) {
                    String value = String.valueOf(map.get(variable));
                    matcher.appendReplacement(result, value);
                } else {
                    matcher.appendReplacement(result, variable);
                }
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
