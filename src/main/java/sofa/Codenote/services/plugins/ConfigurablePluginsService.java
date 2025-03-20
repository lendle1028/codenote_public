/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.plugins;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * a PluginsService that loads plugin configurations from 
 * plugins.json
 * the file looks like
 * [
    {
        "url": "/plugins/explorer.js",
        "allows": "*",
        "excludes":""
    },
    {
        "url": "/plugins/ai_inspector.js",
        "allows": "*",
        "excludes":"11111112"
    },
    {
        "url": "/plugins/ai_inspector2.js",
        "allows": "11111112",
        "excludes":""
    },
    {
        "url": "/plugins/pair_programming.js",
        "allows": "*",
        "excludes":""
    },
    {
        "url": "/plugins/readings.js",
        "allows": "*",
        "excludes":""
    }
]
 * @author lendle
 */
@Service("ConfigurablePluginsService")
public class ConfigurablePluginsService implements PluginsService{
    protected List<PluginConfig> configs=new ArrayList<>();

    public ConfigurablePluginsService() throws IOException {
        loadConfigsFromFile();
    }
    
    public ConfigurablePluginsService(boolean load) throws IOException {
        if(load){
            loadConfigsFromFile();
        }
    }

    protected void loadConfigsFromFile() throws JsonSyntaxException, IOException {
        File file=new File("plugins.json");
        List<Map<String, String>> configMaps=new ArrayList<>();
        Gson gson=new Gson();
        if(file.exists()){
            //locate config file at first
            String json=FileUtils.readFileToString(file, "utf-8");
            configMaps=gson.fromJson(json, List.class);
        }else{
            //use default settings
            try(InputStream input=this.getClass().getClassLoader().getResource("plugins.json").openStream()){
                String json=IOUtils.toString(input, "utf-8");
                configMaps=gson.fromJson(json, List.class);
            }
        }
        //convert configMaps to configs
        for(Map<String, String> map : configMaps){
            PluginConfig config=new PluginConfig();
            config.setUrl(map.get("url"));
            String allowsString=map.get("allows");
            if(allowsString!=null){
                String [] allowStrings=allowsString.split(",");
                for(String allowString : allowStrings){
                    config.getAllows().add(allowString);
                }
            }else{
                config.getAllows().add("*");
            }
            
            String excludesString=map.get("excludes");
            if(excludesString!=null){
                String [] excludeStrings=excludesString.split(",");
                for(String excludeString: excludeStrings){
                    config.getExcludes().add(excludeString);
                }
            }
            configs.add(config);
        }
    }
    
    @Override
    public List<PluginMeta> getAvailablePlugins(String stuid) {
        List<PluginMeta> plugins=new ArrayList<>();
        for(PluginConfig config : configs){
            if(config.allow(stuid)){
                PluginMeta plugin=new PluginMeta();
                plugin.setUrl(config.getUrl()+"?"+System.currentTimeMillis());
                plugins.add(plugin);
            }
        }
        return plugins;
    }
    
}
