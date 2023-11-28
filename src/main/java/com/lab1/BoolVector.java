package com.lab1;

public class BoolVector {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean null_cmp;
    private final int IMPV_CODE = -1111;
    private final String IMPV_MS = "BoolVector must contain only boolean values.";
    private final int INCMP = -2222;
    private final String EQ_FLAGS_MS = "Boolean vectors must be of equal length.";
    private final String NULL_LEN_MS = "Length cannot be 0";

    private String flags;

    private boolean _is_correct() {
        char[] _array = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_array[i] != '0' & _array[i] != '1') {
                return false;
            }
        }
        return true;
    }

    private boolean _is_flags_null() {
        if (this.flags == null) {
            return true;
        } else {
            return false;
        }
    }

    private static void _EXIT(int error, String msg) {
        System.out.println(msg);
        System.exit(error);
    }

    public Object[] _diff_length(BoolVector other) {
        BoolVector left = (this.length() < other.length()) ? this : other;
        return new Object[]{this.length() == other.length(),
                (left == this ? this : other),
                (left == this ? other : this)
        };
    }

    BoolVector() {
    }

    BoolVector(int[] _int_flags) {
        StringBuilder rs = new StringBuilder();
        for (int j : _int_flags) {
            if (j == 1)
                rs.append('1');
            else if (j == 0)
                rs.append('0');
            else {
                this._EXIT(IMPV_CODE, IMPV_MS);
            }
        }
        this.flags = rs.toString();
    }

    BoolVector(boolean[] _flags) {
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < _flags.length; i++) {
            if (_flags[i])
                rs.append('1');
            else
                rs.append('0');
        }
        this.flags = rs.toString();
    }

    BoolVector(String _flags) {
        this.flags = _flags;
    }

    BoolVector(BoolVector other) {

        this.flags = other.flags;
    }

    BoolVector(int default_length) {
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < default_length; i++) {
            rs.append("1");
        }
        this.flags = rs.toString();
    }

    public void set(int index, boolean _flag) {
        if ((_flag) ^ (this.flag(index)))
            this.flip(index);
    }

    public void setFlags(String _flags) {
        // Setter
        if (!new BoolVector(_flags)._is_correct()) {
            int error_status = 0;
            String r = new StringBuilder(_flags).reverse().toString();
            char[] _arr = r.toCharArray();
            for (int i = 0; i < _flags.length(); i++) {
                int x = _arr[i] - '0';
                error_status += x * (int)java.lang.Math.pow((double)10.0, (double)i);
            }
            this._EXIT(error_status, IMPV_MS);
        }
        else {
            this.flags = _flags;
        }
    }

    public void setFlags(boolean [] _flags) {
        this.flags = new BoolVector(_flags).toString();
    }

    public String toString() {
        // теперь System.out.println(new BoolVector()) печатает как класс Object
        return this.flags;
    }

    public boolean equals(BoolVector other) {
        return (this.flags.equals(other.flags));
    }

    public int[] toIntArray() {
        int[] _int_flags = new int[this.flags.length()];
        char[] _cflags = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_cflags[i] == '0') {
                _int_flags[i] = 0;
            }
            else {
                _int_flags[i] = 1;
            }
        }
        return _int_flags;
    }

    public boolean[] toBoolArray() {
        boolean[] _flags = new boolean[this.flags.length()];
        char[] _cflags = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_cflags[i] == '0') {
                _flags[i] = false;
            }
            else {
                _flags[i] = true;
            }
        }
        return _flags;
    }

    public int length() {
        return this.flags.length();
    }

    public int weight() {
        int count = 0;
        char[] _array = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_array[i] == '1') {
                count++;
            }
        }
        return count;
    }

    public int dist(BoolVector v) throws ArrayIndexOutOfBoundsException {
        int count = 0;
        if (this._is_flags_null() | v._is_flags_null()) {
            return 0;
        }
        char[] _array1 = this.flags.toCharArray();
        char[] _array2 = v.flags.toCharArray();
        try {
            for (int i = 0; i < this.flags.length(); i++) {
                if (_array1[i] != _array2[i]) {
                    count++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // this.Exit(INCMP, INCMP_MS);
            System.out.println(EQ_FLAGS_MS);
        }
        return count;
    }

    public boolean isOpposite(BoolVector other) {
        return this.dist(other) == this.flags.length();
    }

    public boolean isPredecessorTo(BoolVector other) throws NullPointerException {
        try {
            char[] _flags = this.flags.toCharArray();
            char[] _other_flags = other.flags.toCharArray();

            for (int i = 0; i < this.flags.length(); i++) {
                if (_flags[i] > _other_flags[i]) {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Length cannot be 0");
            return false;
        }
        return true;
    }

    public boolean isInheritorTo(BoolVector other) {
        try {
            char[] _flags = this.flags.toCharArray();
            char[] _other_flags = other.flags.toCharArray();

            for (int i = 0; i < this.flags.length(); i++) {
                if (_flags[i] < _other_flags[i]) {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            System.out.println(NULL_LEN_MS);
            return false;
        }
        return true;
    }

    public boolean areComparable(BoolVector other) {
        return this.isPredecessorTo(other) | this.isInheritorTo(other);
    }

    public BoolVector inverse() {
        char[] _flags = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_flags[i] == '0') {
                _flags[i] = '1';
            } else if (_flags[i] == '1') {
                _flags[i] = '0';
            }
        }
        return new BoolVector(java.util.Arrays.toString(_flags));
    }

    public void flip() {
        char[] _cflags = this.flags.toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (_cflags[i] == '0') {
                _cflags[i] = '1';
            } else if (_cflags[i] == '1') {
                _cflags[i] = '0';
            }
        }
        this.flags = new String(_cflags);
    }

    public void flip(int index) {
        char[] _cflags = new StringBuilder(this.flags).toString().toCharArray();
        for (int i = 0; i < this.flags.length(); i++) {
            if (i == index) {
                if (_cflags[i] == '0')
                    _cflags[i] = '1';
                else
                    _cflags[i] = '0';
            }
        }
        this.flags = new String(_cflags);
    }

    public void swap(BoolVector other) {
        String tmp = other.flags;
        other.flags = this.flags;
        this.flags = tmp;
    }

    public void swap(int left, int right) {
        if (this.flag(left) ^ (this.flag(right))) {
            this.flip(left);
            this.flip(right);
        }
    }

    public boolean flag(int index) {
        String rs = new StringBuilder(this.flags).reverse().toString();
        return rs.charAt(index) != '0';
        // return rs.charAt(index) == 0 ? false : true;
    }

    public boolean pop() {
        if (!this.flags.isEmpty()) {
            String s = this.flags;
            this.flags = new StringBuilder(this.flags).deleteCharAt(this.flags.length() - 1).toString();
            return (s.charAt(s.length() - 1) != '0');
        } else {
            return false;
        }
    }

    public BoolVector append(BoolVector other) {
        StringBuilder rs = new StringBuilder().append(other.flags.toCharArray());
        this.flags = rs.toString();
        return new BoolVector(this.flags);
    }

    public void push_back(boolean _flag) {
        this.flags = new StringBuilder(this.flags).append(_flag ? '1' : '0').toString();
    }

    public void erase() {
        this.flags = new String("");
    }

    public void AND(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = x[i] & y[i];

        this.flags = new BoolVector(r).toString();
    }

    public void OR(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = x[i] | y[i];

        this.flags = new BoolVector(r).toString();
    }

    public void XOR(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = x[i] ^ y[i];

        this.flags = new BoolVector(r).toString();
    }

    public void NAND(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = !(x[i] & y[i]);

        this.flags = new BoolVector(r).toString();
    }

    public void NOR(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = !(x[i] | y[i]);

        this.flags = new BoolVector(r).toString();
    }

    public void EQ(BoolVector other) {
        StringBuilder rs = new StringBuilder(((String)(((BoolVector)_diff_length(other)[1]).flags))).reverse();
        if (!((Boolean)_diff_length(other)[0])) {
            for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length()
                    - ((BoolVector)_diff_length(other)[1]).length(); i++)
                rs.append("0");
        }

        boolean[] x = new BoolVector(rs.reverse().toString()).toBoolArray();
        System.out.println(java.util.Arrays.toString(x));
        boolean[] y = ((BoolVector)_diff_length(other)[2]).toBoolArray();
        boolean[] r = new boolean[((BoolVector)_diff_length(other)[2]).length()];
        System.out.println(java.util.Arrays.toString(y));

        for (int i = 0; i < ((BoolVector)_diff_length(other)[2]).length(); i++)
            r[i] = !(x[i] ^ y[i]);

        this.flags = new BoolVector(r).toString();
    }

    public void LSHIFT_RIGHT(int SHIFT) {
        if (this.length() >= SHIFT) {
            StringBuilder _FLAGS = new StringBuilder(this.flags).delete(this.length() - SHIFT, this.length()).reverse();
            for (int i = 0; i < SHIFT; i++) {
                _FLAGS.append("0");
            }
            this.flags = _FLAGS.reverse().toString();
        } else {
            StringBuilder NULL_FLAGS = new StringBuilder();

            for (int i = 0; i < this.length(); i++)
                NULL_FLAGS.append("0");
            this.flags = NULL_FLAGS.toString();
        }
    }

    public void LSHIFT_LEFT(int SHIFT) {
        if (this.length() >= SHIFT) {
            StringBuilder _FLAGS = new StringBuilder(this.flags).delete(0, SHIFT);
            for (int i = 0; i < SHIFT; i++) {
                _FLAGS.append("0");
            }
            this.flags = _FLAGS.toString();
        } else {
            StringBuilder NULL_FLAGS = new StringBuilder();

            for (int i = 0; i < this.length(); i++)
                NULL_FLAGS.append("0");
            this.flags = NULL_FLAGS.toString();
        }
    }

    public void USHIFT_LEFT(int SHIFT) {
        StringBuilder rs = new StringBuilder(this.flags);
        for (int i = 0; i < SHIFT; i++) {
            rs.append("0");
        }
        this.flags = rs.toString();
    }

    public void USHIFT_RIGHT(int SHIFT) {
        StringBuilder rs = new StringBuilder(this.flags).reverse();
        for (int i = 0; i < SHIFT; i++) {
            rs.append("0");
        }
        this.flags = rs.reverse().toString();
    }

    public float[] filter(float[] array) {
        boolean[] filter_list = this.toBoolArray();
        if ((this.weight() == 0) | (array.length != filter_list.length))
            return array;

        float[] new_array = new float[this.weight()];
        int new_array_index = 0;

        for (int i = 0; i < array.length; i++) {
            if (filter_list[i]) {
                new_array[new_array_index] = array[i];
                new_array_index++;
            }
        }
        return new_array;
    }

    public int[] filter(int[] array) {
        boolean[] filter_list = this.toBoolArray();
        if ((this.weight() == 0) | (array.length != filter_list.length))
            return array;

        int[] new_array = new int[this.weight()];
        int new_array_index = 0;

        for (int i = 0; i < array.length; i++) {
            if (filter_list[i]) {
                new_array[new_array_index] = array[i];
                new_array_index++;
            }
        }
        return new_array;
    }
}
