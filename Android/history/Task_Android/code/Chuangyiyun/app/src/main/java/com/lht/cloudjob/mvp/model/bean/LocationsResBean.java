package com.lht.cloudjob.mvp.model.bean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LocationsResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/8
 */
public class LocationsResBean {

    public static class Province {
        /**
         * name of province
         */
        private String p;

        /**
         * city list
         */
        private ArrayList<City> c;

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public ArrayList<City> getC() {
            return c;
        }

        public void setC(ArrayList<City> c) {
            this.c = c;
        }
    }

    public static class City {
        /**
         * name of city
         */
        private String n;

        /**
         * areas of city
         */
        private ArrayList<Area> a;

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public ArrayList<Area> getA() {
            return a;
        }

        public void setA(ArrayList<Area> a) {
            this.a = a;
        }
    }

    public static class Area {
        /**
         * the name of area
         */
        private String s;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
