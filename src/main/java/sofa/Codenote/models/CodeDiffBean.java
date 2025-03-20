/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author s1088
 */
@Entity
public class CodeDiffBean {

    @Id
    String codediffid;
    String projectid;
    String author;
    @Lob
    @Column
    String diff;
    String diffSource;
    @Column(scale = 20)
    long timestamp=0;

    public String getCodediffid() {
        return codediffid;
    }

    public void setCodediffid(String codediffid) {
        this.codediffid = codediffid;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getDiffSource() {
        return diffSource;
    }

    public void setDiffSource(String diffSource) {
        this.diffSource = diffSource;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CodeDiffBean{" +", author=" + author + ", diffSource=" + diffSource + ", timestamp=" + timestamp + '}';
    }
    
    
}
