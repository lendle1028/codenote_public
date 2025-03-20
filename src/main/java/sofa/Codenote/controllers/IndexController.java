/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;

/**
 *
 * @author s1088
 */
@Controller
public class IndexController {
    @Autowired
    ProjectBeanDao pbd=null;
    @GetMapping("/")
    public String indexAction(){
//        List<SampleProjectBean> sampleProjectBeans=sampleProjectBeanDao.findAll();
//        Gson gson=new Gson();
//        String json=gson.toJson(sampleProjectBeans);  
//        json=json.replace("{", "__begin__").replace("}", "__end__");
//        System.out.println(json);
//        List list=gson.fromJson(json, List.class);
//        System.out.println(Arrays.deepToString(list.toArray()));
        return "index";
    }
    @GetMapping("/test")
    public String testAction(Model model){
        ProjectBean pb = pbd.findById("01386f1f-23f2-4541-84d9-8130792cfce7").get();
        model.addAttribute("project", pb);
        return "test2";
    }
    
    @GetMapping("/test/corpora")
    public String testCorporaAction(Model model){
        return "test/corpora";
    }
}
