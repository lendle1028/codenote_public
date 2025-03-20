/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.plugins;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lendle
 */
public class PluginConfig {
    private String url=null;
    private List<String> allows=new ArrayList<>();
    private List<String> excludes=new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAllows() {
        return allows;
    }

    public void setAllows(List<String> allows) {
        this.allows = allows;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
    
    public boolean allow(String stuid){
        if(allows.contains(stuid) || (allows.contains("*") && excludes.contains(stuid)==false)){
            return true;
        }else{
            return false;
        }
    }
}
