package top.test.event;

import top.hserver.core.eventx.EventPriority;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;

import java.util.Map;

@EventHandler("/test")
public class EventTest{

    @Event("aa")
    public void aa(Map params) {
        try{
//            Thread.sleep(1);
        }catch (Exception e){

        }
    }

    @Event(value = "bb", priority = EventPriority.MIDDLE)
    public void bb(Map params) {
        System.out.println(params);
    }

    @Event(value = "cc", priority = EventPriority.LOW)
    public void cc(Map params) {
        System.out.println(params);
    }

}
