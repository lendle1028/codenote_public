/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.models;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author s1088
 */
public interface CodeInfoBeanDao  extends JpaRepository<CodeInfoBean, String> {
    
}
