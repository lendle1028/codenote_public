/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.NoteInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author USER
 */
@Controller
public class LoginController {

    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    @Autowired
    private ProjectBeanDao projectBeanDao = null;

    List<String> deeplearnings = List.of("1080307", "1080434", "1083701", "1090332", "1090428");
    List<String> strategiclearnings = List.of("test", "1070440", "1073722", "1090331", "1090438");
    List<String> surfacelearnings = List.of("1080422", "1080440", "1083721", "1090311", "1090325", "1090339");
    List<String> learninglevel = List.of("Deeplearning", "Strategiclearning", "Surfacelearning");

    @GetMapping("/student_login")
    public String LoginAction(Model model) {
        List<SampleProjectBean> sampleProjects = sampleProjectBeanDao.findAll();
        List<String> set = new ArrayList<>();
        
        for (SampleProjectBean s : sampleProjects) {
            set.add(s.getProjectname_ex());
        }
        Collections.sort(set, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        model.addAttribute("pro_name", set);
        return "login";
    }

    @PostMapping("/createproject")
//    @CacheEvict("ProjectBeanDao")
    public String CreateProgect(String stuid, String progectname, HttpServletRequest request, Model model) {
        SampleProjectBean sampleProjectBean = null;
        ProjectBean projectBean = null;
        request.getSession().setAttribute("stuid", stuid);
        int num = (int) (Math.random() * 3) + 1;
//        System.out.println("projectBeanDao.findByPrjectnameAndAuthor");
        List<ProjectBean> pb = projectBeanDao.findByPrjectnameAndAuthor(progectname, stuid);
//        System.out.println("finished projectBeanDao.findByPrjectnameAndAuthor");
        if (pb == null || pb.isEmpty()) {
            if (deeplearnings.contains(stuid)) {
                sampleProjectBean = sampleProjectBeanDao.findByProjectname_exAndSampleproject_learninglevel(progectname, "Deeplearning").get(0);
            } else if (strategiclearnings.contains(stuid)) {
                sampleProjectBean = sampleProjectBeanDao.findByProjectname_exAndSampleproject_learninglevel(progectname, "Strategiclearning").get(0);
            } else if (surfacelearnings.contains(stuid)) {
                sampleProjectBean = sampleProjectBeanDao.findByProjectname_exAndSampleproject_learninglevel(progectname, "Surfacelearning").get(0);
            } else {
                sampleProjectBean = sampleProjectBeanDao.findByProjectname_exAndSampleproject_learninglevel(progectname, learninglevel.get(0)).get(0);
            }
            projectBean=new ProjectBean();
            projectBean.setAuthor(stuid);
            projectBean.setSampleProjectBean(sampleProjectBean);
            List<CodeInfoBean> codeInfoBeans = new ArrayList<>();
            for (SampleCodeBean sampleCodeBean : sampleProjectBean.getSamplecodes()) {
                NoteInfoBean noteInfoBean=new NoteInfoBean();
                noteInfoBean.setNoteid(UUID.randomUUID().toString());
                noteInfoBean.setNote_content("");
                CodeInfoBean codeInfoBean = new CodeInfoBean();
                codeInfoBean.setCode_nmae(sampleCodeBean.getCode_nmae());
                codeInfoBean.setCode_content(sampleCodeBean.getCode_content());
                codeInfoBean.setCodeid(UUID.randomUUID().toString());
                codeInfoBean.setNoteInfoBean(noteInfoBean);
                codeInfoBeans.add(codeInfoBean);
            }
            projectBean.setCodeinfoes(codeInfoBeans);
            projectBean.setProjectid(UUID.randomUUID().toString());
            projectBean.setPrjectname(progectname);
            projectBeanDao.save(projectBean);
            pb.add(projectBean);
        }
        String project_id= pb.get(0).getProjectid();
        //return "redirect:/monaco/project_id/"+project_id;
        //return "redirect:/monacoLoader?projectId="+project_id;
        model.addAttribute("projectId", project_id);
        return "loadingProject";
    }
}
