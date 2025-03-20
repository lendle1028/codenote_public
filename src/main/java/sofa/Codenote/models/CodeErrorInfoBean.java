/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 *
 * @author s1088
 */

@Entity
public class CodeErrorInfoBean {
    @Id
    String errorinfoid;
    String error_linenum;
    @Lob
    String error_reason;
    @Lob
    String error_character;
    @Basic(optional = true)
    String type;
    
//    @ManyToOne
//    private CodeErrorBean codeErrorBean=null;
//
//    public CodeErrorBean getCodeErrorBean() {
//        return codeErrorBean;
//    }
//
//    public void setCodeErrorBean(CodeErrorBean codeErrorBean) {
//        this.codeErrorBean = codeErrorBean;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    public String getErrorinfoid() {
        return errorinfoid;
    }

    public void setErrorinfoid(String errorinfoid) {
        this.errorinfoid = errorinfoid;
    }


    public String getError_linenum() {
        return error_linenum;
    }

    public void setError_linenum(String error_linenum) {
        this.error_linenum = error_linenum;
    }

    public String getError_reason() {
        return error_reason;
    }

    public void setError_reason(String error_reason) {
        this.error_reason = error_reason;
    }

    public String getError_character() {
        return error_character;
    }

    public void setError_character(String error_character) {
        this.error_character = error_character;
    }
    
    
}
