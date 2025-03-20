/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

function getName(){
    return "Readings";
}

function getId(){
    return "readings";
}

function getHtml(){
    return `
        <a v-bind:href="project.sampleProjectBean.sampleprojectnote_path" target="_blank" v-if="project.sampleProjectBean.sampleprojectnote_path!=null">Open Readings</a>
    `;
}

function init(){
    let v=new Vue({
        el: "#readings",
        data: {
            project: project
        }
    });
//    console.log(project);
}

export{getId, getName,getHtml,init};
