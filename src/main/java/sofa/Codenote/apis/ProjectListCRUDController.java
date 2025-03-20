/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.SampleCodeBean;
import sofa.Codenote.models.SampleProjectBean;
import sofa.Codenote.models.SampleProjectBeanDao;

/**
 *
 * @author s1088
 */
@RestController
public class ProjectListCRUDController {

    @Autowired
    private SampleProjectBeanDao sampleProjectBeanDao = null;
    
    @GetMapping("/api/projectList")
    @Transactional
    public List<SampleProjectBean> get() {
        List<SampleProjectBean> sampleprojectes =sampleProjectBeanDao.findAll();
        if(sampleprojectes==null || sampleprojectes.isEmpty()){
            SampleProjectBean bean=new SampleProjectBean();
            bean.setProjectid_ex("sample1");
            bean.setProjectname_ex("Game Elements with Canvas");
            bean.setSampleprojectnote_path("https://docs.google.com/document/d/157ox0HFTifePi-LiWvUui_Kfo2D2kXUU/edit?usp=sharing&ouid=105108459520810918696&rtpof=true&sd=true");
            List<SampleCodeBean> list=new ArrayList<>();
            SampleCodeBean sampleCodeBean=new SampleCodeBean();
            sampleCodeBean.setCodeid("sample1_code1");
            sampleCodeBean.setCode_nmae("index.html");
            sampleCodeBean.setCode_content("<!DOCTYPE html>\n" +
"<!--\n" +
"目標：讓 mario 可以左右移動，並在接觸到 star 的時候，讓 star 消失，\n" +
"且分數 +1\n" +
"-->\n" +
"<html lang=\"en\">\n" +
"  <head>\n" +
"    <title>Hello!</title>\n" +
"    <meta charset=\"utf-8\" />\n" +
"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
"  </head>\n" +
"  <body>\n" +
"    <span id=\"score\"></span><br/>\n" +
"    <img style=\"display: none\" id=\"mario\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/pngaaa.com-1198218.png?v=1640742982064\"/>\n" +
"    <img style=\"display: none\" id=\"star\" src=\"https://cdn.glitch.me/372373dd-bb74-4b00-b938-bbf796cf8ebf/star.png?v=1640743662784\"/>\n" +
"    <canvas id=\"c\" width=\"500\" height=\"500\"></canvas>\n" +
"    <script>\n" +
"      let score=0;\n" +
"      let canvas = document.getElementById(\"c\");\n" +
"      let mario=document.getElementById(\"mario\");\n" +
"      let star=document.getElementById(\"star\");\n" +
"      let ctx = canvas.getContext(\"2d\");\n" +
"\n" +
"      let components = [];\n" +
"      let round = 0;\n" +
"      function loop() {\n" +
"        document.getElementById(\"score\").innerHTML=\"\"+score;\n" +
"        ctx.save();\n" +
"        ctx.clearRect(0, 0, canvas.width, canvas.height);\n" +
"        \n" +
"        for (let component of components) {\n" +
"          if (component.getType() == \"Obstacle\") {\n" +
"            if (round++ % 3 == 0 && Math.random() < 0.4) {\n" +
"              component.y = component.y + 5;\n" +
"              if (Math.random() < 0.2) {\n" +
"                component.x = component.x + 1;\n" +
"              } else if (Math.random() > 0.8) {\n" +
"                component.x = component.x - 1;\n" +
"              }\n" +
"            }\n" +
"          }\n" +
"          component.update();\n" +
"        }\n" +
"        //now, we check overlap between all pairs of components\n" +
"        outer: for(let i=0; i<components.length; i++){\n" +
"          for(let j=i+1; j<components.length; j++){\n" +
"            if(!components[i].visible || !components[j].visible){\n" +
"              //then skip\n" +
"              continue;\n" +
"            }\n" +
"            if(isOverlap(components[i], components[j])){\n" +
"              //hint: 檢查碰撞的雙方，進行條件及分數的判定\n" +
"            }\n" +
"          }\n" +
"        }\n" +
"        let oldComponents=components;\n" +
"        components=[];\n" +
"        //check game state & collect new sets of components to save memory\n" +
"        for(let component of oldComponents){\n" +
"          if(!component.visible){\n" +
"            continue;\n" +
"          }\n" +
"          \n" +
"          if(component.y>=0 && component.y<=canvas.height){\n" +
"            components.push(component);\n" +
"          }\n" +
"        }\n" +
"        \n" +
"        ctx.restore();\n" +
"        if (true) {\n" +
"          setTimeout(function() {\n" +
"            window.requestAnimationFrame(loop);\n" +
"          }, 1000 / 25);\n" +
"        }\n" +
"      }\n" +
"      window.requestAnimationFrame(loop);\n" +
"\n" +
"      class Component {\n" +
"        constructor(x, y, width, height) {\n" +
"          this.x = x;\n" +
"          this.y = y;\n" +
"          this.width = width;\n" +
"          this.height = height;\n" +
"          this.visible = true;\n" +
"        }\n" +
"        getType() {\n" +
"          return \"unknown\";\n" +
"        }\n" +
"        update() {}\n" +
"      }\n" +
"\n" +
"      class MainComponent extends Component {\n" +
"        constructor(x, y) {\n" +
"          super(x, y, 100, 150);\n" +
"        }\n" +
"        getType() {\n" +
"          return \"MainComponent\";\n" +
"        }\n" +
"        update() {\n" +
"          if (this.visible) {\n" +
"            ctx.drawImage(mario, 0, 0, 200, 307, this.x, this.y, 50, 80);\n" +
"          }\n" +
"        }\n" +
"      }\n" +
"\n" +
"      class Obstacle extends Component {\n" +
"        constructor(x, y) {\n" +
"          super(x, y, 30, 30);\n" +
"        }\n" +
"        getType() {\n" +
"          return \"Obstacle\";\n" +
"        }\n" +
"        update() {\n" +
"          if (this.visible) {\n" +
"            ctx.drawImage(star, 0, 0, 30, 30, this.x, this.y, 30, 30);\n" +
"          }\n" +
"        }\n" +
"      }\n" +
"\n" +
"      function isOverlap(obj1, obj2) {\n" +
"        let overlap = !(\n" +
"          obj1.x + obj1.width < obj2.x ||\n" +
"          obj1.x > obj2.x + obj2.width ||\n" +
"          obj1.y + obj1.height < obj2.y ||\n" +
"          obj1.y > obj2.y + obj2.height\n" +
"        );\n" +
"        return overlap;\n" +
"      }\n" +
"\n" +
"      let mainComponent = new MainComponent(\n" +
"        canvas.width / 2,\n" +
"        canvas.height/2\n" +
"      );\n" +
"      components.push(mainComponent);\n" +
"      for (let y = 0; y < 10; y++) {\n" +
"        for (let x = 0; x < 5; x++) {\n" +
"          let ob1 = new Obstacle(\n" +
"            Math.floor(Math.random() * canvas.width),\n" +
"            (y + 1) * 5\n" +
"          );\n" +
"          components.push(ob1);\n" +
"        }\n" +
"      }\n" +
"\n" +
"      function onKeyDown(e) {\n" +
"        //hint: 讓 mario 可以左右移動\n" +
"      }\n" +
"      window.addEventListener(\"keydown\", onKeyDown);\n" +
"    </script>\n" +
"  </body>\n" +
"</html>");
            list.add(sampleCodeBean);
            bean.setSamplecodes(list);
            sampleProjectBeanDao.save(bean);
            sampleprojectes =sampleProjectBeanDao.findAll();
        }
        return sampleprojectes;
    }

}
