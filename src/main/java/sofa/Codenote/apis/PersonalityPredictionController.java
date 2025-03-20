/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.NoteInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBeanDao;
import sofa.Codenote.services.personality.PersonalityPredictionArg;
import sofa.Codenote.services.personality.PersonalityPredictionParameters;
import sofa.Codenote.services.personality.PersonalityPredictionParametersManager;
import sofa.Codenote.services.personality.PersonalityPredictionResult;
import sofa.Codenote.services.personality.PersonalityPredictionService;

/**
 *
 * @author s1088
 */
@RestController
public class PersonalityPredictionController {

    @Autowired
    ProjectBeanDao projectBeanDao = null;

    @Autowired
    SampleCodeBeanDao sampleCodeBeanDao = null;

    @Autowired
    KeywordChatbot keywordChatbot = null;

    @Autowired
    private PersonalityPredictionParametersManager personalityPredictionManager = null;
    
    @Autowired
    private PersonalityPredictionService personalityPredictionService=null;

    @GetMapping("/getPrediction/profileName/{profileName}/projectName/{projectName}")
    public List<PersonalityPredictionResult> getPredictionRes(@PathVariable String profileName, @PathVariable String projectName) throws Exception {
        projectName = "Phaser Game";
        Map<String, String> predictionRes = new HashMap<>();
        List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname(projectName);
//        keywordChatbot.getProgrammingKeys();
        PersonalityPredictionParameters parameters=personalityPredictionManager.get(profileName);
//        personalityPredictionManager.save(parameters);
        List<PersonalityPredictionArg> args=personalityPredictionService.cutKeyword(projectBeans, personalityPredictionManager.get(projectName));
//        System.out.println(args);
        RenjinScriptEngine engine = new RenjinScriptEngine();
        List<String> ques_keyList=new ArrayList<>();
        List<String> other_keyList=new ArrayList<>();
        for(PersonalityPredictionArg arg :args){
            ques_keyList.add(""+(arg.getCountQuestionKeywords()-parameters.getAverageQuestionKeywords())/parameters.getStdQuestionKeywords());
            other_keyList.add(""+(arg.getCountOtherKeywords()-parameters.getAverageOtherKeywords())/parameters.getStdOtherKeywords());
        }
        engine.eval("library(randomForest)");
        engine.eval("a=readRDS('step1.rds')");
        engine.eval("ques_key=c("+String.join(",", ques_keyList)+")");
        engine.eval("oth_key=c("+String.join(",", other_keyList)+")");
        engine.eval("d=data.frame(ques_key, oth_key)");
        engine.eval("result=predict(a,d)");
        org.renjin.sexp.IntArrayVector array=(org.renjin.sexp.IntArrayVector) engine.get("result");
        double [] results=new double[array.length()];
        array.copyTo(results, 0, results.length);
        List<PersonalityPredictionResult> ret=new ArrayList<>();
        for(int i=0; i<args.size(); i++){
            PersonalityPredictionResult pr=new PersonalityPredictionResult();
            pr.setAuthor(args.get(i).getAuthor());
            pr.setLevelCo((results[i]==1)?"L":"H");
            ret.add(pr);
        }
        System.out.println(Arrays.toString(results));
        return ret;
    }

    @GetMapping("testPreprocessNotes")
    public void testPreprocessNotes(){
        List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname("Phaser Game");
        System.out.println(projectBeans.size());
        for(ProjectBean p : projectBeans){
            List<CodeInfoBean> codeInfoBeans = p.getCodeinfoes();
            System.out.println(p.getAuthor());
            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
                NoteInfoBean noteInfoBean = codeInfoBean.getNoteInfoBean();
                String note = noteInfoBean.getNote_content();
                note = note.replaceAll("(\\s)+", " ").trim();
                if(note.isBlank()==false){
                    System.out.println(note);
                }
            }
//            System.out.println(c.getCode_nmae());
//            System.out.println(c.getNoteInfoBean().getNote_content().trim());
//            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
//                if (codename == null) {
//                    codename = codeInfoBean.getCode_nmae();
//                    code_content = sampleCodeBeanDao.findByCode_nmae(codename).get(0).getCode_content();
//                }
//                NoteInfoBean noteInfoBean = codeInfoBean.getNoteInfoBean();
//                String note = noteInfoBean.getNote_content();
//                if (note.length() > 0 && note.trim().length() > 0) {
//                    System.out.println("note");
//                    note = note.replaceAll("(\\s)+", " ");
//                    notes.add(note);
//                    if (buffer.length() > 0) {
//                        buffer.append("\r\n");
//                    }
//                    buffer.append(note);
//                }
//            }
        }
    }
}
