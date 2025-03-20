/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author s1088
 */
public interface ProjectBeanDao extends JpaRepository<ProjectBean, String> {
    @Cacheable("ProjectBeanDao")
    Optional<ProjectBean> findById(String projectId);
    @Cacheable("ProjectBeanDao")
    List<ProjectBean> findByPrjectnameAndAuthor(String prjectname, String author);
    @Cacheable("ProjectBeanDao")
    List<ProjectBean> findByPrjectname(String prjectname);
    List<ProjectBean> findBySampleProjectBean(SampleProjectBean sampleProjectBean);
}
