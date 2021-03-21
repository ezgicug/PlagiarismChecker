package com.cmp3005.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class DocAnalyzer implements Runnable {
    String sourceDocPath;
    String sourceDocName;
    String targetDocPath;
    String targetDocName;
    float similarityRateBetweenDocs;
    ArrayList<SourceDocSentence> sourceDocSentenceList; // The list that keeps one sourcedocsentence object for each sentence in the source document.
    ArrayList<TargetDocSentence> targetDocSentenceList; // The list that keeps one targetdocsentence object for each sentence in the target document.
    ArrayList<ComparisonResultOfSentences> listOfComparisonResultOfSentences; // The list that keeps the list of comparisonresultofsentences object of each sourcedocsentence object.
    
    DocAnalyzer(){
        sourceDocSentenceList = new ArrayList<SourceDocSentence>();
        targetDocSentenceList = new ArrayList<TargetDocSentence>();
        listOfComparisonResultOfSentences = new ArrayList<ComparisonResultOfSentences>();
        similarityRateBetweenDocs = 0;
    }
    
    public void readSourceDoc(){ // This function reads the source document and creates one sourcedocsentence object for each sentence in source document.
        Scanner scanner;
        sourceDocName = sourceDocPath.substring(sourceDocPath.lastIndexOf("\\") + 1);
        try {
            scanner = new Scanner(new File(sourceDocPath));
            int paragraphCounter = 0;
            while (scanner.hasNextLine()){
                String lineHolder = scanner.nextLine();
                if(lineHolder.trim() != ""){
                    paragraphCounter++;
                    int sentenceCounter = 0;
                    String[] sentenceArray = lineHolder.split("(?<=[.!?])\\s* ");
                    for(String sentence: sentenceArray){
                        sentenceCounter++;
                        SourceDocSentence sourceDocSentence = new SourceDocSentence();
                        sourceDocSentence.sentence = sentence;
                        sourceDocSentence.paragraphNumber = paragraphCounter;
                        sourceDocSentence.sentenceNumber = sentenceCounter;
                        String[] wordList = sentence.split(" ");
                        sourceDocSentence.size = wordList.length;
                        createSubSentences(wordList, sourceDocSentence);
                        sourceDocSentenceList.add(sourceDocSentence);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException ex) {}
    }
    
    public void readTargetDoc(){ // This function reads the target document and creates one sourcedocsentence object for each sentence in target document.
        Scanner scanner;
        targetDocName = targetDocPath.substring(targetDocPath.lastIndexOf("\\") + 1);
        try {
            scanner = new Scanner(new File(targetDocPath));
            int paragraphCounter = 0;
            while (scanner.hasNextLine()){
                String lineHolder = scanner.nextLine();
                if(lineHolder.trim() != ""){
                    paragraphCounter++;
                    int sentenceCounter = 0;
                    String[] sentenceArray = lineHolder.split("(?<=[.!?])\\s* ");
                    for(String sentence: sentenceArray){
                        sentenceCounter++;
                        TargetDocSentence targetDocSentence = new TargetDocSentence();
                        targetDocSentence.sentence = sentence;
                        targetDocSentence.paragraphNumber = paragraphCounter;
                        targetDocSentence.sentenceNumber = sentenceCounter;
                        String[] wordHolderList = sentence.toLowerCase().split(" ");
                        targetDocSentence.sentenceWithoutSpecialCharacter = " ";
                        for(String word: wordHolderList){
                            targetDocSentence.sentenceWithoutSpecialCharacter += word.replaceAll("[^a-zA-Z0-9_-]", "") + " ";
                        }
                        targetDocSentenceList.add(targetDocSentence);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException ex) {}
    }
    
    public void constructShiftTable(HashMap<Character, Integer> shiftTable, char[] patternLetters){ // This function constructs shift table for Horspool alhorithm.
        shiftTable.clear();
        int patternSize = patternLetters.length;
        for(int i = 0; i < patternSize - 1; i++){
            if(!shiftTable.containsKey(patternLetters[i])){
                shiftTable.put(patternLetters[i], patternSize - i - 1);
            }
            else{
                shiftTable.replace(patternLetters[i], patternSize - i - 1);
            }
        }
    }
    
    public void compare(){ // This function compares all sentences in source document with all sentences in target document.
        char[] sentenceLetters; 
        char[] patternLetters;
        int sentenceSize;
        int patternSize;
        String sentence;
        String pattern;
        int shift;
        int index;
        HashMap<Character, Integer> shiftTable = new HashMap<Character, Integer>();
        for(SourceDocSentence sourceDocSentence: sourceDocSentenceList){
            for(TargetDocSentence targetDocSentence: targetDocSentenceList){
                initializeSentenceSubsetProperties(sourceDocSentence.subSentenceList);
                sentence = targetDocSentence.sentenceWithoutSpecialCharacter;
                sentenceLetters = sentence.toCharArray(); // Splitting the target document sentence into letters to perform the comparison.
                sentenceSize = sentenceLetters.length;
                for(int i = 0; i < sourceDocSentence.subSentenceList.length; i++){
                    if(!sourceDocSentence.subSentenceList[i].isFound){ // Don't compare this subsentence if this subsentence is a subsentence of some other subsentence which matches with the compared sentence.
                        pattern = sourceDocSentence.subSentenceList[i].subSentenceWithoutSpecialCharacter;
                        patternSize = pattern.length();
                        if(patternSize <= sentenceSize){
                            patternLetters = pattern.toCharArray(); // Splitting the source document subsentence into letters to perform the comparison.
                            constructShiftTable(shiftTable, patternLetters);
                            index = patternSize - 1;
                            shift = 0;
                            while(true){
                                if(patternLetters[index] == sentenceLetters[shift + index]){ // Pattern and sentence letters match
                                    if(index == 0){ // If index points to the first letter of pattern, pattern and sentence match.
                                        sourceDocSentence.subSentenceList[i].isFound = true;
                                        eliminatePatternSubsets(sourceDocSentence.subSentenceList, i);
                                        break;
                                    }
                                    else{ // Index does not points to the first letter of pattern.
                                        index--; // In order to compare the letter which is in 1 unit left of the current index, move index 1 unit backward.
                                    }
                                }
                                else{ // Pattern and sentence letters do not match
                                    if(shiftTable.containsKey(sentenceLetters[shift + index])){ // Letter in sentence's current index exists in shift table.
                                        shift += Math.max(1, shiftTable.get(sentenceLetters[shift + index]) - (patternSize - index - 1)); // If letter is on the right side of the current index, move shift 1 unit forward. If letter is on the left side of the current index, move shift forward enough to align the letter in sentence's current index with the letter in the pattern, which found in the shift table(If the letter in the shift table repeats more than once in the pattern, the rightmost letter among the same letters will be taken into account).
                                    }
                                    else{ // Letter in sentence's current index does not exists in shift table.
                                        shift += index + 1; // Move shift forward enough to pass the current index by 1 unit.
                                    }
                                    if(shift > sentenceSize - patternSize){ // If the part of the sentence to the right of the shift's current position, including the shift's current position, is shorter than the pattern, then the matching is unsuccessful.
                                        break;
                                    }
                                    index = patternSize - 1; // Set index so that it points to the last letter of the pattern.
                                }
                            }
                        }
                    }
                }
                calculateSimilarityRateBetweenSentences(sourceDocSentence, targetDocSentence);
            }
        }
    }
    
    public void eliminatePatternSubsets(SubSentence[] subSentenceList, int currentIndex){ // If pattern match with the compared sentence, this function checks if a pattern has a subsentence in subsentencelist.
        for(int j = currentIndex + 1; j < subSentenceList.length; j++){
            if(subSentenceList[currentIndex].leftIndex <= subSentenceList[j].leftIndex && subSentenceList[currentIndex].rightIndex >= subSentenceList[j].rightIndex){
                subSentenceList[j].isFound = true;
                subSentenceList[j].isContained = true;
            }
        }
    }
    
    public void initializeSentenceSubsetProperties(SubSentence[] sentenceSubsetList){
        for(int i = 0; i < sentenceSubsetList.length; i++){
            sentenceSubsetList[i].isFound = false;
            sentenceSubsetList[i].isContained = false;
        }
    }
    
    public void createSubSentences(String[] wordList, SourceDocSentence sourceDocSentence){ // This function creates one subsentence object for each subsentences of sourcedocsentence object.
        ArrayList<SubSentence> subSentenceListHolder = new ArrayList<SubSentence>();
        SubSentence subSentence;
        int wordListSize = wordList.length;
        int counter = 0;
        int firstIndex;
        int lastIndex;
        String tempSentenceSubset;
        String tempSentenceSubsetWithoutSpecialCharacter;
        while(true){
            counter++;
            firstIndex = 0;
            lastIndex = wordListSize - counter;
            while(true){
                subSentence = new SubSentence();
                tempSentenceSubset = "";
                tempSentenceSubsetWithoutSpecialCharacter = " ";
                for(int i = firstIndex; i <= lastIndex; i++){
                    tempSentenceSubset += wordList[i] + " ";
                    tempSentenceSubsetWithoutSpecialCharacter += wordList[i].replaceAll("[^a-zA-Z0-9_-]", "").toLowerCase() + " ";
                    subSentence.subSentenceWordIndexList.add(i);
                }
                subSentence.subSentence = tempSentenceSubset.trim();
                subSentence.subSentenceWithoutSpecialCharacter = tempSentenceSubsetWithoutSpecialCharacter;
                subSentence.size = lastIndex - firstIndex + 1;
                subSentence.leftIndex = firstIndex;
                subSentence.rightIndex = lastIndex;
                subSentenceListHolder.add(subSentence);
                lastIndex++;
                if(lastIndex == wordListSize){
                    break;
                }
                firstIndex++;
            }
            if(counter + 2 == wordListSize){
                break;
            }
        }
        sourceDocSentence.subSentenceList = new SubSentence[subSentenceListHolder.size()];
        for(int i = 0; i < subSentenceListHolder.size(); i++){
            sourceDocSentence.subSentenceList[i] = subSentenceListHolder.get(i);
        }
    }
    
    public void calculateSimilarityRateBetweenSentences(SourceDocSentence sourceDocSentence, TargetDocSentence targetDocSentence){
        ComparisonResultOfSentences comparisonResultOfSentences = new ComparisonResultOfSentences();
        for(SubSentence subSentence: sourceDocSentence.subSentenceList){
            if(subSentence.isFound){
                if(!subSentence.isContained){ // Don't take into account of this subsentence if this subsentence is a subsentence of some other subsentence which matches with the compared sentence.
                    comparisonResultOfSentences.plagiarisedParts.add(subSentence.subSentence);
                    for(Integer wordIndex: subSentence.subSentenceWordIndexList){
                        comparisonResultOfSentences.foundWordsIndexList.add(wordIndex); // Since the hashset does not keep the same value more than once, if the plagiarised part is in more than one subsentence, it will keep the plagiarised part's index only once.
                    }
                }
            }
        }
        comparisonResultOfSentences.similarityRate = (float)comparisonResultOfSentences.foundWordsIndexList.size() / sourceDocSentence.size;
        comparisonResultOfSentences.sourceDocSentence = sourceDocSentence;
        comparisonResultOfSentences.targetDocSentence = targetDocSentence;
        sourceDocSentence.listOfComparisonResultOfSentences.add(comparisonResultOfSentences);
    }
    
    public void calculateSimilarityRateBetweenDocs(){
        HashSet<Integer> foundWordsIndexList = new HashSet<Integer>();
        int paragraphSize = 0;
        int totalEqualityValue = 0;
        for(SourceDocSentence sourceDocSentence: sourceDocSentenceList){
            for(ComparisonResultOfSentences comparisonResultOfSentences: sourceDocSentence.listOfComparisonResultOfSentences){
                for(Integer wordIndex: comparisonResultOfSentences.foundWordsIndexList){
                    foundWordsIndexList.add(wordIndex); // Since the hashset does not keep the same value more than once, if the plagiarised part is in more than one subsentence, it will keep the plagiarised part's index only once.
                }
            }
            totalEqualityValue += foundWordsIndexList.size();
            foundWordsIndexList.clear();
            paragraphSize += sourceDocSentence.size;
        }
        if(paragraphSize == 0){
            paragraphSize = 1;
        }
        similarityRateBetweenDocs = (float)totalEqualityValue / paragraphSize;
    }
    
    public void collectListOfComparisonResultOfSentences(){
        for(SourceDocSentence sourceDocSentence: sourceDocSentenceList){
            for(ComparisonResultOfSentences comparisonResultOfSentences: sourceDocSentence.listOfComparisonResultOfSentences){
                listOfComparisonResultOfSentences.add(comparisonResultOfSentences);
            }
        }
        Collections.sort(listOfComparisonResultOfSentences);
    }

    @Override
    public void run() {
        readSourceDoc();
        readTargetDoc();
        compare();
        collectListOfComparisonResultOfSentences();
        calculateSimilarityRateBetweenDocs();
    }
}