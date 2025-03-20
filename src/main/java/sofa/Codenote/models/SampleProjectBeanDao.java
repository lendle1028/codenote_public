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
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author s1088
 */
public interface SampleProjectBeanDao extends JpaRepository<SampleProjectBean, String> {

    @Query(value = "SELECT * FROM sample_project_bean WHERE projectname_ex = ?1 and sampleproject_learninglevel= ?2", nativeQuery = true)
    @Cacheable("SampleProjectBeanDao")
    List<SampleProjectBean> findByProjectname_exAndSampleproject_learninglevel(String projectname_ex, String sampleproject_learninglevel);
}
