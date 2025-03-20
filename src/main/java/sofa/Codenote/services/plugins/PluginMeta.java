/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.plugins;

/**
 *
 * @author lendle
 */
public class PluginMeta {
    private String url=null;

    public PluginMeta() {
    }
    
    public PluginMeta(String url) {
        this.url=url;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
