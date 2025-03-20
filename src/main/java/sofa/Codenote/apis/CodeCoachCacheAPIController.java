/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.services.codecoach.CodeCoachCacheService;
import sofa.Codenote.services.codecoach.CodeCoachCacheService.CodeCoachCacheKey;

/**
 *
 * @author lendle
 */
@RestController
public class CodeCoachCacheAPIController {
    @Autowired
    private CodeCoachCacheService codeCoachCacheService=null;
    @GetMapping("/api/codeCoachCache/items")
    public List<Map> getAllItems(){
        List<CodeCoachCacheService.CodeCoachCacheKey> keys=codeCoachCacheService.getAllKeys();
        List<Map> ret=new ArrayList<>();
        for(CodeCoachCacheService.CodeCoachCacheKey key : keys){
            ret.add(Map.of(
                    "key", key,
                    "value", codeCoachCacheService.getFromCache(key)
            ));
        }
        return ret;
    }
    @PostMapping("/api/codeCoachCache/removeItem")
    public void remove(@RequestBody CodeCoachCacheKey key){
        System.out.println(key.getSampleProjectId()+":"+key.getPrompt());
        codeCoachCacheService.removeFromCache(key);
    }
}
