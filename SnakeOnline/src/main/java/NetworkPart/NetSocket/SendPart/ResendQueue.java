package NetworkPart.NetSocket.SendPart;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResendQueue {
    private ConcurrentHashMap<SnakesProto.GameMessage, Date> resendQueue;

    public ResendQueue() {
        resendQueue = new ConcurrentHashMap<>();
    }

    public void addNewResendMessage(SnakesProto.GameMessage message) {

            resendQueue.put(message, new Date());

    }

    public long getMinToResend() {
            if (resendQueue.isEmpty())
                return 0;
            Date currentDate = new Date(0);
            for (Map.Entry<SnakesProto.GameMessage, Date> iterator : resendQueue.entrySet()) {
                if (iterator.getValue().getTime() > currentDate.getTime()) {
                    currentDate = iterator.getValue();
                }
            }
            return (new Date()).getTime() - currentDate.getTime();

    }

    public ConcurrentHashMap<SnakesProto.GameMessage, Date> getResendQueue() {
            return resendQueue;
    }


    public void deleteMessage(SnakesProto.GameMessage message) {
        synchronized (ResendQueue.class) {
            int msg_seq = (int) message.getMsgSeq();
            Iterator<Map.Entry<SnakesProto.GameMessage, Date>> iterator = resendQueue.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<SnakesProto.GameMessage, Date> next = iterator.next();
                if (next.getKey().getMsgSeq() == msg_seq)
                    iterator.remove();
            }
        }
    }
    public void deleteAnnouncementMsg(){
        synchronized (ResendQueue.class) {
            if (!resendQueue.isEmpty()) {
                Iterator<Map.Entry<SnakesProto.GameMessage, Date>> iterator = resendQueue.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<SnakesProto.GameMessage, Date> next = iterator.next();
                    if (next.getKey().getTypeCase().equals(SnakesProto.GameMessage.TypeCase.ANNOUNCEMENT))
                        iterator.remove();
                }
            }
        }
    }
}