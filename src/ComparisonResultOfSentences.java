package com.cmp3005.project;

import java.util.ArrayList;
import java.util.HashSet;

public class ComparisonResultOfSentences implements Comparable<ComparisonResultOfSentences> { // Objects of this class keeps the reference of the two sentences compared, the similarity rate between them and the plagiarized parts.
    TargetDocSentence targetDocSentence;
    SourceDocSentence sourceDocSentence;
    float similarityRate;
    HashSet<Integer> foundWordsIndexList;
    ArrayList<String> plagiarisedParts;
    
    ComparisonResultOfSentences(){
        foundWordsIndexList = new HashSet<Integer>();
        plagiarisedParts = new ArrayList<String>();
}

    @Override
    public int compareTo(ComparisonResultOfSentences comparisonResultOfSentences) {
        if(this.similarityRate > comparisonResultOfSentences.similarityRate){
            return -1;
        }
        else if(this.similarityRate < comparisonResultOfSentences.similarityRate){
            return 1;
        }
        return 0;
    }
}