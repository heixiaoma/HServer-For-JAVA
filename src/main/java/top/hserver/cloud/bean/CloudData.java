package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CloudData implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String name;

    private String ip;

    private List<Class> classes;
}
