package com.lht.creationspace.util;

/**
 * <p><b>Package</b> com.lht.vsocyy.util
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> MathUtils
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
public class MathUtils {
    // 递归法求最大公约数
    public static int maxCommonDivisor(int m, int n) {
        if (m < n) {// 保证m>n,若m<n,则进行数据交换
            int temp = m;
            m = n;
            n = temp;
        }
        if (m % n == 0) {// 若余数为0,返回最大公约数
            return n;
        } else { // 否则,进行递归,把n赋给m,把余数赋给n
            return maxCommonDivisor(n, m % n);
        }
    }

    // 循环法求最大公约数
    public static int maxCommonDivisor2(int m, int n) {

        if (m < n) {// 保证m>n,若m<n,则进行数据交换
            int temp = m;
            m = n;
            n = temp;
        }
        while (m % n != 0) {// 在余数不能为0时,进行循环
            int temp = m % n;
            m = n;
            n = temp;
        }
        return n;// 返回最大公约数
    }

    // 求最小公倍数
    public static int minCommonMultiple(int m, int n) {
        return m * n / maxCommonDivisor(m, n);
    }
}
