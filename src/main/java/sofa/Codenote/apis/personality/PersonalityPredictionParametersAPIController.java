/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis.personality;

import org.springframework.beans.factory.annotation.Value;
import sofa.Codenote.services.personality.PersonalityPredictionService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.imsofa.ai.puppychatter.gemini.AbstractGoogleSearchHandler;
import rocks.imsofa.ai.puppychatter.gemini.GoogleSearchFactSource;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.services.personality.PersonalityPredictionArg;
import sofa.Codenote.services.personality.PersonalityPredictionParameters;
import sofa.Codenote.services.personality.PersonalityPredictionParametersManager;
import sofa.Codenote.services.ChatbotService;

/**
 *
 * @author USER
 */
@RestController
public class PersonalityPredictionParametersAPIController {
    @Autowired
    PersonalityPredictionParametersManager manager=null;
    
    @Autowired
    private ChatbotService chatbotService = null;
    
    @Autowired
    private PersonalityPredictionService personalityPredictionService=null;
    
    @Autowired
    ProjectBeanDao projectBeanDao = null;
    @Value("${googleApiKey}")
    private String googleApiKey;
    private String searchEngineId = "27d58ef5bac114aec";
    
    @GetMapping("/api/personalityPrediction/conf/list")
    public List<PersonalityPredictionParameters> list() throws Exception{
        return manager.list();
    }
    
    @DeleteMapping("/api/personalityPrediction/conf/id/{id}")
    public void remove(@PathVariable String id) throws Exception{
        manager.remove(id);
    }
    
    @GetMapping("/api/personalityPrediction/conf/id/{id}")
    public PersonalityPredictionParameters get(@PathVariable String id) throws Exception{
        return manager.get(id);
    }
    
    @PutMapping("/api/personalityPrediction/conf")
    public void save(@RequestBody PersonalityPredictionParameters p) throws Exception{
//        System.out.println(p.getQuestionKeywords().size());
        manager.save(p);
    }
    
    @GetMapping("/api/personalityPrediction/conf/generateProgrammingKeys")
    public List<String> getProgrammingKeys() throws Exception {
        GoogleSearchFactSource javaScriptFactSource = new GoogleSearchFactSource(googleApiKey, searchEngineId, "", 
                "(site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Grammar_and_types OR site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Loops_and_iteration OR site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Functions) javascript", 
                new KeywordCollectorGoogleSearchHandler());
        GoogleSearchFactSource phaserFactSource = new GoogleSearchFactSource(googleApiKey, searchEngineId, "", 
                "(site:https://phaser.io OR site:www.w3schools.com/* OR site:phaser.io/tutorials*) phaser", 
                new KeywordCollectorGoogleSearchHandler());
        String javaScriptSummary=javaScriptFactSource.getSummary();
        String phaserSummary=phaserFactSource.getSummary();
        if(javaScriptSummary.length()>5000){
            javaScriptSummary=javaScriptSummary.substring(0, 5000);
        }
        if(phaserSummary.length()>5000){
            phaserSummary=phaserSummary.substring(0, 5000);
        }
        String summaryJavaScript=chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 你現在是一位 JavaScript 的老師，請根據這些資料，撰寫一份詳細的 JavaScript 教材，這個教材要是詳細的，可以直接當作參考書用的:\r\n" + javaScriptSummary).getMessage();
        String summaryPhaser=chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 你現在是一位 phaser 的老師，請根據這些資料，撰寫一份詳細的 phaser 教材，這個教材要是詳細的，可以直接當作參考書用的:\r\n" + phaserSummary).getMessage();
        String allKeywordsJavaScript = chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 從下列文字中，擷取 200 個與程式撰寫有關且在教科書中會出現的專業用語，包括中英文，以 json 陣列的方式回傳，格式如 ['abc','def','cfg']，不要加上任何說明：\r\n" + summaryJavaScript).getMessage();
        String allKeywordsPhaser = chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 從下列文字中，擷取 200 個與程式撰寫有關且在教科書中會出現的專業用語，包括中英文，以 json 陣列的方式回傳，格式如 ['abc','def','cfg']，不要加上任何說明：\r\n" + summaryPhaser).getMessage();
        Gson gson=new Gson();
        List<String> list1=gson.fromJson(allKeywordsJavaScript, List.class);
        List<String> list2=gson.fromJson(allKeywordsPhaser, List.class);
        Set<String> allKeywords=new HashSet<>();
        for(String str : list1){
            allKeywords.add(str);
        }
        for(String str : list2){
            allKeywords.add(str);
        }
        return new ArrayList<>(allKeywords);
    }
    
    @PostMapping("/api/personalityPrediction/conf/recommendedParameters/projectName/{projectName}")
    public PersonalityPredictionParameters getRecommendedParameters(@RequestBody List<String> keywords, @PathVariable String projectName) throws IOException{
        PersonalityPredictionParameters ret=new PersonalityPredictionParameters();
        ret.setId(projectName);
        System.out.println(keywords);
        ret.setProgrammingKeywords(keywords);
        ret.setQuestionKeywords(new ArrayList<>());
        
        List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname(projectName);
        List<PersonalityPredictionArg> args=personalityPredictionService.cutKeyword(projectBeans, ret);
        DescriptiveStatistics programmingKeywords=new DescriptiveStatistics();
        DescriptiveStatistics questionKeywords=new DescriptiveStatistics();
        DescriptiveStatistics otherKeywords=new DescriptiveStatistics();
        
        for(PersonalityPredictionArg arg : args){
            programmingKeywords.addValue(arg.getCountProgrammingKeywords());
            questionKeywords.addValue(arg.getCountQuestionKeywords());
            otherKeywords.addValue(arg.getCountOtherKeywords());
        }
        
        ret.setAverageProgrammingKeywords(programmingKeywords.getMean());
        ret.setAverageQuestionKeywords(questionKeywords.getMean());
        ret.setAverageOtherKeywords(otherKeywords.getMean());
        
        ret.setStdProgrammingKeywords(programmingKeywords.getStandardDeviation());
        ret.setStdQuestionKeywords(questionKeywords.getStandardDeviation());
        ret.setStdOtherKeywords(otherKeywords.getStandardDeviation());
        
        return ret;
    }
    
    class KeywordCollectorGoogleSearchHandler extends AbstractGoogleSearchHandler {

        @Override
        public boolean shouldIncludeLink(String link) {
            return true;
        }

        @Override
        public boolean shouldGoNextLink(List<String> processedLinks) {
            return processedLinks.size() < 10;
        }

        @Override
        public String summarizePageContent(String html) throws Exception {
            return html;
        }
    }
}
