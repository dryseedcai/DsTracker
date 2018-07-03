package com.dryseed.timecost.utils

import java.lang.reflect.Array

class Log {

    static void setIsDebug(boolean isDebug) {
        //此处如果不加@会导致循环调用
        Log.@isDebug = isDebug
    }

    def static boolean isDebug = false
    def static boolean showHelp = false


    def static info(Object msg) {
        if (!isDebug) return
        try {
            println "====> TimeCost : ${msg}"
        } catch (Exception e) {
        }
    }

    def static logEach(Object... msg) {
        if (!isDebug) return
        print "====> TimeCost : "
        msg.each {
            Object m ->
                try {
                    if (m != null) {
                        if (m.class.isArray()) {
                            print "["
                            def length = Array.getLength(m);
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    def get = Array.get(m, i);
                                    if (get != null) {
                                        print "${get}\t"
                                    } else {
                                        print "null\t"
                                    }
                                }
                            }
                            print "]\t"
                        } else {
                            print "${m}\t"
                        }
                    } else {
                        print "null\t"
                    }
                } catch (Exception e) {
                }
        }
        println ""
    }

}