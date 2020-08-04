package client;

import client.util.DateUtil;
import client.util.msgUtils;
import client.util.propertiesUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HertBeator implements Runnable{

    private String username;
    private int noRec=1;
    private  int MAX_UN_REC_PING_TIMES = 3;  // 超过心跳阀值的次数
    private int READ_WAIT_SECONDS=5;         // 心跳数据时间间隔（单位秒）
    private int Threshold_SECONDS=10;        //心跳阀值，超过10秒
    private volatile  boolean isShutdown=false;
    private volatile LocalDateTime lastActionTime=LocalDateTime.now(); //上一次接收或者发送聊天消息或者接收到心跳包时更新
    private Reactor reactor;

    public HertBeator(Reactor reactor,String username){
        loadProperties();
        this.reactor=reactor;
        this.username=username;
    }

    public void loadProperties(){
        this.MAX_UN_REC_PING_TIMES= propertiesUtils.getInt("config/hertbeator","MAX_UN_REC_PING_TIMES");
        this.READ_WAIT_SECONDS= propertiesUtils.getInt("config/hertbeator","READ_WAIT_SECONDS");
        this.Threshold_SECONDS = propertiesUtils.getInt("config/hertbeator","Threshold_SECONDS");
    }



    @Override
    public void run() {
        try{
            while(!isShutdown&&!Thread.interrupted()) {
              //获取当前时间与上次操作时间的差值
              long seconds=  DateUtil.compareTimeWithNow(lastActionTime);
              //等待时间小于READ_WAIT_SECONDS，代表期间有过操作，我们让其继续等待，不发送心跳包
              if(seconds<READ_WAIT_SECONDS){
                  TimeUnit.SECONDS.sleep(READ_WAIT_SECONDS-seconds);
              }else {
                  //判断等待时间是否超过阀值
                  if(seconds>Threshold_SECONDS*noRec) {
                      //超时
                      noRec++;
                      //超过给定次数未收到消息，认为失联，开始重连，直到连接上
                      if (noRec > MAX_UN_REC_PING_TIMES) {
                            reactor.hertBeatorConnect();
                      }
                  }else{
                      noRec=1;
                  }
                  //发送心跳消息
                  boolean result =reactor.sendMsg(msgUtils.getHertBeatMsg(username));
                  if(!result){
                      reactor.hertBeatorConnect();
                  }
                  TimeUnit.SECONDS.sleep(READ_WAIT_SECONDS);
              }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateActionTime(){
        lastActionTime=LocalDateTime.now();
    }

    public void stop(){
        isShutdown=true;
    }
}
