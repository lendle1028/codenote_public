/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author USER
 */
@RestController
public class SampleProjectController {
    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao=null;
    @GetMapping("/api/sampleProjectBean")
    public List<SampleProjectBean> getAllSampleProjectBeans(){
        List<SampleProjectBean> list=sampleProjectBeanDao.findAll();
        Collections.sort(list, new Comparator<SampleProjectBean>(){
            @Override
            public int compare(SampleProjectBean o1, SampleProjectBean o2) {
                return o1.getProjectname_ex().compareTo(o2.getProjectname_ex());
            }
        });
        return list;
    }
    
    @GetMapping("/api/sampleProjectBean/exprot/id/{id}")
    public void exportSampleProjectBean(@PathVariable String id, HttpServletResponse response){
//        System.out.println("id="+id);
        SampleProjectBean sampleProjectBean=sampleProjectBeanDao.findById(id).get();
        Gson gson=new Gson();
        String json=gson.toJson(sampleProjectBean);
        response.setContentType("application/json;charset=utf-8");
        ((HttpServletResponse) response).setHeader("Content-disposition", "attachment; filename="+sampleProjectBean.getProjectname_ex()+".json");
        try (InputStream input = IOUtils.toInputStream(json, "utf-8"); OutputStream output = response.getOutputStream()) {
            IOUtils.copy(input, output);
        } catch (Exception ex) {
            Logger.getLogger(SampleProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
