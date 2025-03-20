/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

function getName(){
    return "Files";
}

function getId(){
    return "files";
}

function getHtml(){
    return `
        <select id="sampleProjectBeans" v-model="selectedSampleProjectBean">
            <option v-for="sampleProjectBean in sampleProjectBeans" :value="sampleProjectBean" :key="sampleProjectBean.projectid_ex">{{sampleProjectBean.projectname_ex}}</option>
        </select><button v-on:click="switchSampleProject();">GO</button>
        <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Code Title</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="code in project.codeinfoes">
                                        <td style="cursor: pointer" v-on:click="loadCode(code);">{{code.code_nmae}}</td>
                                    </tr>
                                </tbody>
        </table>
    <div style="display:none">
        <form method="post" action="/createproject" id="explorer_form">
            <input type="text" name="stuid" value="11111111" id="explorer_stuid"/>
            <input type="text" name="progectname" value="11111111" id="explorer_progectname"/>
        </form>
    </div>
    `;
}

function init(){
    let v=new Vue({
        el: "#files",
        data: {
            project: project,
            sampleProjectBeans: [],
            selectedSampleProjectBean: {}
        },
        methods: {
            loadCode: function(code){
//                console.log(app);
                app.loadCode(code.codeid);
            },
            switchSampleProject: function(){
                console.log(v.selectedSampleProjectBean);
                $("#explorer_stuid").val(stuid);
                $("#explorer_progectname").val(v.selectedSampleProjectBean.projectname_ex);
                $("#explorer_form").submit();
            }
        }
    });
    $.ajax("/api/sampleProjectBean", {
        success: function(list){
            v.sampleProjectBeans=list;
            for(let o of list){
                if(o.projectname_ex==project.sampleProjectBean.projectname_ex){
                    v.selectedSampleProjectBean=o;
                    console.log(o.projectname_ex+":"+project.sampleProjectBean.projectname_ex);
                    break;
                }
            }
        }
    });
}

export{getId, getName,getHtml,init};
