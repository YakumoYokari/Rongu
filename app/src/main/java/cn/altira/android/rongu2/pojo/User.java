package cn.altira.android.rongu2.pojo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
    @Column(index = true)
    private Integer id;

    private String userName;

    private String passWord;

    private Integer accountNumber;

    private int friendCount;

    public User(Integer id,String userName, String passWord, Integer accountNumber, int friendCount) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.accountNumber = accountNumber;
        this.friendCount = friendCount;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }
}
