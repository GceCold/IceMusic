package ltd.icecold.icemusic.bean;

/**
 * @author ice-cold
 */
public class ReturnMessage {

    private String type;
    private String code;
    private Object message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
