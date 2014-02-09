package com.messedagliavr.messeapp;

import java.util.Date;
import java.util.GregorianCalendar;

public class MyDifferenceFromToday {

        static Date today;
        Date d1;
        long diff;

        public MyDifferenceFromToday(int year,int month, int day){
            d1 = new GregorianCalendar(year, month-1, day).getTime();
            diff = d1.getTime() - today.getTime();
            today = new GregorianCalendar().getTime();
        }

        public MyDifferenceFromToday(int year,int month, int day,int h, int m){
            d1 = new GregorianCalendar(year, month-1, day, h, m).getTime();
            today = new GregorianCalendar().getTime();
            diff = d1.getTime() - today.getTime();
        }

        public long getDiff() {
            return diff;
        }

        public int getDays(long ms){
            return (int) ((ms / (1000*60*60*24)));
        }


        public int getHours(long ms){
            return (int) ((ms / (1000*60*60)) % 24);
        }

        public int getMinutes(long ms){
            return (int) ((ms / (1000*60)) % 60);
        }

        public int getSeconds(long ms){
            return (int) ((ms / 1000) % 60);
        }

        public int getMilliseconds(long ms){
            return (int)ms;
        }
}
