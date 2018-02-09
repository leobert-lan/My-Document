package com.lht.creationspace.util.numeric;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.numeric
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> NumericalUtils
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/11.
 */

public class NumericalUtils {

    public static String integerFormat(double d) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        return nf.format(d);
    }

    /**
     * @param d number to format
     * @param decimalBit 十进制，小数点后位数
     * @return 格式化后的字符串
     */
    public static String decimalFormat(double d, int decimalBit) {
        DecimalFormat format = new DecimalFormat(newDecimalFormatPattern(decimalBit));
        return format.format(d);
    }

    /**
     * @param d number to format
     * @param mode 整数分割模式
     * @param decimalBit 十进制，小数点后位数
     * @return 格式化后的字符串
     */
    public static String decimalFormat(double d,SplitMode mode, int decimalBit) {
        DecimalFormat format = new DecimalFormat(newDecimalFormatPattern(decimalBit,mode));
        return format.format(d);
    }

    /**
     * @param decimalBit 十进制，小数点后位数
     * @return pattern
     */
    private static String newDecimalFormatPattern(int decimalBit) {
        final String zero = "0";
        final String dot = ".";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(zero);
        if (decimalBit > 0) {
            stringBuilder.append(dot);
        }
        for (int i = 0; i < decimalBit; i++) {
            stringBuilder.append(zero);
        }
        return stringBuilder.toString();
    }

    /**
     * 分隔符模式
     */
    public /*static*/ enum SplitMode {
        THOUSAND("#,###"), MILLION("#,####");
        private final String prefix;

        SplitMode(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * @param decimalBit 十进制，小数点后位数
     * @param mode 整数分隔符模式
     * @return pattern
     */
    private static String newDecimalFormatPattern(int decimalBit, SplitMode mode) {
        final String zero = "0";
        final String dot = ".";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mode.getPrefix());
        if (decimalBit > 0) {
            stringBuilder.append(dot);
        }
        for (int i = 0; i < decimalBit; i++) {
            stringBuilder.append(zero);
        }
        return stringBuilder.toString();
    }
}
