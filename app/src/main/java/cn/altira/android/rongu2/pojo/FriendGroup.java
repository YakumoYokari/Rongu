package cn.altira.android.rongu2.pojo;

import java.util.List;

public class FriendGroup {
    private String groupName;
    private List<User> friends;

    public FriendGroup(String groupName, List<User> friends) {
        this.groupName = groupName;
        this.friends = friends;
    }

    public FriendGroup() {
    }

    public String getGroupName() {
        return groupName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
