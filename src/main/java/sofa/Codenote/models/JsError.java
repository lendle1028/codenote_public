/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sofa.Codenote.models.CodeErrorBean;
import sofa.Codenote.models.CodeErrorInfoBean;

/**
 *
 * @author lendle
 */
public class JsError {
    
    private String message = null;
    private String fileName = null;
    private int lineNumber = -1;
    private String type = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * convert to CodeErrorInfoBean instances
     * @param errors
     * @return
     */
    public static CodeInspectionLogBean toCodeInspectionLog(String projectId, String stuId, List<JsError> errors) {
        CodeInspectionLogBean codeInspectionLogBean=new CodeInspectionLogBean();
        codeInspectionLogBean.setId(UUID.randomUUID().toString());
        codeInspectionLogBean.setProjectid(projectId);
        codeInspectionLogBean.setStu_id(stuId);
        codeInspectionLogBean.setTimestamp(System.currentTimeMillis());
        List<CodeErrorBean> codeErrorBeans = new ArrayList<>();
        Map<String, CodeErrorBean> file2CodeErrorBeanMap = new HashMap<>();
        long timestamp = System.currentTimeMillis();
        for (JsError e : errors) {
            String fileName = e.getFileName();
            int startIndex = fileName.lastIndexOf("/") + 1;
            int endIndex = fileName.lastIndexOf("?");
            fileName = (endIndex != -1) ? fileName.substring(startIndex, endIndex) : fileName.substring(startIndex, fileName.length());
            CodeErrorBean codeErrorBean = file2CodeErrorBeanMap.get(fileName);
            if (codeErrorBean == null) {
                codeErrorBean = new CodeErrorBean();
                codeErrorBean.setErrorid(UUID.randomUUID().toString());
                codeErrorBean.setJs_errors(new ArrayList<>());
                codeErrorBean.setJsfile_name(fileName);
                file2CodeErrorBeanMap.put(fileName, codeErrorBean);
                codeErrorBeans.add(codeErrorBean);
            }
            CodeErrorInfoBean codeErrorInfoBean = new CodeErrorInfoBean();
            codeErrorInfoBean.setErrorinfoid(UUID.randomUUID().toString());
            codeErrorInfoBean.setError_linenum("" + e.getLineNumber());
            codeErrorInfoBean.setError_character(e.getMessage());
            codeErrorInfoBean.setError_reason(e.getMessage());
            codeErrorInfoBean.setType(e.getType());
            codeErrorBean.getJs_errors().add(codeErrorInfoBean);
        }
        codeInspectionLogBean.setFiles(codeErrorBeans);
        codeInspectionLogBean.setHasError(codeErrorBeans.size()>0);
        return codeInspectionLogBean;
    }
    
}
