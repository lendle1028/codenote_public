/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rocks.imsofa.ai.puppychatter.Response;
import sofa.Codenote.models.CodeErrorBean;
import sofa.Codenote.models.CodeErrorInfoBean;
import sofa.Codenote.models.CodeInspectionLogBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.ThreadUtils;
import sofa.Codenote.services.ChatbotService;
import sofa.Codenote.services.codecoach.CodeCouchInput;
import sofa.Codenote.services.codecoach.CodeCoachOutput;
import sofa.Codenote.models.JsError;
import sofa.Codenote.services.codecoach.CodeCoachCacheService;

/**
 *
 * @author USER
 */
@RestController
public class CodeCoachChatbotController {

    @Autowired
    private ChatbotService chatbotService = null;

    @Autowired
    private CodeCoachCacheService coachCacheService = null;
//    @Autowired
//    private CodeErrorBeanDao codeErrorBeanDao = null;
    private Map<String, CodeCoachOutput> codeCouchOutputMap = new HashMap<>();

    @Autowired
    private ProjectBeanDao projectBeanDao = null;
    @Autowired
    private CodeInspectionLogBeanService codeInspectionLogBeanService = null;
    
    @GetMapping("/ai/codeCoach/result/{ticketId}")
    public CodeCoachOutput getResult(@PathVariable String ticketId) {
        return codeCouchOutputMap.get(ticketId);
    }

    @GetMapping("/ai/codeCoach/errorSummary/sampleProjectId/{sampleProjectId}")
    public CodeCoachOutput errorSummary(@PathVariable String sampleProjectId) throws Exception {
        List<CodeInspectionLogBean> logs = codeInspectionLogBeanService.findTopLogBySampleProjectId(sampleProjectId);
        StringBuffer askContent = new StringBuffer();
        for (CodeInspectionLogBean log : logs) {
            askContent.append("以下是學生 " + log.getStu_id() + " 的程式錯誤訊息:\r\n");
            StringBuffer errors = new StringBuffer();
            for (CodeErrorBean file : log.getFiles()) {
                if (log.isHasError() == false) {
                    continue;
                }
                for (CodeErrorInfoBean error : file.getJs_errors()) {
                    errors.append("""
                    在 第 ${lineNumber} 行有錯誤：${error} \r\n
                                  """
                            .replace("${lineNumber}", error.getError_linenum())
                            .replace("${error}", error.getError_reason()));
                }
                askContent.append("在 " + file.getJsfile_name() + "中有下列 error:\r\n").append(errors);
            }

        }
        Response ret = chatbotService.askByText("你現在是一位經驗豐富的 javascript 教師，我會提供你學生練習程式時發生的錯誤，請從這些錯誤中判斷學生較不會的地方，提供教學建議，不要個別評論，請整體性的檢視，請提供1-2個繁體中文網頁作爲補充教材，你產生的回覆請使用 html 片段進行格式，並將其放在 div 中使其容易閱讀\r\n\r\n" + askContent);

        CodeCoachOutput codeCoachOutput = new CodeCoachOutput();
        codeCoachOutput.setError(ret.isError());
        codeCoachOutput.setMessage(ret.getMessage());
        return codeCoachOutput;
    }

    @PostMapping("/ai/codeCoach")
    public String checkAsync(@RequestBody CodeCouchInput input) throws Exception {
        String projectId = input.getProjectId();
        ProjectBean project = projectBeanDao.findById(projectId).get();
        String prompt = this.createPrompt(project, input);
//        System.out.println(prompt+":"+prompt.startsWith("model:"));
        String ticketId = UUID.randomUUID().toString();
        String cachedResult = coachCacheService.getFromCache(project.getSampleProjectBean().getProjectid_ex(), prompt);
        if (cachedResult != null) {
            CodeCoachOutput output = new CodeCoachOutput();
            output.setError(false);
            output.setMessage(cachedResult);
            codeCouchOutputMap.put(ticketId, output);
        } else {
            ThreadUtils.submit(() -> {
                try {
                    //check cache again
                    Thread.sleep((long) (1000*Math.random()));//make them run in non-parallel form so cache service may work
                    String cachedResult2 = coachCacheService.getFromCache(project.getSampleProjectBean().getProjectid_ex(), prompt);
                    if (cachedResult2 != null) {
//                        System.out.println("cached!");
                        CodeCoachOutput output = new CodeCoachOutput();
                        output.setError(false);
                        output.setMessage(cachedResult2);
                        codeCouchOutputMap.put(ticketId, output);
                    }else{
//                        System.out.println("not cached!");
                        Response ret = chatbotService.askByText(prompt, true);
                        CodeCoachOutput output = new CodeCoachOutput();
                        output.setError(ret.isError());
                        if (output.isError()) {
                            output.setMessage("未發現或無法處理錯誤");
                        } else {
                            try{
                                output.setMessage(cleanJsonResponse(ret.getMessage("application/json")));
//                                System.out.println("getMessage="+output.getMessage());
                                coachCacheService.putIntoCache(project.getSampleProjectBean().getProjectid_ex(), prompt, output.getMessage());
                            }catch(Exception e1){
                                output.setError(true);
                                output.setMessage(e1.getMessage());
                            }
                        }
                        codeCouchOutputMap.put(ticketId, output);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(CodeCoachChatbotController.class.getName()).log(Level.SEVERE, null, ex);
                    CodeCoachOutput output = new CodeCoachOutput();
                    output.setError(true);
                    output.setMessage(ex.getMessage());
                    codeCouchOutputMap.put(ticketId, output);
                }
            }, ThreadUtils.CODECOUCH_EXECUTOR_SERVICE);
        }

        return ticketId;
    }

    private String createPrompt(ProjectBean project, CodeCouchInput input) throws Exception {
        List<String> fileFilter = input.getFileFilter();
        List<JsError> errors = input.getErrors();
        StringBuffer askContent = new StringBuffer();
        Map<String, List<String>> fileContentsMap = new HashMap<>();
        boolean syntaxErrorFound = false;
 
        //read file contents line by line and store in fileContentsMap
        for (JsError error : errors) {
            List<String> fileContents = fileContentsMap.get(error.getFileName());
            if (fileContents == null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URI(error.getFileName()).toURL().openStream(), "utf-8"))) {
                    fileContents = IOUtils.readLines(reader);
                    fileContentsMap.put(error.getFileName(), fileContents);
                }
            }
            if (error.getType().equals("SyntaxError")) {//check if there are syntax errors
                syntaxErrorFound = true;
                //檢查是否由括號造成，這是最常見且會影響整個解讀的錯誤
                String src=String.join("\r\n", fileContents);
                BracketsMatchResults ret=matchBrackets( src);
                String postfix="你現在是一個教授入門 JavaScript 程式設計的教師，請用 json 物件回應，在回傳的訊息中，將檔名放到 file 屬性，檔名是 "+toSimpleFileName(error.getFileName())+" ，將說明以醒目的 html格式放到 suggest 屬性，";
                if(ret.curly>0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，少了一個 } 或多了一個 {，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }else if(ret.curly<0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，多了一個 } 或少了一個 { ，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }else if(ret.parentheses>0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，少了一個 ) 或多了一個 (，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }else if(ret.parentheses<0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，多了一個 ) 或少了一個 (，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }else if(ret.square>0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，少了一個 ] 或多了一個 [，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }else if(ret.square<0){
                    return "以下程式碼是以 JavaScript ES6 的標準撰寫，多了一個 ] 或少了一個 [，請找出在哪裏並說明如何修改，回覆時只要顯示片段程式碼，不要完整程式\r\n"+postfix+"以下是程式碼\r\n"+src;
                }
            }
        }
        //若有 SyntaxError，因爲會影響程式的解讀，因此只處理 SyntaxError
        String codeSnippets=null;
        for (JsError error : errors) {
            if (syntaxErrorFound && error.getType().equals("SyntaxError") == false) {
                continue;
            }
            List<String> fileContents = fileContentsMap.get(error.getFileName());
            if (fileContents == null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URI(error.getFileName()).toURL().openStream(), "utf-8"))) {
                    fileContents = IOUtils.readLines(reader);
                    fileContentsMap.put(error.getFileName(), fileContents);
                }
            }
            int startLine = Math.max(1, error.getLineNumber() - 5);
            startLine=1;
            int endLine = Math.min(fileContents.size(), error.getLineNumber() + 5);
            if(syntaxErrorFound){
                endLine=fileContents.size();//有 SyntaxError，要看整個程式碼
            }
            List<String> codeLines = fileContents.subList(startLine - 1, endLine);
            codeSnippets = String.join("\r\n", codeLines);
            
            String fileName = error.getFileName();
            int startIndex = fileName.lastIndexOf("/");
            int endIndex = fileName.indexOf("?");
            startIndex = (startIndex != -1) ? startIndex + 1 : 0;
            endIndex = (endIndex != -1) ? endIndex : fileName.length();
            fileName = fileName.substring(startIndex, endIndex);
            askContent.append("""
            在 ${filename} 的第 ${lineNumber} 行有錯誤：${error}，錯誤的類型是 ${type}，該程式片段從第${startLine}行到第${endLine}行如下\r\n ${snippets}\r\n請用最簡單的方式修正 \r\n
                              """.replace("${filename}", fileName)
                    .replace("${lineNumber}", "" + error.getLineNumber())
                    .replace("${error}", error.getMessage())
                    .replace("${snippets}", codeSnippets.trim())
                    .replace("${startLine}", "" + (startLine))
                    .replace("${endLine}", "" + (endLine))
                    .replace("${type}", "" + error.getType()));
        }
        /*return """
                     請用json陣列的格式回應。你現在是一個教授入門 JavaScript 程式設計的教師，請檢視下列錯誤訊息,以最容易被初學者理解的說法，用繁體中文給予修正的建議，及提供修正後的程式碼，回傳的訊息必須符合 json 陣列的格式，將每個檔案的回傳封裝成一個物件，在回傳的訊息中，將檔名放到 file 屬性，將建議以純文字放到 suggest 屬性，請注意說明行號，將原始程式片段放在 oldCode 屬性，將修改後程式片段放在 newCode 屬性。以下是錯誤訊息：\r\n
                     """ + askContent
                + """
                        \r\n在建議中要明確的說明行號，若有 SyntaxError，優先檢查括號。
                        """.trim();*/
        
        return ("""
                請用json陣列的格式回應。你現在是一個教授入門 JavaScript 程式設計的教師，請檢視下列錯誤訊息,以最容易被初學者理解的說法，用繁體中文簡短說明應該怎麼修正，說明儘量不要出現程式碼片段，優先檢查檢查刮號是否成對，將不成對的括號行號紀錄，回傳的訊息必須符合 json 陣列的格式，將每個檔案的回傳封裝成一個物件，在回傳的訊息中，將檔名放到 file 屬性，將說明以醒目的html格式放到 suggest 屬性，請注意說明行號，以下是錯誤訊息：\r\n
                     """ + askContent).trim();
    }

    private String toSimpleFileName(String fileName) {
        int startIndex = fileName.lastIndexOf("/") + 1;
        int endIndex = fileName.lastIndexOf("?");
        fileName = (endIndex != -1) ? fileName.substring(startIndex, endIndex) : fileName.substring(startIndex, fileName.length());
        return fileName;
    }

    /**
     * the response generated by GAI can vary, this method will try to clean it
     * whenever possible
     *
     * @param response
     * @return
     * @throws Exception
     */
    private String cleanJsonResponse(String response) throws Exception {
        Gson gson = new Gson();
        response=response.trim();
        if(response.endsWith("```")){
            response=response.substring(0, response.length()-3);
        }
        try {
            //first, try the json array syntax
            List list = gson.fromJson(response, List.class);
            //if succeed, return it
            return gson.toJson(list);
        } catch (Exception e) {
            //next, try the json object syntax
            Map map=null;
            try{
                map=gson.fromJson(response, Map.class);
            }catch(Exception e1){
                //if failed, throw error
                throw e1;
            }
            //if succeed, return the array representation of it
            return gson.toJson(List.of(map));
        }
    }
    
    private BracketsMatchResults matchBrackets(String src){
        int leftParentheses=StringUtils.countMatches(src, "(");
        int rightParentheses=StringUtils.countMatches(src, ")");
        int leftSquare=StringUtils.countMatches(src, "[");
        int rightSquare=StringUtils.countMatches(src, "]");
        int leftCurly=StringUtils.countMatches(src, "{");
        int rightCurly=StringUtils.countMatches(src, "}");
        BracketsMatchResults ret=new BracketsMatchResults();
        ret.parentheses=leftParentheses-rightParentheses;
        ret.square=leftSquare-rightSquare;
        ret.curly=leftCurly-rightCurly;
        return ret;
    }
    
    static class BracketsMatchResults{
        int parentheses=0;//1 means left outnumbered while -1 means right outnumbered
        int square=0;
        int curly=0;
    }
}
