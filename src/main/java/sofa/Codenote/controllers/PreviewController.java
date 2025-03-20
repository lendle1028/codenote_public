/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.services.PreviewService;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;

/**
 *
 * @author s1088
 */
@Controller
public class PreviewController {

    @Autowired
    private PreviewService previewService = null;
//    @Autowired
//    private CodeErrorBeanDao codeErrorBeanDao = null;

//    @Autowired
//    private SampleProjectBeanDao sampleProjectBeanDao = null;
//
    @Autowired
    private ProjectBeanDao projectBeanDao = null;
//
//    @Autowired
//    private CodeErrorBeanDao codeErrorBeanDao = null;
//
//    ProjectBean projectBean = new ProjectBean();

    @GetMapping("/previewSampleCode")
    public String previewSampleProject(@RequestParam String projectId, Model model, HttpServletRequest request) {
        String tempSessionId = previewService.previewSampleProject(projectId);
//        SampleProjectBean sampleProjectBean = sampleProjectBeanDao.findById(projectId).get();
//        List<SampleCodeBean> list = sampleProjectBean.getSamplecodes();
//        File root = new File("preview").getAbsoluteFile();
//        String tempSessionId = UUID.randomUUID().toString();
//        File previewProjectRoot = new File(root, tempSessionId);
//        if (!previewProjectRoot.exists()) {
//            previewProjectRoot.mkdirs();
//        }
//        for (SampleCodeBean sampleCodeBean : list) {
//            File file = new File(previewProjectRoot, sampleCodeBean.getCode_nmae());
//            try {
//                FileUtils.write(file, sampleCodeBean.getCode_content(), "utf-8");
//            } catch (IOException ex) {
//                Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        System.out.println(previewProjectRoot.getAbsolutePath());

        model.addAttribute("url", "/preview/" + tempSessionId + "/index.html");
        model.addAttribute("goback_url", "/manageSampleCode?id=" + projectId);
        return "previewProject";
    }

    @GetMapping("/previewCode")
    @Transactional
    public String previewProject(@RequestParam String projectId, @RequestParam(defaultValue="false") boolean inspectionMode, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ProjectBean projectBean = projectBeanDao.findById(projectId).get();
        String tempSessionId = projectBean.getFolderId();
        if (tempSessionId == null) {
            tempSessionId = UUID.randomUUID().toString();
            projectBean.setFolderId(tempSessionId);
            projectBeanDao.save(projectBean);
        }
        File root = new File("preview").getAbsoluteFile();
        File previewProjectRoot = new File(root, tempSessionId);
        if (!previewProjectRoot.exists()) {
            previewProjectRoot.mkdirs();
            List<CodeInfoBean> list = projectBean.getCodeinfoes();
            for (CodeInfoBean codeInfoBean : list) {
                File file = new File(previewProjectRoot, codeInfoBean.getCode_nmae());
                String html_src = null;
                try {
                    String src = codeInfoBean.getCode_content();
                    FileUtils.write(file, src, "utf-8");
//                    if (file.getName().endsWith(".html") && codeInfoBean.timestamp <= comparetime) {
//                        src = src.replace(".js\"", ".js?" + System.currentTimeMillis() + "\"");
//                        FileUtils.write(file, src, "utf-8");
//                        System.out.println("output " + file + ":" + codeInfoBean.timestamp + ":" + comparetime);
//                    } else if (file.lastModified() <= codeInfoBean.timestamp) {
//                        FileUtils.write(file, src, "utf-8");
//                        System.out.println("output " + file);
//                    }

                } catch (IOException ex) {
                    Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
//        Optional<CodeErrorBean> codeErrorBeanOptional=codeErrorBeanDao.findTopByProjectidOrderByTimestampDesc(projectId);
//        List<CodeErrorBean> errorBeans=new ArrayList<>();
//        if(codeErrorBeanOptional.isPresent()){
//            CodeErrorBean codeErrorBean=codeErrorBeanOptional.get();
////            System.out.println("dao bean: "+codeErrorBean.getTimestamp());
//            errorBeans = codeErrorBeanDao.findByProjectidAndTimestamp(projectId, codeErrorBean.getTimestamp());
//            errorBeans=errorBeans.stream().filter(e->(e.getJs_errors()!=null && e.getJs_errors().size()>0)).collect(Collectors.toList());
//        }
//        System.out.println("errorBeans.1="+errorBeans+", "+"errorBeans." + projectId);
//        model.addAttribute("errors", errorBeans);
        model.addAttribute("url", "/preview/" + tempSessionId + "/index.html?preview=true&projectId="+projectBean.getProjectid());
        model.addAttribute("projectId", projectBean.getProjectid());
        model.addAttribute("tempSessionId", tempSessionId);
        model.addAttribute("inspectionMode", inspectionMode);//error inspection will be enabled in inspection mode
//        model.addAttribute("goback_url", "/monaco/project_id/" + projectId);
        return "previewProject";
        //use timestamp.${projectId} to track modified time
        /*HttpSession session=request.getSession();
        ProjectBean projectBean=(ProjectBean) session.getAttribute("projectBean."+projectId);
        String tempSessionId=null;
        if(projectBean==null){
            //reload it
            tempSessionId=previewService.resetProjectReviewFolder(projectId, null, request);
        }else{
            tempSessionId=projectBean.folderId;
        }
        List<CodeErrorBean> errorBeans = (List<CodeErrorBean>) request.getSession().getAttribute("errorBeans."+projectId);
        model.addAttribute("errors", errorBeans);
        model.addAttribute("url", "/preview/" + tempSessionId + "/index.html");
        model.addAttribute("tempSessionId", tempSessionId);
        model.addAttribute("goback_url", "/monaco/project_id/" + projectId);
        return "previewProject";*/
 /*String sessionTimestampString = (String) request.getSession().getAttribute("timestamp." + projectId);
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
        return "previewProject";*/
    }

    /*public List<CodeErrorBean> jsGetReason(File root) {
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
                    if (errors != null && errors.size() > 0) {//prevent the generation of empty error bean instances
                        CodeErrorBean codeErrorBean = new CodeErrorBean();
                        codeErrorBean.setErrorid(UUID.randomUUID().toString());
                        codeErrorBean.setStu_id(projectBean.getAuthor());
                        codeErrorBean.setProjectid(projectBean.getProjectid());
                        codeErrorBean.setJsfile_name(file.getName());
                        codeErrorBean.setJs_errors(errors_info);
                        codeErrorBean.setTimestamp(System.currentTimeMillis());
                        codeErrors.add(codeErrorBean);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PreviewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return codeErrors;
    }*/
}
