/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author s1088
 */
@Entity
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING,
    name = "dtype")
@DiscriminatorValue("CodeInfoBean")
public class CodeInfoBean extends SampleCodeBean {
    @OneToOne(cascade = CascadeType.ALL)
    NoteInfoBean noteInfoBean;
    @Column(scale = 20)
    long timestamp=0;

    public NoteInfoBean getNoteInfoBean() {
        return noteInfoBean;
    }

    public void setNoteInfoBean(NoteInfoBean noteInfoBean) {
        this.noteInfoBean = noteInfoBean;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
