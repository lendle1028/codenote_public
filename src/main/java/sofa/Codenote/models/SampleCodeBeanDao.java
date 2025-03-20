/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import sofa.Codenote.*;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author s1088
 */
public interface SampleCodeBeanDao extends JpaRepository<SampleCodeBean, String> {
    @Query(value = "SELECT * FROM sample_code_bean WHERE code_nmae = ?1", nativeQuery = true)
    List<SampleCodeBean> findByCode_nmae(String code_nmae);
}
