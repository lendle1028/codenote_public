/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.plugins;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * a ConfigurablePluginsService that loads plugin configurations from
 * .conf/.plugins folder
 * the folder has several files:
 * pluginIndex: list of plugin js files
 * ${jsFileName}.allows: the allow list (defaults to * if not present)
 * ${jsFileName}.excludes: the excludes list (defaults to empty if not present)
 * 
 * e.g.
 * 
 * explorer.js.allows
 * explorer.js.excludes
 * 
 * if
 * .conf/.plugins/pluginIndex is not found, delegates to plugins.json
 * @author lendle
 */

@Service("ConfFolderConfigurablePluginsService")
@Primary
public class ConfFolderConfigurablePluginsService extends ConfigurablePluginsService{

    public ConfFolderConfigurablePluginsService() throws IOException {
        super(false);
        File confFolder=new File(".conf");
        File pluginsFolder=new File(confFolder, ".plugins");
        File pluginIndexFile=new File(pluginsFolder, "pluginIndex");
        if(!pluginIndexFile.exists()){
            super.loadConfigsFromFile();
        }else{
            List<String> urls=FileUtils.readLines(pluginIndexFile, "utf-8");
            for(String url : urls){
                if(url.trim().startsWith("#")){
                    continue;
                }
                PluginConfig config=new PluginConfig();
                config.setUrl(url);
                String baseName=getPluginFileName(url);
                File allowsFile=new File(pluginsFolder, baseName+".allows");
                File excludesFile=new File(pluginsFolder, baseName+".excludes");
                if(allowsFile.exists()){
                    config.setAllows(FileUtils.readLines(allowsFile, "utf-8"));
                }else{
                    config.setAllows(List.of("*"));
                }
                
                if(excludesFile.exists()){
                    config.setExcludes(FileUtils.readLines(excludesFile, "utf-8"));
                }else{
                    config.setExcludes(List.of());
                }
                super.configs.add(config);
            }
        }
        Gson gson=new Gson();
        Logger.getLogger(this.getClass().getName()).info("plugin configs loaded: "+gson.toJson(super.configs));
    }
    
    private String getPluginFileName(String url){
        int index=url.lastIndexOf("/");
        return url.substring(index+1);
    }
    
}
