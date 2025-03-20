/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rocks.imsofa.ai.puppychatter.Response;
import rocks.imsofa.ai.puppychatter.gemini.AbstractGoogleSearchHandler;
import rocks.imsofa.ai.puppychatter.gemini.GoogleSearchFactSource;
import sofa.Codenote.services.ChatbotService;

/**
 *
 * @author s1088
 */
@Component
public class KeywordChatbot {

    @Autowired
    private ChatbotService chatbotService = null;
    @Value("${googleApiKey}")
    private String googleApiKey;
    private String searchEngineId = "27d58ef5bac114aec";

    public List<String> getProgrammingKeys() throws Exception {
        
        GoogleSearchFactSource javaScriptFactSource = new GoogleSearchFactSource(googleApiKey, searchEngineId, "", 
                "(site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Grammar_and_types OR site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Loops_and_iteration OR site:https://developer.mozilla.org/zh-TW/docs/Web/JavaScript/Guide/Functions) javascript", 
                new KeywordCollectorGoogleSearchHandler());
        GoogleSearchFactSource phaserFactSource = new GoogleSearchFactSource(googleApiKey, searchEngineId, "", 
                "(site:https://phaser.io OR site:www.w3schools.com/* OR site:phaser.io/tutorials*) phaser", 
                new KeywordCollectorGoogleSearchHandler());
        String summaryJavaScript=chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 你現在是一位 JavaScript 的老師，請根據這些資料，撰寫一份詳細的 JavaScript 教材，這個教材要是詳細的，可以直接當作參考書用的:\r\n" + javaScriptFactSource.getSummary()).getMessage();
        String summaryPhaser=chatbotService.askByText("model:anthropic/claude-3.5-sonnet:beta 你現在是一位 phaser 的老師，請根據這些資料，撰寫一份詳細的 phaser 教材，這個教材要是詳細的，可以直接當作參考書用的:\r\n" + phaserFactSource.getSummary()).getMessage();
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
        System.out.println(gson.toJson(allKeywords));
        return new ArrayList<>(allKeywords);
    }

    public Map<String, List<String>> getKeywordRes(String words) {
        Map<String, List<String>> res = new HashMap<>();
        try {
            Response ret = chatbotService.askByText("""
                    請從下列詞彙中篩選出與程式撰寫有關且在教科書中會出現的專業用語，結果寫成一行，用逗號隔開來:\r\n
                     """ + words);
            System.out.println(ret.getMessage());

        } catch (Exception ex) {
            Logger.getLogger(KeywordChatbot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
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
