/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleCodeBeanDao;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author USER
 */
@Controller
public class CreateSampleProjectController {
    
    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    @Autowired
    private SampleCodeBeanDao sampleCodeBeanDao = null;
    @Autowired
    private ProjectBeanDao projectBeanDao = null;
    
    @GetMapping("/createsamplecode")
    public String createsampleprojectAction(Model model) {
        List<SampleProjectBean> sampleProjects = sampleProjectBeanDao.findAll();
        Collections.sort(sampleProjects, new Comparator<SampleProjectBean>(){
            @Override
            public int compare(SampleProjectBean o1, SampleProjectBean o2) {
                return o1.getProjectname_ex().compareTo(o2.getProjectname_ex());
            }
        });
        model.addAttribute("sampleProjects", sampleProjects);
        return "createsamplecode";
    }
    
    @PostMapping("/saveSampleProject")
    @CacheEvict("ProjectBeanDao")
    public String saveSampleProject(SampleProjectBean sampleProjectBean) {
        String id = UUID.randomUUID().toString();
        sampleProjectBean.setProjectid_ex(id);
        sampleProjectBeanDao.save(sampleProjectBean);
        return "redirect:/manageSampleCode?id=" + sampleProjectBean.getProjectid_ex();
    }
    
    @GetMapping("/importSampleProject")
    public String importSampleProjectAction(){
        return "importSampleProject";
    }
    
    @PostMapping("/importSampleProject")
    @CacheEvict("SampleProjectBeanDao")
    @Transactional
    public String importSampleProjectPost(@RequestParam("file") MultipartFile file,
            HttpServletResponse response){
        Gson gson=new Gson();
        try(InputStream input=file.getInputStream()){
            String json=IOUtils.toString(input, "utf-8");
            SampleProjectBean sampleProjectBean=gson.fromJson(json, SampleProjectBean.class);
            //reset all ids
            sampleProjectBean.setProjectid_ex(UUID.randomUUID().toString());
            for(SampleCodeBean code : sampleProjectBean.getSamplecodes()){
                code.setCodeid(UUID.randomUUID().toString());
            }
            sampleProjectBeanDao.save(sampleProjectBean);
        } catch (IOException ex) {
            Logger.getLogger(CreateSampleProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "redirect:/createsamplecode";
    }
    
    @GetMapping("/manageSampleCode")
    @Transactional
    public String manageSampleCodeAction(@RequestParam String id, Model model, HttpServletRequest request) {
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(id).get();
        model.addAttribute("sampleProjectBean", sampleProjectBean);
        return "managesamplecode";
    }
    
    @GetMapping("/manageSupplementary")
    public String manageSupplementaryAction(@RequestParam String id, Model model){
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(id).get();
        model.addAttribute("sampleProjectBean", sampleProjectBean);
        return "manageSupplementary";
    }
    
    @GetMapping("/manageInspectionScript")
    public String manageInspectionScriptAction(@RequestParam String id, Model model){
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(id).get();
        model.addAttribute("sampleProjectBean", sampleProjectBean);
        return "manageInspectionScript";
    }
    
    @GetMapping("/removeSampleProject")
    public String removeSampleProject(@RequestParam String id) {
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(id).get();
        List<SampleCodeBean> sampleCodeBeans = sampleProjectBean.getSamplecodes();
        for (SampleCodeBean scb : sampleCodeBeans) {
            sampleCodeBeanDao.delete(scb);
        }
        List<ProjectBean> projectBeans = projectBeanDao.findBySampleProjectBean(sampleProjectBean);
        for (ProjectBean pb : projectBeans) {
            projectBeanDao.delete(pb);
        }
        sampleProjectBeanDao.delete(sampleProjectBean);
        return "redirect:/createsamplecode";
    }
    
}
