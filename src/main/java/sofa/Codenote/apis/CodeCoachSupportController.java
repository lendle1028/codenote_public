/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import com.hankcs.hanlp.HanLP;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.imsofa.ai.puppychatter.Response;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;
import sofa.Codenote.services.ChatbotService;

/**
 *
 * @author lendle
 */
@RestController
public class CodeCoachSupportController {
    @Autowired
    private ChatbotService chatbotService = null;
    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    
    @PostMapping("/ai/codeCoach/createSupplementary/sampleProjectBeanId/{sampleProjectBeanId}")
    public Response createSupplementary(@PathVariable String sampleProjectBeanId, @RequestBody String hint) throws Exception{
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(sampleProjectBeanId).get();
        List<SampleCodeBean> sampleCodeBeans=sampleProjectBean.getSamplecodes();
        Set<String> keywords=new HashSet<>();
        for(SampleCodeBean sampleCodeBean : sampleCodeBeans){
            List<String> keywords4SampleCode=HanLP.extractSummary(sampleCodeBean.getCode_content(), 50);
            keywords.addAll(keywords4SampleCode);
        }
        String prompt="""
                      你是一個教 JavaScript 的教師，現在教授 Phaser 遊戲程式，請根據下列摘要，產生說明概念用的教學網頁，至少500字，以說明爲主，輔以少量程式碼作爲說明，可以給一些外部連結作爲參考資料，使用繁體中文，html 標籤要 escape，不要執行，儘量清楚說明，請用 html 格式產生，以下是相關的摘要：\r\n
                      """.trim()+keywords.toString()+"""
                                                     \r\n。另外請重視這些要求：
                                                     """.trim()+hint;
        Response message=chatbotService.askByText(prompt);
        return message;
    }
    
    @PostMapping("/ai/codeCoach/generateSupplementary/sampleProjectBeanId/{sampleProjectBeanId}")
    public String createLink(@PathVariable String sampleProjectBeanId, @RequestBody String content) throws Exception{
        File home=new File("supplementary");
        File root=new File(home, sampleProjectBeanId);
        if(!root.exists()){
            root.mkdirs();
        }
        File htmlFile=new File(root, ""+System.currentTimeMillis()+".html");
        FileUtils.write(htmlFile, content, "utf-8");
        return "/supplementary/"+root.getName()+"/"+htmlFile.getName();
    }
    
    @PostMapping("/ai/codeCoach/saveSupplementary/sampleProjectBeanId/{sampleProjectBeanId}")
    @CacheEvict("ProjectBeanDao")
    @Transactional
    public void saveSupplementary(@PathVariable String sampleProjectBeanId, @RequestBody String link) throws Exception{
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(sampleProjectBeanId).get();
        sampleProjectBean.setSampleprojectnote_path(link);
        System.out.println("sampleProjectBeanId="+sampleProjectBeanId+":"+sampleProjectBean.getProjectid_ex());
        sampleProjectBeanDao.save(sampleProjectBean);
    }
    
    @PostMapping("/ai/codeCoach/saveInspectionScript/sampleProjectBeanId/{sampleProjectBeanId}")
    @CacheEvict("ProjectBeanDao")
    @Transactional
    public void saveInspectionScript(@PathVariable String sampleProjectBeanId, @RequestBody String script, @RequestParam("inspectionLatency") long inspectionLatency) throws Exception{
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(sampleProjectBeanId).get();
        sampleProjectBean.setInspectionScript(script);
        sampleProjectBean.setInspectionLatency(inspectionLatency);
//        System.out.println("sampleProjectBeanId="+sampleProjectBeanId+":"+sampleProjectBean.getProjectid_ex());
        sampleProjectBeanDao.save(sampleProjectBean);
    }
}
