package cn.hserver.handler;

import cn.hserver.msg.ReqMsg;
import cn.hserver.plugin.web.context.sse.SSeEvent;
import cn.hserver.plugin.web.context.sse.SSeStream;

import java.util.concurrent.atomic.AtomicLong;

public abstract class MsgHandler {

    private final SSeStream sSeStream;

    public MsgHandler(SSeStream sSeStream) {
        this.sSeStream = sSeStream;
    }

    private final AtomicLong lastId = new AtomicLong();

    public Long getId() {
        return lastId.getAndIncrement();
    }
    public void handle(ReqMsg reqMsg) {
        if ("tools/list".equals(reqMsg.getMethod())){
            sendMsg(toolsList(reqMsg));
        }
        if ("initialize".equals(reqMsg.getMethod())){
            sendMsg(initialize(reqMsg));
        }
        if ("tools/call".equals(reqMsg.getMethod())){
            sendMsg(toolsCall(reqMsg));
        }
    }

    private void sendMsg(String msg) {
        sSeStream.sendSseEvent(new SSeEvent.Builder().event("message").data(msg).build());
    }

    protected abstract String toolsCall(ReqMsg reqMsg);
    protected abstract String toolsList(ReqMsg reqMsg);

    protected abstract String initialize(ReqMsg reqMsg);

}
