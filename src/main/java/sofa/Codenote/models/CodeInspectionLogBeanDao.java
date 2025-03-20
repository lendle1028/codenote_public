/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author s1088
 */
public interface CodeInspectionLogBeanDao  extends JpaRepository<CodeInspectionLogBean, String> {
    public Optional<CodeInspectionLogBean> findTopByProjectidOrderByTimestampDesc(String projectId);
    public List<CodeInspectionLogBean> findByProjectidInOrderByProjectidAscTimestampDesc(Collection<String> ids);
}
