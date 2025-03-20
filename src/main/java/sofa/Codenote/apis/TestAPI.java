/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.apis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author lendle
 */
@RestController
public class TestAPI {
    @GetMapping("/api/test/session")
    public String testSession(HttpServletRequest httpServletRequest){
        HttpSession session=httpServletRequest.getSession();
        return (String) session.getAttribute("stuid");
    }
}
