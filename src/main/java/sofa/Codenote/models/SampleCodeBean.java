/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author s1088
 */
@Entity
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING,
    name = "dtype")
@DiscriminatorValue("SampleCodeBean")
public class SampleCodeBean {
    @Id
    String codeid;
    @Lob
    String code_content;
    String code_nmae;
 
    public String getCode_nmae() {
        return code_nmae;
    }

    public void setCode_nmae(String code_nmae) {
        this.code_nmae = code_nmae;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getCode_content() {
        return code_content;
    }

    public void setCode_content(String code_content) {
        this.code_content = code_content;
    }

}
