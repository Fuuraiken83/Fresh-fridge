package com.hfad.sqlite3example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class CheckDate {
    static String[] dateAsArray1 = new String[3];
    static String[] dateAsArray2 = new String[3];
    static int[] dateAsInt1 = new int[3];
    static int[] dateAsInt2 = new int[3];
    static Long dateLong1;
    static Long dateLong2;

    static Pair legitDate(String dateAsString1,String dateAsString2){
        Date firstDate;
        Date secondDate;
        try{
            dateAsArray1 = dateAsString1.split("\\.");
            dateAsArray2 = dateAsString2.split("\\.");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            for (int i =0;i<3;i++){
                dateAsInt1[i]=parseInt(dateAsArray1[i]);
                dateAsInt2[i]=parseInt(dateAsArray2[i]);

            }

            if ((dateAsArray1.length == 3 || dateAsArray2.length == 3)
                    && CheckValidDate(dateAsInt1) && CheckValidDate(dateAsInt2)){
                firstDate = dateFormat.parse(dateAsString1);
                secondDate = dateFormat.parse(dateAsString2);
                dateLong1 = firstDate.getTime();
                dateLong2 = secondDate.getTime();

            }
            else {
                dateLong1 = null;
                dateLong2 = null;
            }


            return new Pair<>(dateLong1,dateLong2);

        }
        catch (NumberFormatException | NullPointerException | ParseException err) {
            dateLong1 = null;
            dateLong2 = null;
            Pair<Long,Long> pair = new Pair<>(dateLong1,dateLong2);
            return new Pair<>(dateLong1,dateLong2);
        }


    }

    static boolean CheckSecondOverFirst(Pair dates){
        long first = (Long)dates.getFirst();
        long second = (Long)dates.getSecond();
        return first<=second;
    }

    static boolean CheckValidDate(int[] date){
        if (date[2]>1970){
            switch (date[1]){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                     return (date[0]<=31 && date[0]>0);

                case 4:
                case 6:
                case 9:
                case 11:
                    return (date[0]<=30 && date[0]>0);

                case 2:
                    if (date[2]%400 == 0
                            || (date[2]%100 != 0 && date[2]%4 == 0)){
                        return (date[0]<=29 && date[0]>0);
                    }
                    else{
                        return (date[0]<=28 && date[0]>0);
                    }

                default:
                    return false;
            }


        }
        else return false;

    }

    static class Pair<U, V> {

        /**
         * The first element of this <code>Pair</code>
         */
        private U first;

        /**
         * The second element of this <code>Pair</code>
         */
        private V second;

        /**
         * Constructs a new <code>Pair</code> with the given values.
         *
         * @param first  the first element
         * @param second the second element
         */
        public Pair(U first, V second) {

            this.first = first;
            this.second = second;
        }

        public U getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }

}
