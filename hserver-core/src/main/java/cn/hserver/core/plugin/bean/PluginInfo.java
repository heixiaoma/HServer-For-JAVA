package cn.hserver.core.plugin.bean;

public class PluginInfo {
    private String name;
    private String version;
    private String author;
    private String description;
    public PluginInfo() {}
    public PluginInfo(String name, String version, String author, String description) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description;
    }

    // 私有构造方法，供Builder使用
    private PluginInfo(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.author = builder.author;
        this.description = builder.description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String info() {
        if (name == null) {
            return null;
        }
        // 定义分隔线
        String separator = "==========================================";
        // 拼接日志内容
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n")
                .append(separator).append("\n")
                .append("【")
                .append(name)
                .append("】\n");
        // 只添加非空字段
        logBuilder.append(String.format("名称\t: %s\n", name));
        if (version != null && !version.isEmpty()) {
            logBuilder.append(String.format("版本\t: %s\n", version));
        }
        if (author != null && !author.isEmpty()) {
            logBuilder.append(String.format("作者\t: %s\n", author));
        }
        if (description != null && !description.isEmpty()) {
            logBuilder.append(String.format("描述\t: %s\n", description));
        }
        logBuilder.append(separator).append("\n");
        return logBuilder.toString();
    }


    // Builder类
    public static class Builder {
        private String name;
        private String version;
        private String author;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public PluginInfo build() {
            return new PluginInfo(this);
        }
    }
}
