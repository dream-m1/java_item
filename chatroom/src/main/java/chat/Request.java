package chat;

/**
 * 一定要有setget方法
 */
public class Request {

    //消息类型
    private RequestOperType operType;

    //发送者用户名
    private String from;

    //接受者用户名
    private String to;

    //聊天内容
    private String msg;

    //表情
    private String expression;

    //true=私聊
    private boolean pm;

    public RequestOperType getOperType() {
        return operType;
    }

    public void setOperType(RequestOperType operType) {
        this.operType = operType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isPm() {
        return pm;
    }

    public void setPm(boolean pm) {
        this.pm = pm;
    }

    @Override
    public String toString() {
        return "Request{" +
                "operType=" + operType +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", msg='" + msg + '\'' +
                ", expression='" + expression + '\'' +
                ", pm=" + pm +
                '}';
    }
}


