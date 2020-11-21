package chat;

import java.util.Set;

public class Response {

    private ResponseOperType operType;

    private String msg;

    public Response(ResponseOperType operType, String msg) {
        this.operType = operType;
        this.msg = msg;
    }

    public Response() {
    }

    public Response(ResponseOperType operType) {
        this.operType = operType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Response{" +
                "operType=" + operType +
                ", msg='" + msg + '\'' +
                 +
                '}';
    }

    public ResponseOperType getOperType() {
        return operType;
    }

    public void setOperType(ResponseOperType operType) {
        this.operType = operType;
    }

}
