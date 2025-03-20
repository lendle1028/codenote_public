/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

function getName(){
    return "Readings2";
}

function getId(){
    return "ai_readings";
}

function getHtml(){
    return `
        <a v-bind:href="'http://imsofa.rocks:1025/tutor/phaserTutor?query='+query" target="_blank" v-if="project.sampleProjectBean.sampleprojectnote_path!=null">Open Readings</a>
    `;
}

function init(){
    let v=new Vue({
        el: "#ai_readings",
        data: {
            project: project,
            query: ""
        }
    });
    console.log(project);
    if(project.sampleProjectBean.projectname_ex=="Phaser Game Lab 2-1"){
        v.query="this.physics.add.group";
    }else if(project.sampleProjectBean.projectname_ex=="Phaser Game Lab 2-2"){
        v.query="this.physics.add.overlap";
    }else if(project.sampleProjectBean.projectname_ex=="Phaser Game Lab 2-3"){
        v.query="this.physics.add.staticGroup";
    }else if(project.sampleProjectBean.projectname_ex=="Phaser Game Lab 2-4"){
        v.query="this.cursors.left.isDown";
    }else if(project.sampleProjectBean.projectname_ex=="Phaser Game Lab 2-5"){
        v.query="limit the fire rate of bullet";
    }
    
}

export{getId, getName,getHtml,init};
