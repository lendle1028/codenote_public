/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sofa.Codenote.models.CodeInfoBeanDao;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;

/**
 *
 * @author s1088
 */
@Controller
public class EditorController {

    @Autowired
    ProjectBeanDao pbd = null;
    @Autowired
    CodeInfoBeanDao codeInfoBeanDao=null;

    @GetMapping("/monaco/project_id/{project_id}")
    public String monacoAction(@PathVariable String project_id, Model model) {
        ProjectBean pb = pbd.findById(project_id).get();
        model.addAttribute("project", pb);
        model.addAttribute("projectId", pb.getProjectid());
        return "managecode";
//        return "test2";
    }
    
    @GetMapping("/monacoLoader")
    @Transactional
    public String monacoLoaderAction(@RequestParam String projectId, Model model, HttpServletRequest request){
        ProjectBean pb = pbd.findById(projectId).get();
        model.addAttribute("project", pb);
        model.addAttribute("projectId", projectId);
        model.addAttribute("timestamp", ""+System.currentTimeMillis());
        model.addAttribute("stuid", ""+request.getSession().getAttribute("stuid"));
        return "monaco";
    }
    
     @GetMapping("/editCode")
    public String showCode(@RequestParam String projectId, @RequestParam String codeId, Model model) {
        model.addAttribute("codeid", codeId);
        model.addAttribute("projectId", projectId);
        return "monaco";
    }
    
}
