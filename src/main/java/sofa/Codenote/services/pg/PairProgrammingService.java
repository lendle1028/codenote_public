/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sofa.Codenote.services.pg;

import sofa.Codenote.models.pg.RoomRole;
import java.util.List;

/**
 *
 * @author lendle
 */
public interface PairProgrammingService {
    public List<RoomRole> getAvailableRooms(String stuid);
}
