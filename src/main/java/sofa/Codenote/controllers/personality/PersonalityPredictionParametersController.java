/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.controllers.personality;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author USER
 */
@Controller
public class PersonalityPredictionParametersController {
    @GetMapping("personalityPrediction/conf/list")
    public String indexAction(){
        return "personalityPrediction/list";
    }
    
    @GetMapping("personalityPrediction/conf/new")
    public String newAction(Model model){
        model.addAttribute("mode", "new");
        return "personalityPrediction/edit";
    }
    
    @GetMapping("personalityPrediction/conf/id/{id}")
    public String editAction(Model model, @PathVariable String id){
        model.addAttribute("id", id);
        model.addAttribute("mode", "edit");
        return "personalityPrediction/edit";
    }
}
