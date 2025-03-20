/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sofa.Codenote.apis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sofa.Codenote.models.CodeDiffBean;
import sofa.Codenote.models.CodeDiffBeanDao;
import sofa.Codenote.models.CodeInfoBean;
import sofa.Codenote.models.NoteInfoBean;
import sofa.Codenote.models.ProjectBean;
import sofa.Codenote.models.ProjectBeanDao;

/**
 *
 * @author s1088
 */
@RestController
public class CodenoteDataOutput {

    @Autowired
    CodeDiffBeanDao codeDiffBeanDao = null;
    @Autowired
    ProjectBeanDao projectBeanDao = null;

    @GetMapping("getnotedata")
    public void getnotedata(String projectname) throws IOException {
        projectname = "Phaser Game";
        List<String> title = Arrays.asList("stuid", "projectname", "note");
        List<List<String>> output = new ArrayList<>();
        List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname(projectname);
        for (ProjectBean pb : projectBeans) {
            String author = pb.getAuthor();
            System.out.println(author);
            List<CodeInfoBean> codeInfoBeans = pb.getCodeinfoes();
            for (CodeInfoBean codeInfoBean : codeInfoBeans) {
                List<String> infolist = new ArrayList<>();
                NoteInfoBean noteInfoBean = codeInfoBean.getNoteInfoBean();
                String note = noteInfoBean.getNote_content();
                if (note.length() > 0 && note.trim().length() > 0) {
//                    note = note.replace("\n", " ");
//                    note = note.replace("\t", " ");
                    note=note.replaceAll("(\\s)+", " ");
                    infolist.add(author);
                    infolist.add(projectname);
                    infolist.add(note);
                    output.add(infolist);
                    System.out.println("author" + author + ":" + note);
                }
            }
        }

        FileUtils.writeStringToFile(new File(projectname + "_note.csv"), String.join(",", title.toArray(new String[0])) + "\r\n", "big5", false);
        for (List<String> row : output) {
            FileUtils.writeStringToFile(new File(projectname + "_note.csv"), String.join(",", row.toArray(new String[0])) + "\r\n", "big5", true);
        }
    }

    @GetMapping("data")
    public void getdata() throws IOException {
        List<String> title = Arrays.asList("stuid", "projectname", "timestamp", "t1", "t2", "t3", "t4", "t5", "t1_var", "t2_var", "t3_var", "t4_var", "t5_var", "t_var");
        List<String> projectnameList = Arrays.asList("javascript實作簡易版計算機", "jquery 圈圈叉叉");
        List<Map<String, String>> js_idList = new ArrayList<>();
        List<Map<String, String>> jq_idList = new ArrayList<>();

        for (String pn : projectnameList) {
            String id = "";
            String author = "";
            List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname(pn);
            for (ProjectBean p : projectBeans) {
                id = p.getProjectid();
                author = p.getAuthor();

                Map<String, String> map = new HashMap<>();
                map.put("projectid", id);
                map.put("author", author);

                if (p.getPrjectname().equals("javascript實作簡易版計算機")) {
                    js_idList.add(map);
                } else if (p.getPrjectname().equals("jquery 圈圈叉叉")) {
                    jq_idList.add(map);
                }
            }

        }
        List<List<String>> output = new ArrayList<>();
        List<List<String>> codediff_js = getCodeDiffData(js_idList, "javascript實作簡易版計算機");
        List<List<String>> codediff_jq = getCodeDiffData(jq_idList, "jquery 圈圈叉叉");

        for (List<String> o : codediff_js) {
            output.add(o);
        }
        for (List<String> o : codediff_jq) {
            output.add(o);
        }

        FileUtils.writeStringToFile(new File("vector3.csv"), String.join(",", title.toArray(new String[0])) + "\r\n", "utf-8", false);
        for (List<String> row : output) {
            FileUtils.writeStringToFile(new File("vector3.csv"), String.join(",", row.toArray(new String[0])) + "\r\n", "utf-8", true);
        }
    }

    private List<List<String>> getCodeDiffData(List<Map<String, String>> data, String pname) {
        List<List<String>> output = new ArrayList<>();
        for (Map<String, String> d : data) {
            String projectid = d.get("projectid");
            String author = d.get("author");
            List<CodeDiffBean> cdbs = (List<CodeDiffBean>) codeDiffBeanDao.findByProjectidAndAuthorOrderByTimestampAsc(projectid, author);
            if (cdbs.size() == 0 || cdbs.size() == 2) {
                System.out.println("empty" + author);
                continue;
            }
            int firstTimestamp = (int) cdbs.get(0).getTimestamp();
            int lastTimestamp = (int) cdbs.get(cdbs.size() - 1).getTimestamp();
            int timelag = (lastTimestamp - firstTimestamp) / 5;
            System.out.println("lag" + timelag + "times" + cdbs.size());
            int score = 0;
            int[] scores = {0, 0, 0, 0, 0};
            List<CodeDiffBean>[] OpeProcesses = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
            System.out.println(pname + author + " size :" + cdbs.size());

            for (CodeDiffBean cdb : cdbs) {
                List<String> status = new ArrayList<>();
                int lag = (int) (cdb.getTimestamp() - firstTimestamp);
////                System.out.println(cdb.getAuthor() + ": type:" + cdb.getDiffSource() + "time: " + cdb.getTimestamp());
                for (int i = 0; i <= 3; i++) {
                    if (lag <= timelag * (i + 1) && lag >= timelag * i) {
                        OpeProcesses[i].add(cdb);
                        if (cdb.getDiffSource().equals("note")) {
                            scores[i] = scores[i] - 1;
                        } else if (cdb.getDiffSource().equals("code")) {
                            scores[i] = scores[i] + 1;
                        }
                    } else {
                        continue;
                    }
                }
                if (lag > timelag * 4) {
                    OpeProcesses[4].add(cdb);
                    if (cdb.getDiffSource().equals("note")) {
                        scores[4] = scores[4] - 1;
                    } else if (cdb.getDiffSource().equals("code")) {
                        scores[4] = scores[4] + 1;
                    }
                }
            }
            List<String> outList = new ArrayList<>();
            outList.add(author);
            outList.add(pname);
            int Timestamp = lastTimestamp - firstTimestamp;
            outList.add(Integer.toString(Timestamp));
            for (int i = 0; i < 5; i++) {
                outList.add(String.valueOf(scores[i]));
            }
            for (int i = 0; i < OpeProcesses.length; i++) {
                if (OpeProcesses[i].size() != 0) {
                    List<Integer> OpeProcess = getOpeProcess(OpeProcesses[i]);
                    double var = getVar(OpeProcess);
                    if (Double.isNaN(var)) {
                        outList.add("0");
                    } else {
                        outList.add(String.valueOf(var));
                    }
                } else {
                    outList.add("0");
                }
            }

            List<Integer> t_var_OpeProcess = getOpeProcess(cdbs);
            double t_var = getVar(t_var_OpeProcess);
            if (!Double.isNaN(t_var)) {
                outList.add(String.valueOf(t_var));
            } else {
                outList.add("0");
            }
            output.add(outList);
        }
        return output;
    }

    private List<Integer> getOpeProcess(List<CodeDiffBean> cdbs) {
        List<Integer> OpeProcess = new ArrayList<>();
        int flagtime = 0;
        int nexttime = 0;
        for (CodeDiffBean cdb : cdbs) {
            if (flagtime == 0) {
                flagtime = (int) cdb.getTimestamp();
                continue;
            }
            nexttime = (int) cdb.getTimestamp();
            int time_len = nexttime - flagtime;
            int count = time_len / 30000;
            for (int i = 0; i < count; i++) {
                if (cdb.getDiffSource().equals("code")) {
                    OpeProcess.add(1);
                } else {
                    OpeProcess.add(0);
                }
            }
            flagtime = nexttime;
        }
        return OpeProcess;
    }

    private double getVar(List<Integer> OpeProcess) {
        // The mean average
        double mean = 0.0;
        for (int i = 0; i < OpeProcess.size(); i++) {
            mean += OpeProcess.get(i);
        }
        mean /= OpeProcess.size();
        // The variance
        double variance = 0;
        for (int i = 0; i < OpeProcess.size(); i++) {
            variance += Math.pow(OpeProcess.get(i) - mean, 2);
        }
        variance /= OpeProcess.size();
        return variance;
    }
    
    @GetMapping("data2")
    public void getdata2() throws IOException {
        List<String> title = Arrays.asList("stuid", "projectname", "timestamp", "t1_code", "t2_code", "t3_code", "t4_code", "t5_code","t1_note", "t2_note", "t3_note", "t4_note", "t5_note", "t1_switch", "t2_switch", "t3_switch", "t4_switch", "t5_switch");
        List<String> projectnameList = Arrays.asList("Phaser Rocket Game", "SpaceShooter", "Phaser Pumpkin Man");
        List<Map<String, String>> js_idList = new ArrayList<>();
        List<Map<String, String>> jq_idList = new ArrayList<>();
        List<Map<String, String>> jp_idList = new ArrayList<>();

        for (String pn : projectnameList) {
            String id = "";
            String author = "";
            List<ProjectBean> projectBeans = projectBeanDao.findByPrjectname(pn);
            for (ProjectBean p : projectBeans) {
                id = p.getProjectid();
                author = p.getAuthor();

                Map<String, String> map = new HashMap<>();
                map.put("projectid", id);
                map.put("author", author);

                if (p.getPrjectname().equals("Phaser Rocket Game")) {
                    js_idList.add(map);
                } else if (p.getPrjectname().equals("SpaceShooter")) {
                    jq_idList.add(map);
                } else if (p.getPrjectname().equals("Phaser Pumpkin Man")) {
                    jp_idList.add(map);
                }
            }

        }
        List<List<String>> output = new ArrayList<>();
        List<List<String>> codediff_js = getCodeDiffData2(js_idList, "Phaser Rocket Game");
        List<List<String>> codediff_jq = getCodeDiffData2(jq_idList, "SpaceShooter");
        List<List<String>> codediff_jp = getCodeDiffData2(jq_idList, "Phaser Pumpkin Man");

        for (List<String> o : codediff_js) {
            output.add(o);
        }
        for (List<String> o : codediff_jq) {
            output.add(o);
        }
        for (List<String> o : codediff_jp) {
            output.add(o);
        }

        FileUtils.writeStringToFile(new File("vector3.csv"), String.join(",", title.toArray(new String[0])) + "\r\n", "utf-8", false);
        for (List<String> row : output) {
            FileUtils.writeStringToFile(new File("vector3.csv"), String.join(",", row.toArray(new String[0])) + "\r\n", "utf-8", true);
        }
    }
    
    private List<List<String>> getCodeDiffData2(List<Map<String, String>> data, String pname) {
        List<List<String>> output = new ArrayList<>();
        for (Map<String, String> d : data) {
            String projectid = d.get("projectid");
            String author = d.get("author");
            List<CodeDiffBean> cdbs = (List<CodeDiffBean>) codeDiffBeanDao.findByProjectidAndAuthorOrderByTimestampAsc(projectid, author);
            if (cdbs.size() == 0 || cdbs.size() == 2) {
                System.out.println("empty" + author);
                continue;
            }
            long firstTimestamp = cdbs.get(0).getTimestamp();
            long lastTimestamp = cdbs.get(cdbs.size() - 1).getTimestamp();
            long deltaTimestamp=60000;
            long totalTime=lastTimestamp-firstTimestamp;
            int [] countCodePerMinute=new int[5];
            int [] countNotePerMinute=new int[5];
            int [] countSwitchPerMinute=new int[5];
            int currentTimeDeltaMultiply=1;
            String lastSource=null;
            for (CodeDiffBean cdb : cdbs) {
                if(cdb.getTimestamp()>(firstTimestamp+currentTimeDeltaMultiply*deltaTimestamp)){
                    currentTimeDeltaMultiply++;
                }
                if(currentTimeDeltaMultiply>5){
                    break;//only capture the first five minutes
                }
                if(lastSource!=null && cdb.getDiffSource().equals(lastSource)==false){
                    countSwitchPerMinute[currentTimeDeltaMultiply-1]++;
                }
                lastSource=cdb.getDiffSource();
                if(cdb.getDiffSource().equals("code")){
                    countCodePerMinute[currentTimeDeltaMultiply-1]++;
                }else{
                    countNotePerMinute[currentTimeDeltaMultiply-1]++;
                }
            }
            List<String> row=new ArrayList<>(List.of(author, projectid, ""+totalTime));
            for(int count : countCodePerMinute){
                row.add(""+count);
            }
            for(int count : countNotePerMinute){
                row.add(""+count);
            }
            for(int count : countSwitchPerMinute){
                row.add(""+count);
            }
            output.add(row);
        }
        return output;
    }

}
