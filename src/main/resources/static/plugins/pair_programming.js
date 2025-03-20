/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

import { createClient }  from "https://cdn.jsdelivr.net/npm/@liveblocks/client@1.7.1/+esm";
import LiveblocksProvider from "https://cdn.jsdelivr.net/npm/@liveblocks/yjs@1.7.1/+esm";
import * as Y from "https://cdn.jsdelivr.net/npm/yjs@13.6.9/+esm";
import { MonacoBinding }  from "https://cdn.jsdelivr.net/npm/y-monaco@0.1.5/+esm";
import { createMutex } from 'https://esm.sh/gh/dmonad/lib0@v0.2.88/mutex.js'

let mux=null;
function getName(){
    return "PG";
}

function getId(){
    return "pg";
}

function getHtml(){
    return `
        <h5>PG <span id="pg_role"></span></h5>
        <div class="container" style="height: 95%">
           <select class="form-control col-8" v-model="selectedRoom">
                <option v-for="(room, index) in rooms" v-bind:value="index">{{room.roomName}}</option>
           </select>
           <div class="row">
            <button class="col-6 btn btn-info " v-on:click="joinRoom(selectedRoom);">Join</button>
            <button class="col-6 btn btn-danger " v-on:click="leaveRoom(selectedRoom);">Leave</button>
           </div>

           <div class="row container d-flex justify-content-center" id="chat-container" >
            <div class="card mt-1" style="height: 300px; overflow: auto">
              <div class="d-flex flex-row justify-content-between p-1 adiv text-white">
                <i class="fas fa-chevron-left"></i>
                <span class="pb-3">Live chat</span>
                <i class="fas fa-times"></i>
              </div>
              
            </div>
            <div class="row form-group px-3">
                  <input type="text" class="col-8" placeholder="Type your message" id="newChatMessage"/>
                  <button class="btn btn-info col-4" v-on:click="sendChatMessage();" id="buttonSendChatMessage" disabled>SEND</button>
            </div>
          </div>
        </div>
    `;
}

let leaveRoom=null;
let yDoc=null;
let client=null;
let yProvider=null;
let pusher=null;
let pg_reader_editor=null;

function init(){
    mux=createMutex();
    let v=new Vue({
        el: "#pg",
        data: {
            project: project,
            rooms: [],
            selectedRoom: ""
        },
        methods:{
            joinRoom: function (r) {
                client = createClient({
                    publicApiKey: "pk_dev_1y2L9SCWabu2QTLcsrWl0QqjqnoMgdLEayw1IPNNol42kwYfjwkeySqGAP_nbM41",
                });
                r=v.rooms[r];
                if(r.writable==false){
                    $("#pg_role").text("[Reader]");
                    //for reader, create a readonly instance of monaco editor in the
                    //codeUtilitiesTabs
                    let tabLi=document.createElement("li");
                    $(tabLi).attr("role", "tab");
                    $(tabLi).addClass("nav-item");
                    $(tabLi).html(
                        `
                            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#pg_code_container" type="button" role="tab" aria-controls="pg_code_container" aria-selected="false">PG</button>
                        `
                    );
                    $("#codeUtilitiesTabs").append(tabLi);
                    
                    let div=document.createElement("div");
                    $(div).attr("id", "pg_code_container");
                    $(div).addClass("p-0");
                    $(div).addClass("tab-pane");
                    $(div).attr('style', 'height: 580px; border: 0px solid #D5DBDB; width: 98%');
                    $("#codeUtilitiesTabsContent").append(div);
                    require(['vs/editor/editor.main'], function () {
                        pg_reader_editor = monaco.editor.create(div, {
                            language: "javascript",
                            theme: 'vs',
                            automaticLayout: true
                        });
                        pg_reader_editor.updateOptions({ readOnly: true });
                    });
                    
                }else{
                    $("#pg_role").text("[Writer]");
                }
                const {room, leave} = client.enterRoom(r.roomName, {
                    initialPresence: { cursor: null },
                    initialStorage: { monaco: app.editor.getValue() },
                });
                leaveRoom=leave;
                yDoc = new Y.Doc();
                yProvider = new LiveblocksProvider(room, yDoc);
                const yFileName=yDoc.getText("fileName");
                const yMap=yDoc.getMap("data");
                
                window.addOnOpenedListener((fileName)=>{
                    yDoc.transact(()=>{
                        if(r.writable){
                            mux(()=>{
                                if (yFileName.length != 0) {
                                    yFileName.delete(0, yFileName.length);
                                }
                                yFileName.insert(0, fileName);
                            });
                        }
                    });                    
                });
                yDoc.on('afterAllTransactions', function(t){
                    if(r.writable==false){
                        mux(()=>{
                            let codelan = yFileName.toString();
                            let codelan_num = codelan.indexOf(".");
                            codelan = codelan.substring(codelan_num + 1);
                            if (codelan == "js") {
                                codelan = "javascript";
                            } else if (codelan == "html") {
                                codelan = "html";
                            }
                            console.log(pg_reader_editor.setModelLanguage);
//                            pg_reader_editor.setModelLanguage(app.editor.getModel(), codelan);
                        });
                    }
                });

                yMap.observe((e)=>{
                    if(r.writable==false){
                        mux(()=>{
                            console.log(pg_reader_editor.setValue);
                            pg_reader_editor.setValue(yMap.get("value"));
                            let position=JSON.parse(yMap.get("position"));
                            pg_reader_editor.setPosition(position);
                            pg_reader_editor.revealLine(position.lineNumber);
                            pg_reader_editor.getModel().deltaDecorations([], [
                                {
                                  range: new monaco.Range(position.lineNumber, 1, position.lineNumber, 1),
                                  options: {
                                    isWholeLine: true,
                                    inlineClassName: "highlight",
                                  }
                                },
                              ]);
                        });
                        
                    }
                });
                
                
                app.addEditorUpdateListener(function(event){
                    if(r.writable){
                        mux(()=>{
                            yDoc.transact(()=>{
                                yMap.set("value", app.editor.getValue());
                                yMap.set("position", JSON.stringify(app.editor.getPosition()));
                            });
                        });
                    }
                });
                
                pusher = new Pusher('f547ee541555e37c57bd', {
                    cluster: 'ap3'
                });
                
                $("#buttonSendChatMessage").removeAttr("disabled");

                var channel = pusher.subscribe(r.roomName);
                channel.bind('newChat', function (data) {
                    let msg=JSON.parse(data);
                    if(msg.stuid!=stuid){
                        //then show this message on the guest side
                        let div=document.createElement("div");
                        $(div).addClass("d-flex");
                        $(div).addClass("flex-row");
                        $(div).addClass("p-3");
                        $(div).html(`
                                      <span class="bi bi-robot" style="width: 30px; height: 30px"></span>
                                      <div class="chat ml-2 p-3"></div>
                        `);
                        $(div).find(".chat").text(msg.message);
                        $(".card").append(div);
                        var objDiv = $(".card").get(0);
                        objDiv.scrollTop = objDiv.scrollHeight;
                    }else{
                        //then show this message on my side
                        let div=document.createElement("div");
                        $(div).addClass("d-flex");
                        $(div).addClass("flex-row");
                        $(div).addClass("p-3");
                        $(div).addClass("justify-content-end");
                        $(div).html(`
                                      <div class="bg-white mr-2 p-3"><span class="text-muted"></span></div>
                                      <img src="https://img.icons8.com/color/48/000000/circled-user-male-skin-type-7.png" width="30" height="30">
                        `);
                        $(div).find(".text-muted").text(msg.message);
                        $(".card").append(div);
                        var objDiv = $(".card").get(0);
                        objDiv.scrollTop = objDiv.scrollHeight;
                        $("#buttonSendChatMessage").removeAttr("disabled");
                    }
                });
                
            },
            leaveRoom: function(){
                yDoc.destroy();
                yProvider.destroy();
                leaveRoom();
                pusher.disconnect();
                $("#buttonSendChatMessage").attr("disabled", "disabled");
                if(v.rooms[v.selectedRoom].writable==false){
                    app.editor.updateOptions({ readOnly: false });
                }
                window.location.reload();
            },
            sendChatMessage: function(){
                $("#buttonSendChatMessage").attr("disabled", "disabled");
                $.ajax("/api/pg/chat/room/"+v.rooms[v.selectedRoom].roomName+"/stuid/"+stuid, {
                    type: "POST",
                    contentType:"application/json;charset=utf-8",
                    data: JSON.stringify({message: $("#newChatMessage").val()}),
                    success: function(){
                        $("#newChatMessage").val("");
                    }
                });
            }
        }
    });
    
    $("#newChatMessage").keyup(function(event) {
        if (event.keyCode === 13) {
            $("#buttonSendChatMessage").click();
        }
    });
    
    $.ajax("/api/pg/rooms/stuid/"+stuid, {
        success: function(d){
            v.rooms=d;
        }
    });
}

export{getId, getName,getHtml,init};
