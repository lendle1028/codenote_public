/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.servlets;

import java.io.File;
import java.io.IOException;
import static java.lang.System.out;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;

/**
 *
 * @author USER
 */
@WebServlet(urlPatterns = {"*.html", "*.htm"})
@Component
public class HtmlFileServlet extends HttpServlet {
    @Autowired
    private ProjectBeanDao ProjectBeanDao=null;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
//        System.out.println(req.getRequestURL()+req.getParameter("preview"));
        File file = null;
        if(req.getRequestURI().contains("?")){
            file=new File(req.getRequestURI().substring(1, req.getRequestURI().indexOf("?")));
        }else{
            file=new File(req.getRequestURI().substring(1));
        }
        String src = FileUtils.readFileToString(file, "utf-8");
        String preview=req.getParameter("preview");
        if ("true".equals(preview)) {
            src = src.replace(".js\"", ".js?A" + System.currentTimeMillis() + "\"");
            
            src = src.replace("<head>", """
                                   <head><script>
                                   window.onerror=function(e){
                                                              window.parent.iframeError(event);
                                   }
                                   </script>
            """);
            String projectId=req.getParameter("projectId");
            ProjectBean projectBean=ProjectBeanDao.findById(projectId).get();
            if(projectBean.getSampleProjectBean().getInspectionScript()!=null){
                //inject inspection script for automated testing
                src=src.replace("</body>", "<script>"+projectBean.getSampleProjectBean().getInspectionScript()+"</script></body>");
//                System.out.println(src);
            }
        }
        resp.getWriter().println(src);
    }

}
