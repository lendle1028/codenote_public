/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.controllers.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author USER
 */
@Controller
public class ErrorSummaryController {
    @GetMapping("/teacher/errorSummary")
    public String indexAction(){
        return "teacher/errorSummary";
    }
}
