package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RegRpcData implements Serializable {

    private static final long SerialVersionUID = 1L;

    /**
     * 提供者的名字
     */
    private String name;

    /**
     * 提供者的rpc 类名，里面有方法可以调用
     */
    private List<String> classes;
}
