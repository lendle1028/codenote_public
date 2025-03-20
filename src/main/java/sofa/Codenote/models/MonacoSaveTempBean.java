/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author s1088
 */
public class MonacoSaveTempBean {
    List<CodeDiffBean> diffArray= new ArrayList<>();
    CodeInfoBean cib;

    public List<CodeDiffBean> getDiffArray() {
        return diffArray;
    }

    public void setDiffArray(List<CodeDiffBean> diffArray) {
        this.diffArray = diffArray;
    }

    public CodeInfoBean getCib() {
        return cib;
    }

    public void setCib(CodeInfoBean cib) {
        this.cib = cib;
    }
    
    
}
