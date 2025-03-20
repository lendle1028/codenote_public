/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.services.personality;

import sofa.Codenote.apis.KeywordChatbot;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.NoteInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBeanDao;

/**
 *
 * @author s1088
 */

@Service
public class PersonalityPredictionService {

    @Autowired
    ProjectBeanDao projectBeanDao = null;

    @Autowired
    SampleCodeBeanDao sampleCodeBeanDao = null;

    @Autowired
    KeywordChatbot keywordChatbot = null;

    @Autowired
    private PersonalityPredictionParametersManager personalityPredictionManager = null;

    public List<PersonalityPredictionArg> cutKeyword(List<ProjectBean> projectBeans, PersonalityPredictionParameters parameters) throws IOException {
        //setup CustomDictionary and stop words
        CustomDictionary.reload();
        for (String key : parameters.getProgrammingKeywords()) {
            CustomDictionary.add(key);
        }
        
        if (parameters.getQuestionKeywords() == null || parameters.getQuestionKeywords().isEmpty()) {
            //generate question keywords
            StringBuffer codeContent = new StringBuffer();
            List<CodeInfoBean> codeInfoBeans = projectBeans.get(0).getCodeinfoes();
            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
                String codename=codeInfoBean.getCode_nmae();
                String code_content = sampleCodeBeanDao.findByCode_nmae(codename).get(0).getCode_content();
                if(codeContent.length()>0){
                    codeContent.append("\r\n");
                }
                codeContent.append(code_content);
            }
            List<String> keywords=HanLP.extractKeyword(codeContent.toString(), 10);
            parameters.setQuestionKeywords(keywords);
        }
        
        for (String key : parameters.getQuestionKeywords()) {
            CustomDictionary.add(key);
        }
        List<String> stopwords = FileUtils.readLines(new File("stopwords.txt"), "utf-8");
        for (String stopword : stopwords) {
            stopword = stopword.trim();
            if (stopword.length() > 0) {
                CoreStopWordDictionary.add(stopword);
            }
        }
        ////////////////////////
        List<PersonalityPredictionArg> ret=new ArrayList<>();
        
        for (ProjectBean pb : projectBeans) {
            StringBuffer buffer = new StringBuffer();
            for (CodeInfoBean codeInfoBean : pb.getCodeinfoes()) {
                
                NoteInfoBean noteInfoBean = codeInfoBean.getNoteInfoBean();
                String note = noteInfoBean.getNote_content();
                if (note.isBlank()==false) {
                    note = note.replaceAll("(\\s)+", " ");
                    if (buffer.length() > 0) {
                        buffer.append("\r\n");
                    }
                    buffer.append(note);
                }
            }
            List<String> keywords=HanLP.extractKeyword(buffer.toString(), 100);
            PersonalityPredictionArg arg=new PersonalityPredictionArg();
            arg.setAuthor(pb.getAuthor());
            for(String keyword : keywords){
                if(parameters.getQuestionKeywords().contains(keyword)){
                    arg.setCountQuestionKeywords(arg.getCountQuestionKeywords()+1);
                }else  if(parameters.getProgrammingKeywords().contains(keyword)){
                    arg.setCountProgrammingKeywords(arg.getCountProgrammingKeywords()+1);
                }else{
                    arg.setCountOtherKeywords(arg.getCountOtherKeywords()+1);
                }
            }
            ret.add(arg);
        }
//        return ret;
        //preserve this for identifying question keys
        String codename = null;
        String code_content = "";
        StringBuffer buffer = new StringBuffer();
        List<String> notes=new ArrayList<>();
        for (ProjectBean pb : projectBeans) {
            List<CodeInfoBean> codeInfoBeans = pb.getCodeinfoes();
            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
                if (codename == null) {
                    codename = codeInfoBean.getCode_nmae();
                    code_content = sampleCodeBeanDao.findByCode_nmae(codename).get(0).getCode_content();
                }
                NoteInfoBean noteInfoBean = codeInfoBean.getNoteInfoBean();
                String note = noteInfoBean.getNote_content();
                if (note.isBlank()==false) {
                    note = note.replaceAll("(\\s)+", " ");
                    notes.add(note);
                    if (buffer.length() > 0) {
                        buffer.append("\r\n");
                    }
                    buffer.append(note);
                }
            }
        }
        HanLP(notes, code_content);
        return ret;
    }

    private Map<String, List<String>> HanLP(List<String> notes, String code_content) {
        System.out.println("ques key="+HanLP.extractKeyword(code_content, 50));
        System.out.println(notes.size());
        Map<String, List<String>> wordMap = new HashMap<>();
        try {
            Map<String, Integer> word_map = new HashMap<String, Integer>();
            List<String> keyList = new ArrayList<>();

            for (String note : notes) {
                List<String> list = HanLP.extractKeyword(note, 100);
                System.out.println("key words="+list);
                for (String keyword : list) {
                    word_map.put(keyword, (!word_map.containsKey(keyword)) ? 1 : word_map.get(keyword) + 1);
                }
            }
            for (String key : word_map.keySet()) {
                if (word_map.get(key) >= 2 && key.length() > 1) {
                    keyList.add(key);
                    System.out.println(key);
                }
            }

            List<String> ques_key = new ArrayList<>();
            List<String> oth_key = new ArrayList<>();

            for (String key : keyList) {
                if (code_content.contains(key)) {
                    ques_key.add(key);
                } else {
                    oth_key.add(key);
                }
            }
            wordMap.put("ques_key", ques_key);
//            Map<String, List<String>> oth_codeMap = keywordChatbot.getKeywordRes(oth_key.toString());
//            wordMap.putAll(oth_codeMap);
            System.out.println("ques_key" + ques_key);
            System.out.println("oth_key" + oth_key);
        } catch (Exception ex) {
            Logger.getLogger(PersonalityPredictionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wordMap;
    }
}
