package cn.hserver.mvc.constants;

import cn.hserver.mvc.json.JackSonJsonAdapter;
import cn.hserver.mvc.json.JsonAdapter;
import cn.hserver.mvc.session.SessionManager;
import cn.hserver.mvc.template.FreemarkerTemplate;
import cn.hserver.mvc.template.Template;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class WebConstConfig {

    public static Integer PORT = 8888;

    /**
     * session-key
     */
    public final static String SESSION_KEY = "session_id";

    /**
     * session过期 2小时
     */
    public static Integer SESSION_TIME_OUT = 7200;

    /**
     * session对象
     */
    public static SessionManager SESSION_MANAGER = null;

    /**
     * 对象处理
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    /**
     * 另外JSON一个名字，兼容以前的
     */
    public static final ObjectMapper JSON = OBJECT_MAPPER;

    /**
     * 通用JSON适配器
     */
    public static JsonAdapter JSONADAPTER = new JackSonJsonAdapter();

    /**
     * 模版对象-可以自定义
     */
    public static Template template=new FreemarkerTemplate();


    public final static String REQUEST_ID = "HRequest-Id";

    /**
     * 内部自用名字
     */
    public final static String SERVER_NAME = "HServer-Web";

    public static String STATIC_PATH;
}
