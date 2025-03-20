/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.plugins;

import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 *
 * @author lendle
 */
@Service("SimplePluginsService")
//@Primary
public class SimplePluginsService implements PluginsService{

    @Override
    public List<PluginMeta> getAvailablePlugins(String stuid) {
        return List.of(
                new PluginMeta("/plugins/explorer.js?"+System.currentTimeMillis()),
                new PluginMeta("/plugins/ai_inspector.js?"+System.currentTimeMillis()),
                new PluginMeta("/plugins/pair_programming.js?"+System.currentTimeMillis()),
                new PluginMeta("/plugins/readings.js?"+System.currentTimeMillis())
        );
    }
    
}
