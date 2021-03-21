package com.cmp3005.project;

import java.util.ArrayList;

public class SourceDocSentence { // Objects of this class keeps one sentence from the source document, and also keeps some attributes related to the sentence.
    String sentence;
    SubSentence[] subSentenceList; // The list that keeps one subsentence object for each subsentences in the sentence.
    int size; // Size of sentence in terms of the number of words it contains.
    int paragraphNumber; // Keeps the information about in which paragraph the sentence is in the document.
    int sentenceNumber; // Keeps the information about which sentence the sentence is in the paragraph.
    ArrayList<ComparisonResultOfSentences> listOfComparisonResultOfSentences; // The list that keeps one comparisonresultofsentences object for each comparison of the sentence.
    
    SourceDocSentence(){
        listOfComparisonResultOfSentences = new ArrayList<ComparisonResultOfSentences>();
    }
}
