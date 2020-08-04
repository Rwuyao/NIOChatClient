package client.service;


import client.dao.GroupMapper;
import client.model.Group;
import client.model.GroupExample;
import client.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class groupService {

    public static boolean isExitsGroupName(String groupname) {
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            GroupMapper groupMapper = session.getMapper(GroupMapper.class);
            GroupExample example = new GroupExample();
            example.createCriteria().andGroupnameEqualTo(groupname);
            long count = groupMapper.countByExample(example);
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static List<Group> getGroupByGroupname(String groupname){
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            GroupMapper groupMapper = session.getMapper(GroupMapper.class);
            GroupExample example = new GroupExample();
            example.createCriteria().andGroupnameEqualTo(groupname);
           List<Group> groupList= groupMapper.selectByExample(example);
            if(groupList!=null&& groupList.size()>0 ){
               return groupList;
            }else{
                return null;
            }
        }
    }

    public static List<Group> getGroupByUsername(String Username){
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            GroupMapper groupMapper = session.getMapper(GroupMapper.class);
            List<Group> groupList =groupMapper.selectGroupByUserName(Username);
            if(groupList!=null&& groupList.size()>0 ){
                return groupList;
            }else{
                return null;
            }
        }
    }

    public static void saveGroup(Group group) {
        try(SqlSession session= MybatisUtils.getCommonMapper()) {
            GroupMapper groupMapper = session.getMapper(GroupMapper.class);
            groupMapper.insert(group);
            session.commit();
        }
    }
}
