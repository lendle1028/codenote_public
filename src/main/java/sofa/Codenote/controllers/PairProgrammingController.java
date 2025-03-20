/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.controllers;

import sofa.Codenote.controllers.CreateSampleProjectController;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sofa.Codenote.models.pg.RoomRoleDao;
import sofa.Codenote.models.pg.RoomRole;

/**
 *
 * @author lendle
 */
@Controller
public class PairProgrammingController {
    @Autowired
    private RoomRoleDao roomRoleDao=null;
    
    @GetMapping("/pg/roomRole/list")
    public String listAction(){
        return "roomRoles/list";
    }
    
    @GetMapping("/pg/roomRole/add")
    public String addAction(){
        return "roomRoles/add";
    }
    
    @GetMapping("/pg/roomRole/id/{id}")
    public String editAction(@PathVariable String id, Model model){
        model.addAttribute("id", id);
        return "roomRoles/edit";
    }
    
    @PostMapping("/pg/roomRole/import")
    @Transactional
    public String importRoomRoles(@RequestParam("file") MultipartFile file,
            HttpServletResponse response){
        Gson gson=new Gson();
        try(InputStream input=file.getInputStream()){
            String json=IOUtils.toString(input, "utf-8");
            List<Map> list=gson.fromJson(json, List.class);
            for(Map map : list){
                RoomRole role=gson.fromJson(gson.toJson(map), RoomRole.class);
                roomRoleDao.save(role);
            }
        } catch (IOException ex) {
            Logger.getLogger(CreateSampleProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "redirect:/pg/roomRole/list";
    }
}
