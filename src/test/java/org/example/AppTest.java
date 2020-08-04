package org.example;

import static org.junit.Assert.assertTrue;

import client.dao.UserMapper;
import client.model.UserExample;
import client.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.net.URL;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
       /* System.out.println(getClass().getResource("/emoji.jpeg").getPath());
        URL url=getClass().getClassLoader().getResource("emoji.jpeg");
        String path = url.getPath();*/

      /*  LocalDateTime now = LocalDateTime.now();
        System.out.println("计算两个时间的差：");
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(now,end);
        long days = duration.toDays(); //相差的天数
        long hours = duration.toHours();//相差的小时数
        long minutes = duration.toMinutes();//相差的分钟数
        long millis = duration.toMillis();//相差毫秒数
        long nanos = duration.toNanos();//相差的纳秒数
        System.out.println(now);
        System.out.println(end);

        System.out.println("发送短信耗时【 "+days+"天："+hours+" 小时："+minutes+" 分钟："+millis+" 毫秒："+nanos+" 纳秒】");*/


        try(SqlSession session= MybatisUtils.getCommonMapper()){

            UserMapper commonMapper =session.getMapper(UserMapper.class);
            UserExample example =new UserExample();
            example.createCriteria().andUsernameEqualTo("xxx");
            long count=commonMapper.countByExample(example);
            System.out.println(count);
        }
    }


}
