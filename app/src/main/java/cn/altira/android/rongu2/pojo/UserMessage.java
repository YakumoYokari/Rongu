package cn.altira.android.rongu2.pojo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class UserMessage extends LitePalSupport {
    @Column(index = true)
    private Integer id;

    Integer to;
    Integer type;
    Integer from;
    String msg;

    public UserMessage() {
    }

    public UserMessage(Integer to, Integer type, Integer from, String msg) {
        this.to = to;
        this.type = type;//1.makefriend 2.chat 3.deletefriend 4.agree 5.refuse 6.send
        this.from = from;
        this.msg = msg;
    }
    public Integer getId() {
        return id;
    }

    public Integer getTo() {
        return to;
    }

    public Integer getType() {
        return type;
    }

    public Integer getFrom() {
        return from;
    }

    public String getMsg() {
        return msg;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
