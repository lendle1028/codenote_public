package sofa.Codenote.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author s1088
 */

@Entity
public class CodeErrorBean {
    @Id
    String errorid;
    String jsfile_name;
    @OneToMany(cascade = CascadeType.ALL)
    List<CodeErrorInfoBean> js_errors = null;

    public String getErrorid() {
        return errorid;
    }

    public void setErrorid(String errorid) {
        this.errorid = errorid;
    }

    public List<CodeErrorInfoBean> getJs_errors() {
        return js_errors;
    }

    public void setJs_errors(List<CodeErrorInfoBean> js_errors) {
        this.js_errors = js_errors;
    }

    public String getJsfile_name() {
        return jsfile_name;
    }

    public void setJsfile_name(String jsfile_name) {
        this.jsfile_name = jsfile_name;
    }
    
    
}
