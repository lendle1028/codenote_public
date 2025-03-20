/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sofa.Codenote.models.pg.RoomRoleDao;
import sofa.Codenote.services.pg.PairProgrammingService;
import sofa.Codenote.models.pg.RoomRole;

/**
 *
 * @author lendle
 */
@RestController
public class PairProgrammingAPIController {
    @Autowired
    private PairProgrammingService pps=null;
    @Autowired
    private RoomRoleDao roomRoleDao=null;
    
    @GetMapping("/api/pg/rooms/stuid/{stuid}")
    public List<RoomRole> getAvailableRooms(@PathVariable String stuid){
        return pps.getAvailableRooms(stuid);
    }
    @PostMapping("/api/pg/chat/room/{room}/stuid/{stuid}")
    public void sendChatMessage(@PathVariable String room, @PathVariable String stuid, @RequestBody Map<String, String> chatMessage, HttpServletRequest request){
        Pusher pusher = new Pusher("1757298", "f547ee541555e37c57bd", "fa2235cc88b72e9e6aa8");
        pusher.setCluster("ap3");
        pusher.setEncrypted(true);
 
        Gson gson=new Gson();
        Map message=Map.of(
                "stuid", stuid,
                "timestamp", ""+System.currentTimeMillis(),
                "message", chatMessage.get("message")
        );
        pusher.trigger(room, "newChat", gson.toJson(message));
    }
    
    @GetMapping("/api/pg/roomRole/list")
    public List<RoomRole> getRoomRoles(){
        return roomRoleDao.getAllByOrderByRoomNameAscStuidAsc();
    }
    
    @Transactional
    @PostMapping("/api/pg/roomRole")
    public void newRoomRole(@RequestBody RoomRole roomRole){
        roomRole.setId(UUID.randomUUID().toString());
        roomRoleDao.save(roomRole);
    }
    
    @Transactional
    @PutMapping("/api/pg/roomRole")
    public void saveRoomRole(@RequestBody RoomRole roomRole){
        roomRoleDao.save(roomRole);
    }
    
    @Transactional
    @DeleteMapping("/api/pg/roomRole/id/{id}")
    public void removeRoomRole(@PathVariable String id){
        RoomRole roomRole=roomRoleDao.findById(id).get();
        roomRoleDao.delete(roomRole);
    }
    
    @GetMapping("/api/pg/roomRole/id/{id}")
    public RoomRole getRoomRole(@PathVariable String id){
        return roomRoleDao.findById(id).get();
    }
    
    @GetMapping("/api/pg/roomRole/export")
    public void exportRoomRoles(HttpServletResponse response){
        List<RoomRole> list=roomRoleDao.getAllByOrderByRoomNameAscStuidAsc();
        Gson gson=new Gson();
        String json=gson.toJson(list);
        response.setContentType("application/json;charset=utf-8");
        ((HttpServletResponse) response).setHeader("Content-disposition", "attachment; filename=roomRoles.json");
        try (InputStream input = IOUtils.toInputStream(json, "utf-8"); OutputStream output = response.getOutputStream()) {
            IOUtils.copy(input, output);//輸出的串流會直接到瀏覽器端
        } catch (Exception ex) {
            Logger.getLogger(PairProgrammingAPIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
