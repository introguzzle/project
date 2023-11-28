package com.lab1;

class InterfacePlantClass {
    interface Subclass {
    }
    interface Redefinable {
        String toString();
        boolean equals(Object obj);
    }
    interface Settable {
        // Setters
        void setFormat(String _FORMAT);
        void setDate(String _pdate);
    }

    interface Gettable {
        // Getters
        int getAge();
        int[] getAdvancedAge();
    }

    interface Action {
        // User interface
        void water(float amount);
        void destroy();
    }
}

public class Plant implements InterfacePlantClass.Settable,
        InterfacePlantClass.Gettable {
    @SuppressWarnings("unused")
    //
    //
    // Private
    //
    //

    private static int[] _getDateArr() {
        String _lct_now = (String)(java.time.LocalDateTime.now().toString());
        String year = new StringBuilder(_lct_now).substring(0, 4);
        String month = new StringBuilder(_lct_now).substring(5, 7);
        String day = new StringBuilder(_lct_now).substring(8, 10);

        return new int[]{java.lang.Integer.parseInt(year), java.lang.Integer.parseInt(month), java.lang.Integer.parseInt(day)};
    }

    private static String _convert(int word_index, int val) {
        if (word_index == 0) {
            if (val < 21) {
                if (val == 0) return new String("");
                if (val == 1) return new String("день");
                if (val < 5) return new String("дня");
                if (val >= 5) return new String("дней");
            }
            else {
                if (val == 21) return new String ("день");
                if (val < 25) return new String("дня");
                if (val >= 25) return new String("дней");
            }
        }
        if (word_index == 1) {
            if (val == 0) return new String("");
            if (val == 1) return new String("месяц");
            if (val < 5) return new String("месяца");
            if (val >= 5) return new String("месяцев");
        }
        if (word_index == 2) {
            if (val == 0) return new String("");
            if ((val >= 5) & (val < 21))
                return new String("лет");
            if (java.lang.Math.floorMod(val, 10) == 1)
                return new String("год");
            if ((java.lang.Math.floorMod(val, 10) < 5) & (java.lang.Math.floorMod(val, 10) != 0))
                return new String("года");
            if ((java.lang.Math.floorMod(val, 10) >= 5) | (java.lang.Math.floorMod(val, 10) == 0))
                return new String("лет");
        }
        return "";
    }

    private void _activateFormat() {
        _FFORMAT = new java.text.SimpleDateFormat(format, java.util.Locale.ENGLISH);
    }

    //
    //
    // Protected
    //
    //

    protected String format = "dd.MM.yyyy";
    protected java.text.SimpleDateFormat _FFORMAT;
    protected String date;


    protected String _getDate() {
        return this.date;
    }

    protected String _getFormat() {
        return this.format;
    }



    //
    //
    // Public
    //
    //

    public Plant() {
    }

    public Plant(String _pdate) {
        this.date = _pdate;
    }

    public Plant(String _pdate, String _FORMAT) {
        this.setFormat(_FORMAT);
        this.date = _pdate;
    }

    public Plant(int _pyear) {
        this.date = java.lang.Integer.toString(_pyear);
    }

    public void setDate(String _pdate) {
        this.date = _pdate;
    }

    public void setFormat(String _FORMAT) {
        this.format = _FORMAT;
    }

    public int getAge() {
        _activateFormat();
        String _lct_now = (String)(java.time.LocalDateTime.now().toString());
        java.util.Calendar clnd = new java.util.GregorianCalendar(_getDateArr()[0], java.util.Calendar.JANUARY, _getDateArr()[2]);
        clnd.add(java.util.Calendar.MONTH, _getDateArr()[1] - 1);
        java.util.Date _now = clnd.getTime();

        try {
            java.util.Date _born = _FFORMAT.parse(this.date);
            java.time.Duration _age = java.time.Duration.between(_born.toInstant(), _now.toInstant());
            return (int)_age.toDays();
        } catch (java.text.ParseException e) {
            e.getCause();
        }
        return 0;
    }

    public int[] getAdvancedAge() {
        int years = java.lang.Math.floorDiv(this.getAge(), (int)365);
        int months = java.lang.Math.floorDiv((this.getAge() - years * (int)365), (int)30);
        int days = this.getAge() - years * (int)365 - months * (int)30;
        return new int[]{days, months, years};
    }

    public String toString() {
        if (!(this instanceof InterfacePlantClass.Subclass)) {
            System.out.println("Interface 'Subclass' must be implemented in " + this.getClass());
            System.exit(-1);
        }

        int days = this.getAdvancedAge()[0];
        int months = this.getAdvancedAge()[1];
        int years = this.getAdvancedAge()[2];

        String d, m, y;

        if (days != 0)
            d = java.lang.Integer.toString(days);
        else
            d = "";

        if (months != 0)
            m = java.lang.Integer.toString(months);
        else
            m = "";

        if (years != 0)
            y = java.lang.Integer.toString(years);
        else
            y = "";

        if ((days == 0) & (months == 0) & (years == 0)) {
            return new String("Возраст: меньше одного дня.");
        }

        return (days == 0) ? (months == 0 ?
                "Возраст: " + y + " " + _convert(2, years) :
                "Возраст: " + m + " " + _convert(1, months) + ", "
                        + y + " " + _convert(2, years)) :
                "Возраст: " + d + " " + _convert(0, days) + ", "
                        + m + " " + _convert(1, months) + ", "
                        + y + " " + _convert(2, years);
        }
}

class Tree extends Plant implements InterfacePlantClass.Action,
        InterfacePlantClass.Redefinable, InterfacePlantClass.Subclass {
    protected String distribution; // Место обитания
    protected String leaf_color; // Цвет листьев
    protected float height; // Высота дерева
    protected float area; // Площадь прикорневого круга

    private static float _toFloat(boolean condition) {
        return condition ? (float)1.0 : (float)0.0;
    }

    private static float _floatMod(float x, float y) {
        if (java.lang.Math.signum(y) == 0) {
            System.out.println("Zero division error");
            System.exit(-2);
        }

        float s = (float)0.0;

        for (float f = (float)0.0;;f = f + (float)1.0) {
            if (s > x) break;
            s += y;
        }

        return x - (s - y);
    }

    private void _init_tree() {
        this.height = (float)(25.0 / 20.0) * this.getAge() / (float)365.0 - this.getAge() / (float)365.0;
        this.area = this.getAge() / (float)182.5 * (float)0.1;
    }

    public Tree() {
        this._init_tree();
    }

    public Tree(String _pdate) {
        super(_pdate);
        this._init_tree();
    }

    public Tree(String _pdate, String _distribution, String _base_color) {
        super(_pdate);
        this.distribution = _distribution;
        this.leaf_color = _base_color;
        this._init_tree();
    }

    public void water(float amount) {
        float pr_height = this.height;
        String str_month = new StringBuilder((java.time.LocalDateTime.now().toString())).substring(5, 7);
        int month = java.lang.Integer.parseInt(str_month);

        float season_multiplier= (float)0.5 + (float)0.5 * _toFloat((month > 4) & (month < 10));

        this.height += (float)(_floatMod((float)(amount * 0.001), (float)(this.area * 20.0 * season_multiplier)));
        System.out.println("\n" + "Дерево было полито. Оно выросло на " + (this.height - pr_height) + " см.");
    }

    public void destroy() {
        this.height = 0;
        System.out.println("Дерево было срублено");
    }

    public String toString() {
        return super.toString() + "\n" + "Место обитания: " + this.distribution
                + "\n" + "Высота дерева: " + this.height + " м."
                + "\n" + "Диаметр ствола: " + java.lang.Math.sqrt(this.area / (float)3.14) + " м."
                + "\n" + "Цвет листьев: " + this.leaf_color;
    }

    public boolean equals(Tree other) {
        return ((this.date.equals(other.date))
                & (this.distribution.equals(other.distribution))
                & (this.leaf_color.equals(other.leaf_color)));
    }
}

class Flower extends Plant implements InterfacePlantClass.Action,
        InterfacePlantClass.Redefinable, InterfacePlantClass.Subclass {
    protected String fclass;
    protected String distribution; // Место обитания
    protected String color; // Цвет листьев

    protected void _init_flower() {

    }

    public Flower() {
        this._init_flower();
    }

    public Flower(String _pdate) {
        super(_pdate);
        this._init_flower();
    }

    public Flower(String _pdate, String _fclass, String _distribution, String _color) {
        super(_pdate);
        this.fclass = _fclass;
        this.distribution = _distribution;
        this.color = _color;
        this._init_flower();
    }

    public void water(float amount) {

    }

    public void destroy() {
        System.out.println("Цветок был вырван");
    }

    public String toString() {
        return super.toString() + "\n" + "Место обитания: " + this.distribution
                + "\n" + "Вид цветка: " + this.fclass
                + "\n" + "Раскрас: " + this.color;
    }

    public boolean equals(Flower other) {
        return ((this.date.equals(other.date))
                & (this.fclass.equals(other.fclass))
                & (this.distribution.equals(other.distribution))
                & (this.color.equals(other.color)));
    }
}
