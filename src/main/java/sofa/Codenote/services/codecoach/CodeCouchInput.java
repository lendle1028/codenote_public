/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.codecoach;

import sofa.Codenote.models.JsError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sofa.Codenote.models.CodeErrorBean;
import sofa.Codenote.models.CodeErrorInfoBean;

/**
 *
 * @author USER
 */
public class CodeCouchInput {
    private String projectId=null;
    private List<String> fileFilter=new ArrayList<>();//empty or null means all files
    private List<JsError> errors=new ArrayList<>();

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<String> getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(List<String> fileFilter) {
        this.fileFilter = fileFilter;
    }

    public List<JsError> getErrors() {
        return errors;
    }

    public void setErrors(List<JsError> errors) {
        this.errors = errors;
    }
    
    
    
}
