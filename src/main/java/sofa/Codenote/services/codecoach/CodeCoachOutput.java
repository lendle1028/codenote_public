/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sofa.Codenote.services.codecoach;

/**
 *
 * @author USER
 */
public class CodeCoachOutput {

    private String message = null;
    private boolean error = false;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        //clean html fragment output
        if (this.message.startsWith("```html") || this.message.startsWith("```json")) {
            this.message=this.message.substring(7);
        }
        if (this.message.endsWith("```")) {
            this.message=this.message.substring(0, this.message.length() - 3);
        }
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
