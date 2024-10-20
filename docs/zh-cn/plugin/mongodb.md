## Redis操作

### 导入依赖
```xml
  <!--  设置这个仓库哦 不然可能morphia凉了(下载不到)  -->
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>dev.morphia.morphia</groupId>
    <artifactId>morphia-core</artifactId>
    <version>2.4.14</version>
</dependency>
</dependencies>
```

### 添加配置类

```java

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Configuration;
import top.hserver.core.ioc.annotation.Value;

@Configuration
public class MongoConfig {

    @Value("mongo.url")
    private String connect;

    @Bean
    public Datastore datastore() {
        ConnectionString connectionString = new ConnectionString(connect);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return Morphia.createDatastore(MongoClients.create(clientSettings),"test");
    }

}

```


### 然后使用
```java

import com.test.bean.Employee;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;

import java.util.List;

@Bean
public class EmployeeDao {

    @Autowired
    private Datastore datastore;

    public void save() {
        final Employee elmer = new Employee("Elmer Fudd", 50000.0);
        datastore.save(elmer);
        final Employee daffy = new Employee("Daffy Duck", 40000.0);
        datastore.save(daffy);
        final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
        datastore.save(pepe);
        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);
        datastore.save(elmer);
    }

    public List<Employee> queryList() {
        Query<Employee> query = datastore.find(Employee.class);
        List<Employee> employees = query.iterator().toList();
        return employees;
    }


    public List<Employee> queryList1() {
        return datastore.find(Employee.class)
                .filter(Filters.lte("salary", 30000))
                .iterator()
                .toList();
    }



}

```

### 官方API使用文档

```text
https://github.com/MorphiaOrg/morphia
```

## 更多复杂查询 相关 操作看文档 文档地址

```text
https://morphia.dev/
```