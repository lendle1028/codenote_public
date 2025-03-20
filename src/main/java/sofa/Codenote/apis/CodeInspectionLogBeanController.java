/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeErrorBean;
import sofa.Codenote.models.CodeInspectionLogBean;
import sofa.Codenote.models.CodeInspectionLogBeanDao;
import sofa.Codenote.models.JsError;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;
import sofa.Codenote.ThreadUtils;

/**
 *
 * @author lendle
 */
@RestController
public class CodeInspectionLogBeanController {
    @Autowired
    private CodeInspectionLogBeanDao codeInspectionLogBeanDao=null;
    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao=null;
    @Autowired
    private ProjectBeanDao projectBeanDao=null;
    
    @PostMapping("/api/saveJsErrors/projectId/{projectId}/author/{author}")
    public void save(@RequestBody List<JsError> errors, @PathVariable("projectId") String projectId, @PathVariable("author") String stuId){
        //codeErrorBeanDao.saveAll(JsError.toCodeInspectionLog(projectId, stuId, errors));
        codeInspectionLogBeanDao.save(JsError.toCodeInspectionLog(projectId, stuId, errors));
    }
    
    @GetMapping("/api/loadLintErrors/projectId/{projectId}")
    public CodeInspectionLogBean loadLintErrors(@PathVariable String projectId, HttpServletRequest request){
        HttpSession session=request.getSession();
        ThreadUtils.awaitFinished(session);
        Optional<CodeInspectionLogBean> codeErrorBeanOptional=codeInspectionLogBeanDao.findTopByProjectidOrderByTimestampDesc(projectId);
        List<CodeErrorBean> errorBeans=new ArrayList<>();
        if(codeErrorBeanOptional.isPresent()){
            return codeErrorBeanOptional.get();
        }else{
            return null;
        }
    }
    
    @GetMapping("/api/inspectionLogs/sampleProjectId/{sampleProjectId}")
    public List<CodeInspectionLogBean> findTopLogBySampleProjectId(@PathVariable String sampleProjectId){
        Optional<SampleProjectBean> sampleProjectBeanOptional=sampleProjectBeanDao.findById(sampleProjectId);
        if(sampleProjectBeanOptional.isPresent()){
            SampleProjectBean sampleProjectBean=sampleProjectBeanOptional.get();
            List<ProjectBean> projectBeans=projectBeanDao.findBySampleProjectBean(sampleProjectBean);
            Set<String> ids=new HashSet<>();
            for(ProjectBean p : projectBeans){
                ids.add(p.getProjectid());
            }
            List<CodeInspectionLogBean> list=codeInspectionLogBeanDao.findByProjectidInOrderByProjectidAscTimestampDesc(ids);
            //filter to show only the last one for each projectId
            List<CodeInspectionLogBean> ret=new ArrayList<>();
            String currentProjectId=null;
            for(CodeInspectionLogBean log : list){
                if(log.getProjectid().equals(currentProjectId)==false){
                    ret.add(log);
                    currentProjectId=log.getProjectid();
                }
            }
            return ret;
        }else{
            return new ArrayList<>();
        }
    }
}
