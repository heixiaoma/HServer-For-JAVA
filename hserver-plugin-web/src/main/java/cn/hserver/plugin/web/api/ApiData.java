package cn.hserver.plugin.web.api;


import cn.hserver.plugin.web.annotation.apidoc.DataType;

import java.util.List;

/**
 * @author hxm
 */
public class ApiData {

    /**
     * 接口名字
     */
    private String name;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 接口描述
     */
    private String note;
    /**
     * 请求类型
     */
    private List<String> requestMethod;
    /**
     * 请求的字段
     */
    private List<ReqData> reqDataList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(List<String> requestMethod) {
        this.requestMethod = requestMethod;
    }

    public List<ReqData> getReqDataList() {
        return reqDataList;
    }

    public void setReqDataList(List<ReqData> reqDataList) {
        this.reqDataList = reqDataList;
    }

    public static class ReqData {

        /**
         * 字段名字
         */
        private String name;
        /**
         * 字段描述
         */
        private String value;
        /**
         * 是否必填
         */
        private boolean required;
        /**
         * 数据类型
         */
        private DataType dataType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public DataType getDataType() {
            return dataType;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }
    }
}
