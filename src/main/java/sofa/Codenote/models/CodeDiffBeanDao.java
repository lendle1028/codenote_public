/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author s1088
 */
public interface CodeDiffBeanDao extends JpaRepository<CodeDiffBean, String> {

    public List<CodeDiffBean> findByAuthor(String author);

    public List<CodeDiffBean> findByProjectid(String projectid);

    public List<CodeDiffBean> findByProjectidAndAuthorOrderByTimestampAsc(String projectid, String author);

    @Query("SELECT codediff.author FROM CodeDiffBean as codediff GROUP BY codediff.author ORDER BY codediff.timestamp")
    List<Object[]> findAllAuthort();
}
