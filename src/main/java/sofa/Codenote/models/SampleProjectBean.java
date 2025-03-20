/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

/**
 *
 * @author s1088
 */
@Entity
public class SampleProjectBean {

    @Id
    String projectid_ex;
    String projectname_ex;
    @OneToMany(cascade = CascadeType.ALL)
    List<SampleCodeBean> samplecodes = new ArrayList<>();
    String sampleprojectnote_path;
    String sampleproject_learninglevel;//Deeplearning, Surfacelearning, Strategiclearning
    @Lob
    @Basic(optional = true)
    String inspectionScript;
    
    @Column(columnDefinition="bigint default(5000) ")
    long inspectionLatency=5000;

    public String getSampleproject_learninglevel() {
        return sampleproject_learninglevel;
    }

    public void setSampleproject_learninglevel(String sampleproject_learninglevel) {
        this.sampleproject_learninglevel = sampleproject_learninglevel;
    }

    public String getProjectid_ex() {
        return projectid_ex;
    }

    public void setProjectid_ex(String projectid_ex) {
        this.projectid_ex = projectid_ex;
    }

    public String getSampleprojectnote_path() {
        return sampleprojectnote_path;
    }

    public void setSampleprojectnote_path(String sampleprojectnote_path) {
        this.sampleprojectnote_path = sampleprojectnote_path;
    }

    public String getProjectname_ex() {
        return projectname_ex;
    }

    public void setProjectname_ex(String projectname_ex) {
        this.projectname_ex = projectname_ex;
    }

    public List<SampleCodeBean> getSamplecodes() {
        return samplecodes;
    }

    public void setSamplecodes(List<SampleCodeBean> samplecodes) {
        this.samplecodes = samplecodes;
    }
    
    public String getInspectionScript() {
        return inspectionScript;
    }

    public void setInspectionScript(String inspectionScript) {
        this.inspectionScript = inspectionScript;
    }

    public long getInspectionLatency() {
        return inspectionLatency;
    }

    public void setInspectionLatency(long inspectionLatency) {
        this.inspectionLatency = inspectionLatency;
    }

    
}
