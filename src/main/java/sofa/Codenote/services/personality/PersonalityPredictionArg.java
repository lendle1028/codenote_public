/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.personality;

/**
 *
 * @author lendle
 */
public class PersonalityPredictionArg {
    private String author=null;
    private int countQuestionKeywords=0;
    private int countProgrammingKeywords=0;
    private int countOtherKeywords=0;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCountQuestionKeywords() {
        return countQuestionKeywords;
    }

    public void setCountQuestionKeywords(int countQuestionKeywords) {
        this.countQuestionKeywords = countQuestionKeywords;
    }

    public int getCountProgrammingKeywords() {
        return countProgrammingKeywords;
    }

    public void setCountProgrammingKeywords(int countProgrammingKeywords) {
        this.countProgrammingKeywords = countProgrammingKeywords;
    }

    public int getCountOtherKeywords() {
        return countOtherKeywords;
    }

    public void setCountOtherKeywords(int countOtherKeywords) {
        this.countOtherKeywords = countOtherKeywords;
    }

    @Override
    public String toString() {
        return "PersonalityPredictionArg{" + "author=" + author + ", countQuestionKeywords=" + countQuestionKeywords + ", countProgrammingKeywords=" + countProgrammingKeywords + ", countOtherKeywords=" + countOtherKeywords + '}';
    }
    
    
}
