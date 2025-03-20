/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.personality;

import java.util.List;

/**
 *
 * @author lendle
 */
public class PersonalityPredictionParameters {
    private String id=null;
    private List<String> programmingKeywords=null;
    private List<String> questionKeywords=null;
    private double averageProgrammingKeywords=-1;
    private double averageQuestionKeywords=-1;
    private double averageOtherKeywords=-1;
    private double stdProgrammingKeywords=-1;
    private double stdQuestionKeywords=-1;
    private double stdOtherKeywords=-1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getProgrammingKeywords() {
        return programmingKeywords;
    }

    public void setProgrammingKeywords(List<String> programmingKeywords) {
        this.programmingKeywords = programmingKeywords;
    }

    public List<String> getQuestionKeywords() {
        return questionKeywords;
    }

    public void setQuestionKeywords(List<String> questionKeywords) {
        this.questionKeywords = questionKeywords;
    }

    public double getAverageProgrammingKeywords() {
        return averageProgrammingKeywords;
    }

    public void setAverageProgrammingKeywords(double averageProgrammingKeywords) {
        this.averageProgrammingKeywords = averageProgrammingKeywords;
    }

    public double getAverageQuestionKeywords() {
        return averageQuestionKeywords;
    }

    public void setAverageQuestionKeywords(double averageQuestionKeywords) {
        this.averageQuestionKeywords = averageQuestionKeywords;
    }

    public double getAverageOtherKeywords() {
        return averageOtherKeywords;
    }

    public void setAverageOtherKeywords(double averageOtherKeywords) {
        this.averageOtherKeywords = averageOtherKeywords;
    }

    public double getStdProgrammingKeywords() {
        return stdProgrammingKeywords;
    }

    public void setStdProgrammingKeywords(double stdProgrammingKeywords) {
        this.stdProgrammingKeywords = stdProgrammingKeywords;
    }

    public double getStdQuestionKeywords() {
        return stdQuestionKeywords;
    }

    public void setStdQuestionKeywords(double stdQuestionKeywords) {
        this.stdQuestionKeywords = stdQuestionKeywords;
    }

    public double getStdOtherKeywords() {
        return stdOtherKeywords;
    }

    public void setStdOtherKeywords(double stdOtherKeywords) {
        this.stdOtherKeywords = stdOtherKeywords;
    }
    
    
}
