/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models.pg;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author s1088
 */
public interface RoomRoleDao extends JpaRepository<RoomRole, String> {
    public List<RoomRole> findByStuid(String stuid);
    public List<RoomRole> getAllByOrderByRoomNameAscStuidAsc();
}
