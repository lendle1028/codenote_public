/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author s1088
 */
@RestController
public class ImportExportController {

    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    @Autowired
    private ProjectBeanDao projectBeanDao = null;

    @GetMapping("/exportSampleProjectBeans")
    public boolean exportSampleProjectBeans() {
        File exportFolder = new File(this.getConfFolder(), ".export");
        exportFolder.mkdirs();
        List<SampleProjectBean> sampleProjectBeans = sampleProjectBeanDao.findAll();
        Gson gson = new Gson();
        for (SampleProjectBean sampleProjectBean : sampleProjectBeans) {
            try {
                File projectFolder = new File(exportFolder, sampleProjectBean.getProjectid_ex());
                projectFolder.mkdir();
                Map<String, String> meta = Map.of("projectid_ex", sampleProjectBean.getProjectid_ex(), "projectname_ex", sampleProjectBean.getProjectname_ex(), 
                        "sampleprojectnote_path", sampleProjectBean.getSampleprojectnote_path(), "sampleproject_learninglevel", 
                        sampleProjectBean.getSampleproject_learninglevel());
                File metaFile = new File(projectFolder, ".meta");
                FileUtils.write(metaFile, gson.toJson(meta), "utf-8");
                List<SampleCodeBean> sampleCodeBeans = sampleProjectBean.getSamplecodes();
                for (SampleCodeBean sampleCodeBean : sampleCodeBeans) {
                    File codeFile = new File(projectFolder, sampleCodeBean.getCode_nmae());
                    FileUtils.write(codeFile, sampleCodeBean.getCode_content(), "utf-8");
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportExportController.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    @GetMapping("/importSampleProjectBeans")
    public List<String> importSampleProjectBeans() {
        List<String> ret = new ArrayList<>();
        File importFolder = new File(this.getConfFolder(), ".import");
        importFolder.mkdirs();
        Gson gson = new Gson();
        for (File projectFolder : importFolder.listFiles()) {
            try {
                File metaFile = new File(projectFolder, ".meta");
                Map<String, String> meta = gson.fromJson(FileUtils.readFileToString(metaFile, "utf-8"), Map.class);
                SampleProjectBean sampleProjectBean = new SampleProjectBean();
                sampleProjectBean.setProjectid_ex(meta.get("projectid_ex"));
                sampleProjectBean.setProjectname_ex(meta.get("projectname_ex"));
                sampleProjectBean.setSampleproject_learninglevel(meta.get("sampleproject_learninglevel"));
                sampleProjectBean.setSampleprojectnote_path(meta.get("sampleprojectnote_path"));
                sampleProjectBean.setSamplecodes(new ArrayList<>());
                for (File codeFile : projectFolder.listFiles()) {
                    if (codeFile.getName().equals(".meta")) {
                        continue;
                    }
                    SampleCodeBean sampleCodeBean = new SampleCodeBean();
                    sampleCodeBean.setCodeid(UUID.randomUUID().toString());
                    sampleCodeBean.setCode_nmae(codeFile.getName());
                    sampleCodeBean.setCode_content(FileUtils.readFileToString(codeFile, "utf-8"));
                    sampleProjectBean.getSamplecodes().add(sampleCodeBean);
                }
                sampleProjectBeanDao.save(sampleProjectBean);
                ret.add(projectFolder.getName());
            } catch (IOException ex) {
                Logger.getLogger(ImportExportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    @GetMapping("/exportProjectBeans/project_id/{projectid}")
    public String exportProjectBeans(@PathVariable String projectid) {
        try {
            File exportFolder = new File(this.getConfFolder(), ".exportProject");
            exportFolder.mkdirs();
            ProjectBean projectBean = projectBeanDao.findById(projectid).get();
            
            File projectFolder = new File(exportFolder, projectBean.getPrjectname());
            projectFolder.mkdir();
            List<CodeInfoBean> codeInfoBeans = projectBean.getCodeinfoes();
            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
                try {
                    File codeFile = new File(projectFolder, codeInfoBean.getCode_nmae());
                    FileUtils.write(codeFile, codeInfoBean.getCode_content(), "utf-8");
                } catch (IOException ex) {
                    Logger.getLogger(ImportExportController.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
            new File("public").mkdirs();
            new ZipFile(new File("public", projectid+".zip")).addFolder(projectFolder);
            return "../../public/"+projectid+".zip";
        } catch (ZipException ex) {
            Logger.getLogger(ImportExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File getConfFolder() {
        return new File(".conf");
    }
}
