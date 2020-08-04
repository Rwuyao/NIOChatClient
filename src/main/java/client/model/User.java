package client.model;

import client.service.groupService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    private Integer id;
    private String username;//账号
    private String password;
    private String nickname;
    private int sex;
    private Date createtime;
    private String iconurl= "view/icon.jpeg";
    private volatile  List<Group> groupList;

    public User(String username){
        this.username= username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<Group> getGroupList() {
        //延迟加载
        if (groupList == null) {
            synchronized(User.class){
                if(groupList == null){
                    groupList = groupService.getGroupByUsername(username);
                }
            }
        }
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
