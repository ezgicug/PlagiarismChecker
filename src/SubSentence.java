package com.cmp3005.project;

import java.util.ArrayList;

public class SubSentence { // Objects of this class keeps one subsentence of a sentence in the source document, and also keep some attributes related to the subsentence.
    String subSentence;
    String subSentenceWithoutSpecialCharacter; // Special characters are removed from the subsentence to avoid unexpected mismatch during comparison.
    ArrayList<Integer> subSentenceWordIndexList; // List of index number of the words in subsentence.
    int size; // Size of subsentence in terms of the number of words it contains.
    boolean isFound; // If subsentence matches with the compared sentence, isFound assigned to true.
    boolean isContained; // If this subsentence is a subsentence of some other subsentence which matches with the compared sentence, isContained assigned to true.
    int leftIndex; // Index number of the first word in the subset. Note that index numbers start from 0 and increase by 1 for each word from left to right and are determined based on the source sentence.
    int rightIndex; // Index number of the last word in the subset. Note that index numbers start from 0 and increase by 1 for each word from left to right and are determined based on the source sentence.
    
    SubSentence(){
        subSentence = "";
        subSentenceWithoutSpecialCharacter = "";
        subSentenceWordIndexList = new ArrayList<Integer>();
    }
}