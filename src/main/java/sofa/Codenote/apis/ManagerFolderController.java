/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author s1088
 */
@RestController
public class ManagerFolderController {

    @GetMapping("deletefolder")
    public void deleteFolder(@RequestParam String tempSessionId) {
        try {
            System.out.println("delete" + tempSessionId);
            File root = new File("preview").getAbsoluteFile();
            File previewProjectRoot = new File(root, tempSessionId);
            FileUtils.deleteDirectory(previewProjectRoot);
        } catch (IOException ex) {
            Logger.getLogger(ManagerFolderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
