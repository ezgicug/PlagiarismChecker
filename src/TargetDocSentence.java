package com.cmp3005.project;

public class TargetDocSentence { // Objects of this class keeps one sentence from the target document, and also keeps some attributes related to the sentence.
    String sentence;
    String sentenceWithoutSpecialCharacter; // Special characters are removed from the sentence to avoid unexpected mismatch during comparison.
    int paragraphNumber; // Keeps the information about in which paragraph the sentence is in the document.
    int sentenceNumber; // Keeps the information about which sentence the sentence is in the paragraph.
    
    TargetDocSentence(){}
}