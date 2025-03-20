/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author lendle
 */
@Controller
public class CodeCoachCacheController {
    @GetMapping("/teacher/codeCoachCacheManagement")
    public String indexAction(){
        return "teacher/codeCoachCacheManagement";
    }
}
