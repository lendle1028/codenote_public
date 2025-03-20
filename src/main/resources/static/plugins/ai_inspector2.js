/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
let v=null;
let collectingErrors=true;
let ticketId=null;
let currentErrorCheckingThread=null;
let distinctMessageMap={};//prevent duplication

let state="ERROR_CHECKING";//ERROR_CHECKING, ERROR_FIXING, CLEAN

function errorFired(e){
    if(state!="ERROR_CHECKING"){
        return;
    }
    let key=e.error.message+":"+toSimpleFileName(e.filename)+":"+e.lineno;
    if(distinctMessageMap[key]){
        return;
    }
    distinctMessageMap[key]="exists";
    e.simpleFileName=toSimpleFileName(e.filename);
    v.errors.push(e);
}

function onSaveFired(fileName){
    $("#previewButton").attr("disabled", "disabled");
    if(state=="ERROR_CHECKING"){
        return;
    }else if(state=="ERROR_FIXING"){
        $("#ai_inspector2_nextContainer").show();
        $("#ai_inspector2_next_go").removeAttr("disabled");
        return;
    }else if(state=="CLEAN"){
        state="ERROR_CHECKING";
        startErrorCheckingThread();
    }
}

function startErrorCheckingThread(){
    if(currentErrorCheckingThread!=null){
        //terminate current checking
        clearTimeout(currentErrorCheckingThread);
        currentErrorCheckingThread=null;
    }
    $("#ai_inspector2_next_go").attr("disabled", "disabled");
    $("#ai_inspector2_nextContainer").hide();
    $("#ai_inspector2_animation").show();
    $("#ai_inspector2_iframe").attr('src',v.app.url+"&inspectionMode=true");
     v.errors=[];
    currentErrorCheckingThread=setTimeout(function(){
        //done error checking, show results
        if(v.errors.length>0){//error found
            state="ERROR_FIXING";
//            $("#ai_inspector2_topContainer").show();
            $("#ai_inspector2_go").removeAttr("disabled");
            var someTabTriggerEl = document.querySelector('#ai_inspector2Tab')
            var tab = new bootstrap.Tab(someTabTriggerEl)
            tab.show();
        }else{
            state="CLEAN";
        }
        let errorInputs=jsError2CodeCouchInput(v.errors);
        saveErrorToDB(errorInputs);
        currentErrorCheckingThread=null;
        $("#ai_inspector2_animation").hide();
    }, project.sampleProjectBean.inspectionLatency);
}

function getName(){
    return "AI";
}

function getId(){
    return "ai_inspector2";
}

function getHtml(){
    return `
        <center>
            <img src="eyes-joypixels.gif" style="display: none; width: 50px" id="ai_inspector2_animation"/>
        </center>
        <center class="mt-2" id="ai_inspector2_topContainer" style="display: none">
            <button id="ai_inspector2_go" v-on:click="askAI();" style="width: 200px">
                <span class="bi bi-bug-fill"></span>
            </button>
            <p style="color:red">AI 幫幫忙</p>
        </center>
        <center class="mt-2" id="ai_inspector2_nextContainer" style="display: none">
            <button id="ai_inspector2_next_go" v-on:click="nextRound();" style="width: 200px" disabled="disabled">
                <span class="bi bi-arrow-right-square-fill"></span>
            </button>
            <p style="color:red">我改好了，再幫我看看</p>
        </center>
        <div>
            <iframe v-bind:src="app.url+'&inspectionMode=true'" style="display: none" id="ai_inspector2_iframe"></iframe>
            <ul class="list-group">
                <li class="list-group-item" v-for="e in errors">{{e.simpleFileName}} {{e.lineno}}  {{e.error.message}}</li>
            </ul>
        </div>
        <!--button id="ai_inspector2_clear" class="btn btn-link" onclick="$('#ai_inspector2_reply').html('');" style="display: none">清空</button-->
        <div id="ai_inspector2_reply" style="width: 100%">
           
        </div>
    `;
}

function resetErrorQueue(){
    $("#ai_inspector2_animation").hide();
    $("#ai_inspector2_iframe").attr('src','about:blank');
    v.errors=[];
    distinctMessageMap={};
    $("#ai_inspector2_topContainer").hide();
    $(".list-group").show();
}

function jsError2CodeCouchInput(jsErrors){
    let errorInputs = [];
    for (let e of jsErrors) {
        errorInputs.push({
            message: e.error.message,
            fileName: e.filename,
            lineNumber: e.lineno,
            type: e.error.stack.substring(0, e.error.stack.indexOf(":"))
        });
    }
    return errorInputs;
}

function init(){
    v=new Vue({
        el: "#ai_inspector2",
        data: {
            project: project,
            app: app,
            errors: []
        },
        methods: {
            askAI: function(){
                $(".list-group").hide();
                $('#ai_inspector2_reply').html('');
                $("#ai_inspector2_go").attr("disabled", "true");
                if(v.errors.length==0){
                    resetErrorQueue();
                    return;
                }
                $("#ai_inspector2_animation").show();
                let errorInputs=jsError2CodeCouchInput(v.errors);
                $.ajax("/ai/codeCoach", {
                    contentType: "application/json;charset=utf-8",
                    type: "POST",
                    data: JSON.stringify({
                        projectId: project.projectid,
                        fileFilter: [],
                        errors: errorInputs
                    }),
                    success: function(d){
                        ticketId=d;
                        setTimeout(getResultAsync, 5000);
                        
                        /*$("#ai_inspector2_clear").show();
                        $("#ai_inspector2_reply").html(d.message);
                        resetErrorQueue();*/
                    },
                    error: function(){
                        resetErrorQueue();
                    }
                });
            },
            nextRound: function(){
                state="ERROR_CHECKING";
                $('#ai_inspector2_reply').html('');
                resetErrorQueue();
                startErrorCheckingThread();
            }
        }
    });
    window.addErrorListener(errorFired);
    window.addOnSaveListener(onSaveFired);
    $("#previewButton").attr("disabled", "disabled");
    //first time inspection
    startErrorCheckingThread();
    
    /*setTimeout(function(){
        
        if(v.errors.length>0){
            v.checkErrors();
        }else{
            resetErrorQueue();
        }
        
    }, 5000);*/
}

function getResultAsync(){
    $.ajax("/ai/codeCoach/result/"+ticketId, {
        contentType: "application/json;charset=utf-8",
        success: function(d){
            if(d){
                if(d.error){
                    //error happens, run again
                    console.log("Error happens, execute again..."+d.message);
                    setTimeout(function(){
//                        checkingErrors=false;
                        v.askAI();
                    }, 5000);
                }else{
                    try{
                        $("#ai_inspector2_clear").show();
                        d=JSON.parse(d.message);
                        for (let i = 0; i < d.length; i++) {
                            /*let oldCode = d[i].oldCode;
                            let newCode = d[i].newCode;
                            let diff = Diff.createPatch(d[i].file, oldCode, newCode);*/
                            let cell = document.createElement("div");
                            /*$(cell).html(`
                                        <a class="fileName_holder" href="#"></a><br/>
                                        <div class="suggesion_holder"></div><br/>
                                        <div class="diff_holder"></div>
                                     `);*/
                            $(cell).html(`
                                  <div class="card-body">
                                    <h5 class="card-title">John Doe</h5>
                                    <p class="card-text">Some example text.</p>
                                    <div class="card-footer"><a href="#" class="btn btn-primary">Open</a></div>
                                  </div>
                            `);
                            $(cell).addClass("card");
                            $(cell).find(".card-title").text(d[i].file);
                            $(cell).find(".btn").get(0).onclick=function(){
                                openFile(d[i].file);
                            };
                            $(cell).find(".card-text").html(d[i].suggest);
                            $("#ai_inspector2_reply").append(cell);
                            /*const targetElement = $(cell).find(".diff_holder").get(0);
                            var configuration = {
                                drawFileList: false,
                                fileListToggle: false,
                                fileListStartVisible: false,
                                fileContentToggle: false,
                                matching: 'lines',
                                outputFormat: 'side-by-side',
                                synchronisedScroll: true,
                                highlight: true,
                                renderNothingWhenEmpty: false,
                            };
                            const diff2htmlUi = new Diff2HtmlUI(targetElement, diff, configuration);
                            diff2htmlUi.draw();
                            diff2htmlUi.highlightCode();
                            $($(targetElement).find(".d2h-file-side-diff").get(0)).css("display", "none");*/
                        }
                        $("#ai_inspector2_topContainer").hide();
                        $("#ai_inspector2_nextContainer").show();
                        resetErrorQueue();
                    }catch(e){
                        console.log(e);
                        setTimeout(function(){
                            v.askAI();
                        }, 5000);
                    }
                }
            }else{
                setTimeout(getResultAsync, 5000);
            }
        },
        error: function(){
            resetErrorQueue();
        }
    });
}

function saveErrorToDB(errors){
    $.ajax("/api/saveJsErrors/projectId/"+project.projectid+"/author/"+project.author,{
        type: "POST",
        contentType: "application/JSON;charset=utf-8",
        data: JSON.stringify(errors),
        success: function(){
            $("#previewButton").removeAttr("disabled");
        },
        error: function(){
            $("#previewButton").removeAttr("disabled");
        }
    });
}

function openFile(f){
    for(let codeinfo  of project.codeinfoes){
        if(codeinfo.code_nmae==f){
            app.loadCode(codeinfo.codeid);
            return;
        }
    }
}

function toSimpleFileName(str){
    let index1=str.lastIndexOf("/");
    let index2=str.indexOf("?");
    return str.substring(index1+1, index2);
}

export{getId, getName,getHtml,init};
