package com.example.morsetorch;

import java.util.HashMap;
import java.util.Map;

public class DotDashRepo {
    public static final Map<Character, String> charToDotDashSeqMap = new HashMap<>();

    static {
        charToDotDashSeqMap.put('A', ".-");
        charToDotDashSeqMap.put('B', "-...");
        charToDotDashSeqMap.put('C', "-.-.");
        charToDotDashSeqMap.put('D', "-..");
        charToDotDashSeqMap.put('E', ".");
        charToDotDashSeqMap.put('F', "..-.");
        charToDotDashSeqMap.put('G', "--.");
        charToDotDashSeqMap.put('H', "....");
        charToDotDashSeqMap.put('I', "..");
        charToDotDashSeqMap.put('J', ".---");
        charToDotDashSeqMap.put('K', "-.-");
        charToDotDashSeqMap.put('L', ".-..");
        charToDotDashSeqMap.put('M', "--");
        charToDotDashSeqMap.put('N', "-.");
        charToDotDashSeqMap.put('O', "---");
        charToDotDashSeqMap.put('P', ".--.");
        charToDotDashSeqMap.put('Q', "--.-");
        charToDotDashSeqMap.put('R', ".-.");
        charToDotDashSeqMap.put('S', "...");
        charToDotDashSeqMap.put('T', "-");
        charToDotDashSeqMap.put('U', "..-");
        charToDotDashSeqMap.put('V', "...-");
        charToDotDashSeqMap.put('W', ".--");
        charToDotDashSeqMap.put('X', "-..-");
        charToDotDashSeqMap.put('Y', "-.--");
        charToDotDashSeqMap.put('Z', "--..");

        charToDotDashSeqMap.put('0', "-----");
        charToDotDashSeqMap.put('1', ".----");
        charToDotDashSeqMap.put('2', "..---");
        charToDotDashSeqMap.put('3', "...--");
        charToDotDashSeqMap.put('4', "....-");
        charToDotDashSeqMap.put('5', ".....");
        charToDotDashSeqMap.put('6', "-....");
        charToDotDashSeqMap.put('7', "--...");
        charToDotDashSeqMap.put('8', "---..");
        charToDotDashSeqMap.put('9', "----.");

        charToDotDashSeqMap.put(' ', ".......");
    }
}
