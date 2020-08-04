package client.service;


import client.dao.UserGroupMapper;
import client.model.UserGroup;
import client.model.UserGroupExample;
import client.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class userGroupService {

    public static List<String> getUserByGroupname(String groupname){
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            UserGroupMapper userGroupMapper = session.getMapper(UserGroupMapper.class);
            List<String> groupList= userGroupMapper.selectUserNameByGroupName(groupname);
            if(groupList!=null&& groupList.size()>0 ){
                return groupList;
            }else{
                return null;
            }
        }
    }

    public static boolean isExitsLink(String username,String groupname) {
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            UserGroupMapper userGroupMapper = session.getMapper(UserGroupMapper.class);
            UserGroupExample example = new UserGroupExample();
            example.createCriteria().andUsernameEqualTo(username).andGroupnameEqualTo(groupname);
            long count = userGroupMapper.countByExample(example);
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void saveLink(UserGroup userGroup) {
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            UserGroupMapper userGroupMapper = session.getMapper(UserGroupMapper.class);
            userGroupMapper.insert(userGroup);
            session.commit();
        }
    }
}
