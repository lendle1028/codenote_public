/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleCodeBeanDao;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;
import sofa.Codenote.models.SampleCodeBeanViewModel;

/**
 *
 * @author USER
 */
@Controller
public class CreateSampleCodeController {

    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    @Autowired
    private SampleCodeBeanDao sampleCodeBeanDao = null;

    @GetMapping("/addSampleCode")
    public String addSampleCode(@RequestParam String projectId, Model model) {
        model.addAttribute("projectid_ex", projectId);
        return "addsamplecode";
    }

    @PostMapping("/addSampleCode")
    @CacheEvict("SampleProjectBeanDao")
    public String addSampleCode(SampleCodeBeanViewModel sampleCodeBeanViewModel) {
//        System.out.println(sampleCodeBeanViewModel.getProjectid_ex());
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(sampleCodeBeanViewModel.getProjectid_ex()).get();
        SampleCodeBean sampleCodeBean = new SampleCodeBean();
        sampleCodeBean.setCodeid(UUID.randomUUID().toString());
        sampleCodeBean.setCode_content(sampleCodeBeanViewModel.getCode_content());
        sampleCodeBean.setCode_nmae(sampleCodeBeanViewModel.getCode_nmae());
        sampleProjectBean.getSamplecodes().add(sampleCodeBean);
        sampleProjectBeanDao.save(sampleProjectBean);
        return "redirect:/manageSampleCode?id=" + sampleProjectBean.getProjectid_ex();
    }

    @GetMapping("removeSampleCode")
    public String removeSampleCode(@RequestParam String projectId, @RequestParam String codeId) {
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(projectId).get();
        List<SampleCodeBean> list = sampleProjectBean.getSamplecodes();
        SampleCodeBean tobeRemoved = null;
        for (SampleCodeBean sampleCodeBean : list) {
            if (sampleCodeBean.getCodeid().equals(codeId)) {
                tobeRemoved = sampleCodeBean;
            }
        }
        list.remove(tobeRemoved);
        sampleProjectBeanDao.save(sampleProjectBean);
        return "redirect:/manageSampleCode?id=" + sampleProjectBean.getProjectid_ex();
    }

    @GetMapping("editSampleCode")
    public String showSampleCode(@RequestParam String projectId, @RequestParam String codeId, Model model) {
        SampleCodeBean sampleCodeBean = sampleCodeBeanDao.findById(codeId).get();
        model.addAttribute("sampleCodeBean", sampleCodeBean);
        model.addAttribute("projectId", projectId);
        return "updatesamplecode";
    }

    @PostMapping("editSampleCode")
    public String updateSampleCode(SampleCodeBeanViewModel sampleCodeBeanViewModel) {
        SampleCodeBean sampleCodeBean = new SampleCodeBean();
        sampleCodeBean.setCodeid(sampleCodeBeanViewModel.getCodeid());
        sampleCodeBean.setCode_content(sampleCodeBeanViewModel.getCode_content());
        sampleCodeBean.setCode_nmae(sampleCodeBeanViewModel.getCode_nmae());
        sampleCodeBeanDao.save(sampleCodeBean);
        return "redirect:/manageSampleCode?id=" + sampleCodeBeanViewModel.getProjectid_ex();
    }
    
}
