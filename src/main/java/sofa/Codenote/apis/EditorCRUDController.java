/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeDiffBean;
import sofa.Codenote.models.CodeDiffBeanDao;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.CodeInfoBeanDao;
import sofa.Codenote.models.MonacoSaveTempBean;
import sofa.Codenote.services.PreviewService;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.ThreadUtils;

/**
 *
 * @author s1088
 */
@RestController
public class EditorCRUDController {

    @Autowired
    CodeInfoBeanDao cibd = null;
    @Autowired
    ProjectBeanDao pbd = null;
    @Autowired
    CodeDiffBeanDao cdbd=null;
    
    @Autowired
    PreviewService previewService=null;

    @PostMapping("/savecode")
    public void saveCode(@RequestBody MonacoSaveTempBean monacoSaveTempBean, @RequestParam String projectId, HttpServletRequest request) {
        ProjectBean projectbean = pbd.findById(projectId).get();
        CodeInfoBean codeInfoBean = monacoSaveTempBean.getCib();
        previewService.writeCode2File(projectId, codeInfoBean);
        ThreadUtils.submit(()->{
            for (CodeDiffBean codeDiffBean : monacoSaveTempBean.getDiffArray()) {
                codeDiffBean.setAuthor(projectbean.getAuthor());
                codeDiffBean.setProjectid(projectId);
                codeDiffBean.setCodediffid(UUID.randomUUID().toString());
                cdbd.save(codeDiffBean);
            }
            CodeInfoBean codeInfoBean_o = cibd.findById(codeInfoBean.getCodeid()).get();
            codeInfoBean_o.setCode_content(codeInfoBean.getCode_content());
            codeInfoBean_o.getNoteInfoBean().setNote_content(codeInfoBean.getNoteInfoBean().getNote_content());
            codeInfoBean_o.setTimestamp(System.currentTimeMillis());
            cibd.save(codeInfoBean_o);
//            previewService.checkCodeErrors(projectbean, request);
        });
        
    }
    
    @GetMapping("/codeInfoBean/{codeId}")
    public CodeInfoBean loadCodeInfoBean(@PathVariable String codeId){
        CodeInfoBean codeInfoBean = cibd.findById(codeId).get();
        return codeInfoBean;
    }
}
