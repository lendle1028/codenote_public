/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.codecoach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 *
 * @author lendle
 */
@Service
public class CodeCoachCacheService {
    private Map<CodeCoachCacheKey, String> cache=new HashMap<>();
    
    public synchronized String getFromCache(String sampleProjectId, String prompt){
        return this.getFromCache(new CodeCoachCacheKey(sampleProjectId, prompt));
    }
    
    public synchronized String getFromCache(CodeCoachCacheKey key){
        return cache.get(key);
    }
    
    public synchronized void putIntoCache(String sampleProjectId, String prompt, String ret){
        cache.put(new CodeCoachCacheKey(sampleProjectId, prompt), ret);
    }
    
    public List<CodeCoachCacheKey> getAllKeys(){
        return new ArrayList<>(cache.keySet());
    }
    
    public synchronized void removeFromCache(String sampleProjectId, String prompt){
        this.removeFromCache(new CodeCoachCacheKey(sampleProjectId, prompt));
    }
    
    public synchronized void removeFromCache(CodeCoachCacheKey key){
        this.cache.remove(key);
    }
    
    public static class CodeCoachCacheKey{
        private String sampleProjectId=null;
        private String prompt=null;

        public CodeCoachCacheKey(String sampleProjectId, String prompt) {
            this.sampleProjectId=sampleProjectId;
            this.prompt=prompt;
        }

        public CodeCoachCacheKey() {
        }

        public String getSampleProjectId() {
            return sampleProjectId;
        }

        public void setSampleProjectId(String sampleProjectId) {
            this.sampleProjectId = sampleProjectId;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.sampleProjectId);
            hash = 97 * hash + Objects.hashCode(this.prompt);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CodeCoachCacheKey other = (CodeCoachCacheKey) obj;
            if (!Objects.equals(this.sampleProjectId, other.sampleProjectId)) {
                return false;
            }
            return Objects.equals(this.prompt, other.prompt);
        }
        
        
    }
}
