/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.pg;

import sofa.Codenote.models.pg.RoomRole;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author lendle
 */
@Service("TestPairProgrammingService")
public class TestPairProgrammingService implements PairProgrammingService{

    @Override
    public List<RoomRole> getAvailableRooms(String stuid) {
        RoomRole rr=new RoomRole();
        rr.setRoomName("test");
        rr.setStuid(stuid);
        if(stuid.equals("11111111")){
            rr.setWritable(true);
        }else{
            rr.setWritable(false);
        }
        return List.of(rr);
    }
    
}
