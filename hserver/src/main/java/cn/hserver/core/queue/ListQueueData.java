package cn.hserver.core.queue;

import java.util.ArrayList;
import java.util.List;

public class ListQueueData {
    private List<QueueData> queueDataList;

    private String id;

    public ListQueueData() {
    }

    public ListQueueData(String id,List<QueueData> queueDataList) {
        this.queueDataList = queueDataList;
        this.id=id;
    }

    public ListQueueData(String id,QueueData queueData) {
        List<QueueData> queueDataList=new ArrayList<>();
        queueDataList.add(queueData);
        this.queueDataList = queueDataList;
        this.id=id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addData(QueueData queueData){
        queueDataList.add(queueData);
    }

    public List<QueueData> getQueueDataList() {
        return queueDataList;
    }

    public void setQueueDataList(List<QueueData> queueDataList) {
        this.queueDataList = queueDataList;
    }
}
