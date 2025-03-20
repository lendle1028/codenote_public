/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services;

import sofa.Codenote.controllers.PreviewController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.touk.jshint4j.JsHint;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author USER
 */
@Service
public class PreviewService {

    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;

    @Autowired
    private ProjectBeanDao projectBeanDao = null;

//    @Autowired
//    private CodeErrorBeanDao codeErrorBeanDao = null;

    public String previewSampleProject(String projectId) {
        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(projectId).get();
        List<SampleCodeBean> list = sampleProjectBean.getSamplecodes();
        File root = new File("preview").getAbsoluteFile();
        String tempSessionId = UUID.randomUUID().toString();
        File previewProjectRoot = new File(root, tempSessionId);
        if (!previewProjectRoot.exists()) {
            previewProjectRoot.mkdirs();
        }
        for (SampleCodeBean sampleCodeBean : list) {
            File file = new File(previewProjectRoot, sampleCodeBean.getCode_nmae());
            try {
                FileUtils.write(file, sampleCodeBean.getCode_content(), "utf-8");
            } catch (IOException ex) {
                Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return tempSessionId;
    }

    /*
    generate two session variables:
    session.setAttribute("projectBean."+projectId, projectBean);
    session.setAttribute("errorBeans."+projectId, errorBeans);
     */
    public String resetProjectReviewFolder(String projectId, String codeId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String tempSessionId;
        ProjectBean projectBean = projectBeanDao.findById(projectId).get();
        File previewProjectRoot = null;
        File root = new File("preview").getAbsoluteFile();
        if (projectBean.getFolderId() == null) {
            tempSessionId = UUID.randomUUID().toString();
            projectBean.setFolderId(tempSessionId);
            projectBeanDao.save(projectBean);

            previewProjectRoot = new File(root, tempSessionId);
            if (!previewProjectRoot.exists()) {
                previewProjectRoot.mkdirs();
            }
        } else {
            tempSessionId = projectBean.getFolderId();
            previewProjectRoot = new File(root, tempSessionId);
        }

        session.setAttribute("projectBean." + projectId, projectBean);

        List<CodeInfoBean> list = projectBean.getCodeinfoes();
        for (CodeInfoBean codeInfoBean : list) {
            if(codeId!=null && codeInfoBean.getCodeid().equals(codeId)==false){
                continue;
            }
            File file = new File(previewProjectRoot, codeInfoBean.getCode_nmae());
            try {
                String src = codeInfoBean.getCode_content();
                FileUtils.write(file, src, "utf-8");

            } catch (IOException ex) {
                Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**remove lint, will use js check instead*/
        /*List<CodeErrorBean> errorBeans = this.jsGetReason(previewProjectRoot, projectBean);
        codeErrorBeanDao.saveAll(errorBeans);

        session.setAttribute("errorBeans." + projectId, errorBeans);*/
        return tempSessionId;
    }
    /*@CacheEvict(value="CodeErrorBeanDao", allEntries = true)
    public void checkCodeErrors(ProjectBean projectBean, HttpServletRequest request){
        File previewProjectRoot = null;
        File root = new File("preview").getAbsoluteFile();
        String tempSessionId=null;
        if (projectBean.folderId == null) {
            tempSessionId = UUID.randomUUID().toString();
            projectBean.folderId = tempSessionId;
            projectBeanDao.save(projectBean);

            previewProjectRoot = new File(root, tempSessionId);
            if (!previewProjectRoot.exists()) {
                previewProjectRoot.mkdirs();
            }
        } else {
            tempSessionId = projectBean.folderId;
            previewProjectRoot = new File(root, tempSessionId);
        }
        List<CodeErrorBean> errorBeans = this.jsGetReason(previewProjectRoot, projectBean);
        codeErrorBeanDao.saveAll(errorBeans);
    }*/
    
    /**
     * output code to file for preview
     * @param projectId
     * @param codeInfoBean 
     */
    public void writeCode2File(String projectId, CodeInfoBean codeInfoBean){
        ProjectBean projectBean = projectBeanDao.findById(projectId).get();
        File previewProjectRoot = null;
        String tempSessionId = null;
        File root = new File("preview").getAbsoluteFile();
        if (projectBean.getFolderId() == null) {
            tempSessionId = UUID.randomUUID().toString();
            projectBean.setFolderId(tempSessionId);
            projectBeanDao.save(projectBean);

            previewProjectRoot = new File(root, tempSessionId);
            if (!previewProjectRoot.exists()) {
                previewProjectRoot.mkdirs();
            }
        } else {
            tempSessionId = projectBean.getFolderId();
            previewProjectRoot = new File(root, tempSessionId);
        }
        File file = new File(previewProjectRoot, codeInfoBean.getCode_nmae());
        try {
            String src = codeInfoBean.getCode_content();
            FileUtils.write(file, src, "utf-8");

        } catch (IOException ex) {
            Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*@GetMapping("/previewCode")
    @Transactional
    public String previewProject(@RequestParam String projectId, Model model, HttpServletRequest request) {
        //use timestamp.${projectId} to track modified time
        String sessionTimestampString = (String) request.getSession().getAttribute("timestamp." + projectId);
        long sessionTimestamp = (sessionTimestampString == null) ? -1 : Long.valueOf(sessionTimestampString);

        projectBean = projectBeanDao.findById(projectId).get();
        String tempSessionId;

        File previewProjectRoot = null;
        File root = new File("preview").getAbsoluteFile();
        if (projectBean.folderId == null) {
            tempSessionId = UUID.randomUUID().toString();
            projectBean.folderId = tempSessionId;
            projectBeanDao.save(projectBean);

            previewProjectRoot = new File(root, tempSessionId);
            if (!previewProjectRoot.exists()) {
                previewProjectRoot.mkdirs();
            }
        } else {
            tempSessionId = projectBean.folderId;
            previewProjectRoot = new File(root, tempSessionId);
        }

        boolean shouldRefresh = false;
        File timestampFile = new File(previewProjectRoot, ".timestamp");
        if (!timestampFile.exists() || timestampFile.lastModified() < sessionTimestamp) {
            shouldRefresh = true;
            try {
                FileUtils.touch(timestampFile);
            } catch (IOException ex) {
                Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List<CodeInfoBean> list = projectBean.getCodeinfoes();
        List<CodeErrorBean> errorBeans = null;

        long comparetime = 0;
        for (CodeInfoBean codeInfoBean : list) {
            File file = new File(previewProjectRoot, codeInfoBean.getCode_nmae());
            if (file.getName().endsWith(".js")) {
                comparetime = Math.max(comparetime, codeInfoBean.timestamp);
            }
        }

        if (shouldRefresh) {
            System.out.println("refresh");
            for (CodeInfoBean codeInfoBean : list) {
                File file = new File(previewProjectRoot, codeInfoBean.getCode_nmae());
                String html_src = null;
                try {
                    String src = codeInfoBean.getCode_content();
                    if (file.getName().endsWith(".html") && codeInfoBean.timestamp <= comparetime) {
                        src = src.replace(".js\"", ".js?" + System.currentTimeMillis() + "\"");
                        FileUtils.write(file, src, "utf-8");
                        System.out.println("output " + file + ":" + codeInfoBean.timestamp + ":" + comparetime);
                    } else if (file.lastModified() <= codeInfoBean.timestamp) {
                        FileUtils.write(file, src, "utf-8");
                        System.out.println("output " + file);
                    }

                } catch (IOException ex) {
                    Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        errorBeans = jsGetReason(previewProjectRoot);
        if (!errorBeans.isEmpty()) {//prevent the generation of empty error bean instances
            codeErrorBeanDao.saveAll(errorBeans);
        }

        model.addAttribute("errors", errorBeans);
        model.addAttribute("url", "/preview/" + tempSessionId + "/index.html");
        model.addAttribute("tempSessionId", tempSessionId);
        model.addAttribute("goback_url", "/monaco/project_id/" + projectId);
        return "previewProject";
    }*/
    /*public List<CodeErrorBean> jsGetReason(File root, ProjectBean projectBean) {
        long timestamp=System.currentTimeMillis();
        JsHint jsHint = new JsHint();
        List<CodeErrorBean> codeErrors = new ArrayList<CodeErrorBean>();
        int seed = 0;
        for (File file : root.listFiles()) {
            if (file.getName().endsWith(".js")) {
                List<CodeErrorInfoBean> errors_info = new ArrayList<>();
                try {
                    String js = FileUtils.readFileToString(file, "utf-8");
                    List<pl.touk.jshint4j.Error> errors = jsHint.lint(js, "{esversion:6}");
                    for (pl.touk.jshint4j.Error e : errors) {
                        CodeErrorInfoBean codeErrorInfoBean = new CodeErrorInfoBean();
                        codeErrorInfoBean.setError_character(String.valueOf(e.getCharacter()));
                        codeErrorInfoBean.setError_linenum((String.valueOf(e.getLine())));
                        codeErrorInfoBean.setError_reason(e.getReason());
                        codeErrorInfoBean.setErrorinfoid(UUID.randomUUID().toString() + (seed++));
                        errors_info.add(codeErrorInfoBean);
                    }
                    //if (errors != null && errors.size() > 0) {//prevent the generation of empty error bean instances
                        CodeErrorBean codeErrorBean = new CodeErrorBean();
                        codeErrorBean.setErrorid(UUID.randomUUID().toString());
                        codeErrorBean.setStu_id(projectBean.getAuthor());
                        codeErrorBean.setProjectid(projectBean.getProjectid());
                        codeErrorBean.setJsfile_name(file.getName());
                        codeErrorBean.setJs_errors(errors_info);
                        codeErrorBean.setTimestamp(timestamp);
                        codeErrors.add(codeErrorBean);
                    //}

                } catch (IOException ex) {
                    Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return codeErrors;
    }*/
}
