/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.pg;

import sofa.Codenote.models.pg.RoomRoleDao;
import sofa.Codenote.models.pg.RoomRole;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 *
 * @author lendle
 */
@Service("JpaPairProgrammingService")
@Primary
public class JpaPairProgrammingService implements PairProgrammingService{
    @Autowired
    private RoomRoleDao dao=null;
    @Override
    public List<RoomRole> getAvailableRooms(String stuid) {
        List<RoomRole> roomRoles=dao.findByStuid(stuid);
        return roomRoles;
    }
    
}
