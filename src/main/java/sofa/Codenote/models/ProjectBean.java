/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author s1088
 */
@Entity
public class ProjectBean {

    @Id
    String projectid;
    String prjectname;
    String author;
    String folderId;
    
    

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    List<CodeInfoBean> codeinfoes = null;
    
    @ManyToOne
    private SampleProjectBean sampleProjectBean=null;

    public SampleProjectBean getSampleProjectBean() {
        return sampleProjectBean;
    }

    public void setSampleProjectBean(SampleProjectBean sampleProjectBean) {
        this.sampleProjectBean = sampleProjectBean;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getPrjectname() {
        return prjectname;
    }

    public void setPrjectname(String prjectname) {
        this.prjectname = prjectname;
    }

    public List<CodeInfoBean> getCodeinfoes() {
        return codeinfoes;
    }

    public void setCodeinfoes(List<CodeInfoBean> codeinfoes) {
        this.codeinfoes = codeinfoes;
    }
    
}
