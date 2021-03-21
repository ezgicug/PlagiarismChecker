package com.cmp3005.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class AppFrame extends JFrame implements Runnable, ActionListener {
    JTextArea sourceDocTextArea;
    JButton sourceDocAddButton;
    JButton sourceDocClearButton;
    String sourceDocPath;
    JTextArea targetDocTextArea;
    JButton targetDocAddButton;
    JButton targetDocClearButton;
    ArrayList<String> targetDocPathList;
    JButton analysisButton;
    JTextArea analysisOutput;
    Thread swingThread;
    ArrayList<Thread> docAnalyzerThreadList;
    Timer timer;
    String processing;
    long start;
    long finish;
    
    AppFrame(){
        super("Plagiarism Detector");
        setSize(550, 500);
        setDefaultCloseOperation(AppFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        sourceDocComponents();
        targetDocComponents();
        analysisButtonAndOutput();
        setTimer();
        targetDocPathList = new ArrayList<String>();
        sourceDocPath = "";
        docAnalyzerThreadList = new ArrayList<Thread>();
        setVisible(true);
    }
    
    public void sourceDocComponents(){
        JLabel sourceDocLabel = new JLabel("Source Document:");
        sourceDocLabel.setBounds(20, 50, 140, 20);
        add(sourceDocLabel);
        sourceDocTextArea = new JTextArea();
        sourceDocTextArea.setEditable(false);
        JScrollPane sourceDocScrollPane = new JScrollPane(sourceDocTextArea);
        sourceDocScrollPane.setBounds(180, 50, 140, 24);
        add(sourceDocScrollPane);
        sourceDocAddButton = new JButton("Add File");
        sourceDocAddButton.setBounds(340, 50, 80, 24);
        sourceDocAddButton.addActionListener(this);
        add(sourceDocAddButton);
        sourceDocClearButton = new JButton("Clear");
        sourceDocClearButton.setBounds(440, 50, 64, 24);
        sourceDocClearButton.addActionListener(this);
        add(sourceDocClearButton);
    }
    
    public void targetDocComponents(){
        JLabel targetDocLabel = new JLabel("Target Documents:");
        targetDocLabel.setBounds(20, 150, 140, 20);
        add(targetDocLabel);
        targetDocTextArea = new JTextArea();
        targetDocTextArea.setEditable(false);
        JScrollPane targetDocScrollPane = new JScrollPane(targetDocTextArea);
        targetDocScrollPane.setBounds(180, 150, 140, 24);
        add(targetDocScrollPane);
        targetDocAddButton = new JButton("Add File");
        targetDocAddButton.setBounds(340, 150, 80, 24);
        targetDocAddButton.addActionListener(this);
        add(targetDocAddButton);
        targetDocClearButton = new JButton("Clear");
        targetDocClearButton.setBounds(440, 150, 64, 24);
        targetDocClearButton.addActionListener(this);
        add(targetDocClearButton);
    }
    
    public void analysisButtonAndOutput(){
        analysisButton = new JButton("Start Analysis");
        analysisButton.setBounds(208, 250, 110, 24);
        analysisButton.addActionListener(this);
        add(analysisButton);
        analysisOutput = new JTextArea();
        analysisOutput.setEditable(false);
        JScrollPane analysisOutputScrollPane = new JScrollPane(analysisOutput);
        analysisOutputScrollPane.setBounds(0, 300, 537, 164);
        add(analysisOutputScrollPane);
    }
    
    public void setTimer(){
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(processing.length() == 14){
                    processing = processing.substring(0, 10);
                    analysisOutput.setText(processing);
                }
                else{
                    analysisOutput.setText(processing);
                    processing += ".";
                }
            }
        });
    }
    
    public boolean isSameNamedFileAddedBeforeToTargetDocList(String filePath){
        for(String targetDocPath: targetDocPathList){
            if(targetDocPath.substring(targetDocPath.lastIndexOf("\\") + 1).equals(filePath.substring(filePath.lastIndexOf("\\") + 1))){
                return true;
            }
        }
        return false;
    }
    public boolean isSameNamedFileAddedBeforeToSourceDoc(String filePath){
        if(filePath.equals(sourceDocPath)){
            return true;
        }
        return false;
    }
    
    public boolean isExtensionAcceptable(String filePath){ // Only .txt files are accepted.
        Boolean extensionAcceptable = false;
        if(filePath.substring(filePath.lastIndexOf(".")).equals(".txt")){
            extensionAcceptable = true;
        }
        return extensionAcceptable;
    }
    
    public void printOutput(ArrayList<DocAnalyzer> docAnalyzerList){
        String[] orderHolder = {"Most", "Second most", "Third most", "Fourth most", "Fifth most"};
        for(DocAnalyzer docAnalyzer: docAnalyzerList){
            analysisOutput.append("-----\n\nSimilarity Rate between " + docAnalyzer.sourceDocName + " and " + docAnalyzer.targetDocName + " is " + docAnalyzer.similarityRateBetweenDocs * 100 + "%\n\n");
            int sentenceCounter = 0;
            for(ComparisonResultOfSentences comparisonResultOfSentences: docAnalyzer.listOfComparisonResultOfSentences){
                if(comparisonResultOfSentences.similarityRate == 0){
                    break;
                }
                else{
                    analysisOutput.append(orderHolder[sentenceCounter] + " similar sentence is \"" + comparisonResultOfSentences.sourceDocSentence.sentence
                                          + "\" in document named " + docAnalyzer.sourceDocName + ", in paragraph " + comparisonResultOfSentences.sourceDocSentence.paragraphNumber
                                          + ", and sentence " + comparisonResultOfSentences.sourceDocSentence.sentenceNumber + ", and it has "
                                          + comparisonResultOfSentences.similarityRate * 100 + "% similarity rate with the sentence \""
                                          + comparisonResultOfSentences.targetDocSentence.sentence + "\" in document named " + docAnalyzer.targetDocName
                                          + ", in paragraph " + comparisonResultOfSentences.targetDocSentence.paragraphNumber
                                          + ", and sentence " + comparisonResultOfSentences.targetDocSentence.sentenceNumber + ". Plagiarised parts: ");
                    int plagiarisedPartsSize = comparisonResultOfSentences.plagiarisedParts.size();
                    int counter = 0;
                    for(String part: comparisonResultOfSentences.plagiarisedParts){
                        counter++;
                        if(counter == plagiarisedPartsSize){
                            analysisOutput.append("\"" + part + "\".\n\n");
                        }
                        else{
                            analysisOutput.append("\"" + part + "\", ");
                        }
                    }
                    sentenceCounter++;
                    if(sentenceCounter == 5){
                        break;
                    }
                }
            }
        }
        analysisOutput.append("-----\n\nElapsed time: " + (finish - start) + "ms");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sourceDocAddButton){
            JFileChooser sourceDocChooser = new JFileChooser();
            if(JFileChooser.APPROVE_OPTION == sourceDocChooser.showOpenDialog(this)){
                File sourceDoc = sourceDocChooser.getSelectedFile();
                String pathHolder = sourceDoc.getPath();
                if(isExtensionAcceptable(pathHolder)){
                    if(!isSameNamedFileAddedBeforeToTargetDocList(pathHolder)){
                        sourceDocPath = pathHolder;
                        sourceDocTextArea.setText(sourceDocPath.substring(sourceDocPath.lastIndexOf("\\") + 1));
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Files with the same names cannot be added again.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this, "File is must be in .txt format.");
                }
            }
        }
        else if(e.getSource() == targetDocAddButton){
            JFileChooser targetDocChooser = new JFileChooser();
            if(JFileChooser.APPROVE_OPTION == targetDocChooser.showOpenDialog(this)){
                File targetDoc = targetDocChooser.getSelectedFile();
                String filePath = targetDoc.getPath();
                if(isExtensionAcceptable(filePath)){
                    if(!isSameNamedFileAddedBeforeToSourceDoc(filePath) && !isSameNamedFileAddedBeforeToTargetDocList(filePath)){
                        targetDocPathList.add(filePath);
                        String listHolder = "";
                        int listSize = targetDocPathList.size();
                        int sizeCounter = 0;
                        for(String targetDocPath: targetDocPathList){
                            sizeCounter++;
                            if(sizeCounter != listSize){
                                listHolder += targetDocPath.substring(targetDocPath.lastIndexOf("\\") + 1) + "\n";
                            }
                            else {
                                listHolder += targetDocPath.substring(targetDocPath.lastIndexOf("\\") + 1);
                            }
                        }
                        targetDocTextArea.setText(listHolder);
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Files with the same names cannot be added again.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this, "File is must be in .txt format.");
                }
            }
        }
        else if(e.getSource() == sourceDocClearButton){
            sourceDocPath = "";
            sourceDocTextArea.setText("");
        }
        else if(e.getSource() == targetDocClearButton){
            targetDocPathList.clear();
            targetDocTextArea.setText("");
        }
        else if(e.getSource() == analysisButton){
            if(sourceDocPath != "" && !targetDocPathList.isEmpty()){
                if(!(swingThread == null) && swingThread.isAlive()){
                    timer.stop();
                    for(Thread threadInstance: docAnalyzerThreadList){
                        threadInstance.stop();
                    }
                    docAnalyzerThreadList.clear();
                    swingThread.stop();
                    sourceDocAddButton.setEnabled(true);
                    sourceDocClearButton.setEnabled(true);
                    targetDocAddButton.setEnabled(true);
                    targetDocClearButton.setEnabled(true);
                    analysisButton.setText("Start Analysis");
                    analysisOutput.setText("");
                }
                else{
                    swingThread = new Thread(this);
                    swingThread.start();
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "Files must be added before analyzing");
            }
        }
    }
    
    public void analysisStarted(){
        sourceDocAddButton.setEnabled(false);
        sourceDocClearButton.setEnabled(false);
        targetDocAddButton.setEnabled(false);
        targetDocClearButton.setEnabled(false);
        analysisButton.setText("Stop Analysis");
        processing = "Processing";
        timer.start();
    }
    
    public void analysisEnded(ArrayList<DocAnalyzer> docAnalyzerList){
        timer.stop();
        analysisOutput.setText("");
        printOutput(docAnalyzerList);
        sourceDocAddButton.setEnabled(true);
        sourceDocClearButton.setEnabled(true);
        targetDocAddButton.setEnabled(true);
        targetDocClearButton.setEnabled(true);
        analysisButton.setText("Start Analysis");
    }

    @Override
    public void run() {
        analysisStarted();
        
        DocAnalyzer docAnalyzer;
        Thread thread;
        ArrayList<DocAnalyzer> docAnalyzerList = new ArrayList<DocAnalyzer>();
        for(String targetDocPath: targetDocPathList){
            docAnalyzer = new DocAnalyzer();
            docAnalyzer.sourceDocPath = sourceDocPath;
            docAnalyzer.targetDocPath = targetDocPath;
            thread = new Thread(docAnalyzer);
            thread.start(); // Use threads for each target document which compared to source document.
            docAnalyzerThreadList.add(thread);
            docAnalyzerList.add(docAnalyzer);
        }
        start = System.currentTimeMillis();
        for(Thread threadInstance: docAnalyzerThreadList){ // Wait for all threads to finish the executing their run method.
            try {
                threadInstance.join();
            } catch (InterruptedException ex) {}
        }
        finish = System.currentTimeMillis();
        printOutput(docAnalyzerList);
        analysisEnded(docAnalyzerList);
    }
}