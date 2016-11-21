package com.cswithandroidproject.ashwani.hangman;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by ashwani on 18/11/16.
 */

public class EvilHangman {

    public TreeSet<String> possibleWords = new TreeSet<String>();
    public String secretWord;
    public int wordLength, guessesLeft;


    //filtering words
    public EvilHangman(ArrayList<String> wordset, int wordLength, int guessesLeft) {
        for(String word : wordset) {
            if(word != null && word.length() == wordLength) {
                possibleWords.add(word);
            }
        }
        secretWord = getSecretWord(wordLength);
        this.guessesLeft = guessesLeft;
    }


    //this function generates the word family the word belongs to
    public String getPattern(String word, char guessedLetter) {
        String regExp = "[^" + guessedLetter + "]";
        return word.replaceAll(regExp, "_");
    }

    //updates possibleWords and updates the pattern/word-family with highest frequency of words
    public TreeMap<String, TreeSet<String>> partitionIntoWordFamily(char guessedLetter) {
        TreeMap<String, TreeSet<String>> wordfamily = new TreeMap<>();
        TreeSet<String> currentSet;
        for(String word : possibleWords) {
            String pattern = getPattern(word, guessedLetter);
            if(wordfamily.containsKey(pattern)) {
                currentSet = wordfamily.get(pattern);
            }
            else {
                currentSet = new TreeSet<String>();
            }
            currentSet.add(word);
            wordfamily.put(pattern, currentSet);
        }
        return wordfamily;
    }


    public boolean guessMade(char guess) {
        TreeMap<String, TreeSet<String>> wordfamily = partitionIntoWordFamily(guess);
        int maxLength = 0;
        String key = "";
        for(String string : wordfamily.keySet()) {
            int length = wordfamily.get(string).size();
            if (length > maxLength) {
                maxLength = length;
                key = string;
            }
        }
        possibleWords = wordfamily.get(key);
        secretWord = getSecretWord(key);
        if(!secretWord.contains(""+guess)) {
            guessesLeft = guessesLeft-1;
            return false;
        }
        else {
            return true;
        }
        //System.out.println(secretWord.trim());
        //for(String i: wordfamily.keySet())System.out.println(i + "  " + wordfamily.get(i).size());
    }


    public String getSecretWord(int letters) {
        String dashes = "";
        for(int i = 0; i < letters-1; i++) {
            dashes += "_ ";
        }
        dashes += "_";
        return dashes;
    }

    public String getSecretWord(String string) {
        String dashes = "";
        int index;
        for(int i = 0; i < string.length()-1; i++) {
            if(i == 0)
                index = i;
            else
                index = 2*i;
            if(secretWord.charAt(index) != '_') {
                dashes += secretWord.charAt(index) + " ";
            }
            else {
                dashes += string.charAt(i) + " ";
            }
        }
        index = 2*(string.length()-1);
        if(secretWord.charAt(index) != '_') {
            dashes += secretWord.charAt(index);
        }
        else {
            dashes += string.charAt(string.length()-1);
        }
        return dashes;
    }
}
