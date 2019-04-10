package in.dsij.pas.net.respose;

/**
 * Created by Vikas on 9/12/2017.
 */

public class ResMessage {

    /**
     * Code : 411
     * Message : Incorrect Password
     */

    private int code;
    private String message;

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String Message) {
        this.message = Message;
    }
}
