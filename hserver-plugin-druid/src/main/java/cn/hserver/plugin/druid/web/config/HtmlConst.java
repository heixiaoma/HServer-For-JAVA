package cn.hserver.plugin.druid.web.config;

public class HtmlConst {

    public static final String HEADER="<div style=\"float:right;margin-right:20px;\">\n" +
            "    <a class=\"langSelector\" langNow=\"0\">English</a> | <a class=\"langSelector\" langNow=\"1\">中文</a>\n" +
            "</div>\n" +
            "<div class=\"navbar navbar-fixed-top\">\n" +
            "    <div class=\"navbar-inner\">\n" +
            "        <div class=\"container\">\n" +
            "            <a href=\"https://github.com/alibaba/druid/wiki\" target=\"_blank\" class=\"brand lang\" langKey=\"\">Druid\n" +
            "                Monitor</a>\n" +
            "            <div class=\"nav-collapse\">\n" +
            "                <ul class=\"nav\">\n" +
            "                    <li><a href=\"index.html\" class=\"lang\" langKey=\"Index\">Index</a></li>\n" +
            "                    <li><a href=\"datasource.html\" class=\"lang\" langKey=\"DataSource\">DataSource</a></li>\n" +
            "                    <li><a href=\"sql.html\" class=\"lang\" langKey=\"SQL\">SQL</a></li>\n" +
            "                    <li><a href=\"wall.html\" class=\"lang\" langKey=\"Wall\">Wall</a></li>\n" +
            "                    <li><a href=\"api.html\" class=\"lang\" langKey=\"JSON API\">JSON API</a></li>\n" +
            "                </ul>\n" +
            "                <a langKey=\"ResetAll\" class=\"btn btn-primary lang\"\n" +
            "                   href=\"javascript:druid.common.ajaxRequestForReset();\">Reset All</a>\n" +
            "                <a langKey=\"LogAndReset\" class=\"btn btn-primary lang\"\n" +
            "                   href=\"javascript:druid.common.ajaxRequestForLogAndReset();\">Log And Reset</a>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n";
}
