const EDITING_NONE = -1, EDITING_CODE = 0, EDITING_NOTE = 1;
class EditorApp {
    constructor() {
        this.editor = null;
        this.editor2 = null;
        this.codelan;
        this.editorPrevValue = null;
        this.editor2PrevValue = null;
        this.currentEditingSection = EDITING_NONE;
        this.diffArray = [];
        this.flagAdjustingNoteLineNumber = false;
        this.url = "/previewCode?projectId=" + projectid;
        this.modified = false;
        this.codeInfoBean=null;
        this.projectid=projectid;
        this.plugins=[];
        
        this.trackEditorContentChange=true;
        
        this.editorUpdateListeners=[];
        
        this.init=this.init.bind(this);
        this.loadCode=this.loadCode.bind(this);
        this.loadCodeAsync=this.loadCodeAsync.bind(this);
        this.saveCode=this.saveCode.bind(this);
        this.loadEditor=this.loadEditor.bind(this);
        this.currentEditingSectionChanged=this.currentEditingSectionChanged.bind(this);
        this.updateDiffArray=this.updateDiffArray.bind(this);
        this.calculateDiff=this.calculateDiff.bind(this);
        this.goPreview=this.goPreview.bind(this);
        this.download=this.download.bind(this);
        
        $.busyLoadSetup({animation: "slide", background: "rgba(127,63,191,0.54)"});
    }
    
    init(){
        this.loadEditor();
    }

    loadCode(codeid) {
        $.busyLoadFull("show", {text: "LOADING ...", textColor: "white", color: "#DCB5FF", textPosition: "bottom"});
        if (this.modified) {
            this.saveCode(()=>{
                this.loadCodeAsync(codeid);
            });
        } else {
            this.loadCodeAsync(codeid);
        }
    }

    loadCodeAsync(codeid) {
        $.ajax("codeInfoBean/" + codeid, {
            success: (d)=> {
                this.codeInfoBean = d;
                this.diffArray = [];
                $("#codeName").val(this.codeInfoBean.code_nmae);
                this.codelan = this.codeInfoBean.code_nmae;
                let codelan_num = this.codelan.indexOf(".");
                this.codelan = this.codelan.substring(codelan_num + 1);
                if (this.codelan == "js") {
                    this.codelan = "javascript";
                } else if (this.codelan == "html") {
                    this.codelan = "html";
                }
                window.MonacoEditor.setModelLanguage(this.editor.getModel(), this.codelan);
                this.editorPrevValue = this.codeInfoBean.code_content;
                this.editor2PrevValue = this.codeInfoBean.noteInfoBean.note_content;
                this.currentEditingSection = EDITING_NONE;

                //switch editor content change listener to no-op temporarily
                this.trackEditorContentChange=false;
                //////////////////////////////
                this.editor.setValue(this.codeInfoBean.code_content);
                this.editor2.setValue(this.codeInfoBean.noteInfoBean.note_content);
                //turn on content change tracking
                this.trackEditorContentChange=true;
                this.modified = false;
                $.busyLoadFull("hide");
                window.fireOnOpened(d.code_nmae);
            },
            error: function () {
                this.modified = false;
                $.busyLoadFull("hide");
            }
        });
    }

    saveCode(callback) {
        if(this.editorPrevValue==null){
            return;
        }
        let diffs = this.calculateDiff(null);
        this.updateDiffArray(diffs);
//                console.log(diffArray);
        this.codeInfoBean.code_content = this.editor.getValue();
        this.codeInfoBean.noteInfoBean.note_content = this.editor2.getValue();
        let monacoSaveTempBean = {
            diffArray: this.diffArray,
            cib: this.codeInfoBean
        };
        $.busyLoadFull("show", {text: "SAVING ...", textColor: "white", color: "#DCB5FF", textPosition: "bottom"});
        let self=this;
        $.ajax("/savecode?projectId=" + this.projectid, {
            type: "POST",
            data: JSON.stringify(monacoSaveTempBean),
            contentType: "application/json",
            success: function () {
                this.modified = false;
                $.busyLoadFull("hide");
                if (callback) {
                    callback();
                }
                setTimeout(function(){
                    window.fireOnSave(self.codeInfoBean.code_nmae);
                }, 1000);
//                        alert("修改內容已儲存成功，請至專案頁面試試看效果。");
//                        location.href = 'monaco/project_id/' + projectid;
            },
            error: function () {
                $.busyLoadFull("hide");
            }
        });
    }

    loadEditor() {
        this.codelan = "text";
        require.config({paths: {vs: '../../monaco/vs'}});
        let self=this;
        //API:  https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.ICodeEditor.html#onDidChangeModel
        require(['vs/editor/editor.main'], function () {
            self.editor = monaco.editor.create(document.getElementById('container'), {
                language: this.codelan,
                theme: 'vs',
                automaticLayout: true
            });

            self.editor2 = monaco.editor.create(document.getElementById('container2'), {
                language: 'text',
                theme: 'vs',
                automaticLayout: true
            });


            Split(["#plugins_panel", "#code_panel"], {
                sizes: [20, 80],
            });
            Split(["#split-1", "#split-2"], {
                sizes: [70, 30],
            });
            window.MonacoEditor=monaco.editor;
            self.editor.getModel().onDidChangeContent((event)=>{
                self.onEditorDidChangeContent(event);
            });
            self.editor2.getModel().onDidChangeContent((event)=>{
                self.onEditor2DidChangeContent(event);
            });
        });
    }

    currentEditingSectionChanged(oldSection, newSection) {
        if(this.editorPrevValue==null){
            return;
        }
        let diffs = this.calculateDiff(oldSection);
        if (diffs.length > 0) {
            this.updateDiffArray(diffs);
        }
    }

    updateDiffArray(diffs) {
        if (diffs && diffs.length > 0) {
            for (let diff of diffs) {
                this.diffArray.push(diff);
            }
        }
    }

    calculateDiff(filterEditingSection) {
        let ret = [];
        if ((filterEditingSection == null || filterEditingSection == EDITING_CODE) && this.editor.getValue() != this.editorPrevValue) {
            //then there are modifications in the code panel
            let editorNewValue = this.editor.getValue();
            const diff = JsDiff.createTwoFilesPatch("oldCode", "newCode", this.editorPrevValue, editorNewValue);
            this.editorPrevValue = editorNewValue;
            ret.push({
                timestamp: new Date().getTime(),
                diffSource: "code",
                diff: diff
            });
        }
        if ((filterEditingSection == null || filterEditingSection == EDITING_NOTE) && this.editor2.getValue() != this.editor2PrevValue) {
            //then there are modifications in the note panel
            let editor2NewValue = this.editor2.getValue();
            const diff = JsDiff.createTwoFilesPatch("oldNote", "newNote", this.editor2PrevValue, editor2NewValue);
            this.editor2PrevValue = editor2NewValue;
            ret.push({
                timestamp: new Date().getTime(),
                diffSource: "note",
                diff: diff
            });
        }
        return ret;
    }

    goPreview() {
        if (this.modified) {
            this.saveCode(()=> {
                window.open(this.url, "_blank");
            });
        } else {
            window.open(this.url, "_blank");
        }

    }
    
    download() {
        $.ajax("/exportProjectBeans/project_id/" + this.projectid, {
            type: "GET",
            success: function (download_url) {
                window.open(download_url);
            },
            error: function () {}
        });
    }
    
    loadPlugin(plugin){
        this.plugins.push(plugin);
        let tabLi=document.createElement("li");
        $(tabLi).html(`
            <button class="nav-link" id="${plugin.getId()}Tab" data-bs-toggle="tab" data-bs-target="#${plugin.getId()}" type="button" role="tab" aria-controls="${plugin.getId()}" aria-selected="false">${plugin.getName()}</button>
        `);
        $(tabLi).attr("role", "tab");
        $(tabLi).addClass("nav-item");
        if(this.plugins.length==1){
            $(tabLi).addClass("active");
        }
        $("#tabs").append($(tabLi));
        
        let tabDiv=document.createElement("div");
        $(tabDiv).html(plugin.getHtml());
        $(tabDiv).addClass("tab-pane");
        $(tabDiv).css("overflow", "auto");
        $(tabDiv).css("height", "500px");
        if(this.plugins.length==1){
            $(tabDiv).addClass("active");
        }
        $(tabDiv).attr("id", plugin.getId());
        $(tabDiv).attr("role", "tabpanel");
        $(tabDiv).attr("aria-labelledby", plugin.getName()+"Tab");
        $("#pluginsPaneContainer").append($(tabDiv));
        plugin.init();
    }
    
    addEditorUpdateListener(l){
        this.editorUpdateListeners.push(l);
    }
    
    onEditorDidChangeContent(event) {
        if(!this.trackEditorContentChange){
            return;
        }
        this.modified = true;
        if (this.currentEditingSection != EDITING_CODE) {
            let prevEditingSection = this.currentEditingSection;
            this.currentEditingSection = EDITING_CODE;
            this.currentEditingSectionChanged(prevEditingSection, this.currentEditingSection);
        }

        while (this.editor2.getModel().getLineCount() < this.editor.getPosition().lineNumber) {
            this.editor2.setValue(this.editor2.getValue() + "\r\n");
        }

        this.flagAdjustingNoteLineNumber = true;
        this.editor2.getModel().applyEdits([{
                range: {startLineNumber: this.editor.getPosition().lineNumber, endLineNumber: this.editor.getPosition().lineNumber},
                text: ""
            }]);
        this.flagAdjustingNoteLineNumber = false;
        for (let l of this.editorUpdateListeners) {
            l(event);
        }
    }
    
    onEditor2DidChangeContent(event){
        if(!this.trackEditorContentChange){
            return;
        }
        this.modified = true;
        if (this.flagAdjustingNoteLineNumber) {
            return;
        }
        if (this.currentEditingSection != EDITING_NOTE) {
            let prevEditingSection = this.currentEditingSection;
            this.currentEditingSection = EDITING_NOTE;
            this.currentEditingSectionChanged(prevEditingSection, this.currentEditingSection);
        }
    }
}