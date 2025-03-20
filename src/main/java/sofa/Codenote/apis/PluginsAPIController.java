/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.services.plugins.PluginMeta;
import sofa.Codenote.services.plugins.PluginsService;

/**
 *
 * @author lendle
 */
@RestController
public class PluginsAPIController {
    @Autowired
    private PluginsService pluginsService=null;
    @GetMapping("/api/plugins")
    public List<PluginMeta> getPlugins(HttpServletRequest request){
        return pluginsService.getAvailablePlugins((String) request.getSession().getAttribute("stuid"));
    }
}
