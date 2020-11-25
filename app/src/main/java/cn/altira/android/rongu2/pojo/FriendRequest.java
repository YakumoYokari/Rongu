package cn.altira.android.rongu2.pojo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class FriendRequest extends LitePalSupport {
    @Column(index = true)
    private Integer id;

    Integer to;
    Integer type;
    Integer from;
    String msg;

    public FriendRequest() {
    }

    public FriendRequest(Integer to, Integer type, Integer from, String msg) {
        this.to = to;
        this.type = type;
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
