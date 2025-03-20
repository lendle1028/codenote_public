/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.personality;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author lendle
 */
@Component
public class PersonalityPredictionParametersManager {
    public PersonalityPredictionParameters get(String id) throws IOException{
        File personalityConfFolder=getConfFolder();
        Gson gson=new Gson();
        for(File file : personalityConfFolder.listFiles()){
            String json=FileUtils.readFileToString(file, "utf-8");
            PersonalityPredictionParameters p=gson.fromJson(json, PersonalityPredictionParameters.class);
            if(p.getId().equals(id)){
                return p;
            }
        }
        return null;
    }
    
    public void remove(String id) throws Exception{
        ConfFile confFile=this.getConfFile(id);
        FileUtils.forceDelete(confFile.getFile());
    }
    
    public PersonalityPredictionParameters getExample(String id) throws IOException{
        PersonalityPredictionParameters p=new PersonalityPredictionParameters();
        p.setId(id);
        p.setAverageOtherKeywords(5);
        p.setAverageProgrammingKeywords(5);
        p.setAverageQuestionKeywords(3);
        p.setStdOtherKeywords(3);
        p.setStdProgrammingKeywords(3);
        p.setStdQuestionKeywords(2);
        
        p.setQuestionKeywords(List.of("phaser", "角色", "滑鼠", "rocket, true"));
        
        Gson gson=new Gson();
        String json=FileUtils.readFileToString(new File("temp.txt"), "utf-8");
        p.setProgrammingKeywords(gson.fromJson(json, List.class));
        return p;
    }
    
    public List<PersonalityPredictionParameters> list() throws Exception{
        List<PersonalityPredictionParameters> ret=new ArrayList<>();
        File personalityConfFolder=getConfFolder();
        Gson gson=new Gson();
        for(File file : personalityConfFolder.listFiles()){
            String json=FileUtils.readFileToString(file, "utf-8");
            PersonalityPredictionParameters p=gson.fromJson(json, PersonalityPredictionParameters.class);
            ret.add(p);
        }
        return ret;
    }
    
    public void save(PersonalityPredictionParameters parameters) throws Exception{
        ConfFile confFile=getConfFile(parameters.getId());
        File targetFile=null;
        if(confFile!=null){
            //there is no such a file
            targetFile=confFile.getFile();
        }else{
            targetFile=new File(getConfFolder(), UUID.randomUUID().toString()+".json");
        }
        Gson gson=new Gson();
        System.out.println(gson.toJson(parameters));
        FileUtils.write(targetFile, gson.toJson(parameters), "utf-8");
    }
    
    private ConfFile getConfFile(String id) throws Exception{
        File personalityConfFolder=getConfFolder();
        Gson gson=new Gson();
        for(File file : personalityConfFolder.listFiles()){
            String json=FileUtils.readFileToString(file, "utf-8");
            PersonalityPredictionParameters p=gson.fromJson(json, PersonalityPredictionParameters.class);
            if(p.getId().equals(id)){
                return new ConfFile(file, p);
            }
        }
        return null;
    }
    
    private File getConfFolder(){
        File confFolder=new File(".conf");
        File personalityConfFolder=new File(confFolder, ".personalityPrediction");
        if(!personalityConfFolder.exists()){
            personalityConfFolder.mkdirs();
        }
        return personalityConfFolder;
    }
    
    class ConfFile{
        private File file=null;
        private PersonalityPredictionParameters parameters=null;

        public ConfFile(File file, PersonalityPredictionParameters parameters) {
            this.file=file;
            this.parameters=parameters;
        }
        
        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public PersonalityPredictionParameters getParameters() {
            return parameters;
        }

        public void setParameters(PersonalityPredictionParameters parameters) {
            this.parameters = parameters;
        }
        
        
    }
}
