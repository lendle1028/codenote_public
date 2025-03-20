/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sofa.Codenote.services.plugins;

import java.util.List;

/**
 *
 * @author lendle
 */
public interface PluginsService {
    public List<PluginMeta> getAvailablePlugins(String stuid);
}
