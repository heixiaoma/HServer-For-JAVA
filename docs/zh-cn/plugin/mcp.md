# MCP-Server 模块

```xml
        <dependency>
            <groupId>cn.hserver</groupId>
            <artifactId>hserver-plugin-mcp</artifactId>
        </dependency>
```


```java

@McpServerEndpoint(sseEndpoint = "/sse",version = "1.0.0" ,name = "测试MCP")
public class McpController {

    /**
     * 只能返回McpSchema.Content或者Collection<McpSchema.Content>子类实现
     * ImageContent
     * TextContent
     * @param name
     * @return
     */
    @ToolMapping(name = "测试",description = "一个测试函数")
    public McpSchema.Content getTest(@Param(description = "输入名字",defaultValue = "测试",required = false) String name) {
        System.out.println(name);
        return new McpSchema.TextContent("aa");
    }

    /**
     * 只能返回McpSchema.ResourceContents或者Collection<McpSchema.ResourceContents>子类实现
     * BlobResourceContents
     * TextResourceContents
     * @return
     */
    @ResourcesMapping(uri = "db://aaaa", name = "测试",description = "一个测试函数")
    public McpSchema.ResourceContents getTest2() {
        return new McpSchema.TextResourceContents("db://aa","text/plan","测试数据");
    }

    /**
     * 只能返回McpSchema.ResourceContents或者Collection<McpSchema.ResourceContents>子类实现
     * BlobResourceContents
     * TextResourceContents
     * @param userId
     * @return
     */
    @ResourcesMapping(uri = "db://aa/{userId}/aa", name = "测试",description = "一个测试函数")
    public List<McpSchema.ResourceContents> getTest3(String userId) {
        List<McpSchema.ResourceContents> list = new ArrayList<>();
        list.add(new McpSchema.TextResourceContents("db://aa",null,"测试数据"));
        return list;
    }

    /**
     * 只能返回McpSchema.PromptMessage或者Collection<McpSchema.PromptMessage>
     * @param userId
     * @return
     */
    @PromptMapping(name = "测试",description = "一个测试函数")
    public McpSchema.PromptMessage getTest4(@Param(description = "用户ID",required = true) String userId) {
        return new McpSchema.PromptMessage(McpSchema.Role.USER,new McpSchema.TextContent("用户ID:"+userId));
    }
}

```