package client.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatSession {

    private String name;
    private String iconUrl= "view/icon.jpeg";
    private ChatType type;
    private String lastChatMsg;
    private LocalDateTime lastChatTime;
    private int newMsgNum=0;

    public void increase(){
        newMsgNum++;
    }

    public void refresh(){
        newMsgNum=0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatSession that = (ChatSession) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
