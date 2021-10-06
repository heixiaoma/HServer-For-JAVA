
## **参数校验器**

```
控制器参数是一个Bean时，字段可以使用校验器注解
```

| 注解         | 描述                                                   |
| ------------ | ------------------------------------------------------ |
| @AssertFalse | 字段为必须为false                                      |
| @AssertTrue  | 字段为必须为true                                       |
| @Length      | 字段CharSequence 类型的长度必须是 length 长            |
| @Max         | 字段值必须大于这个值，number                           |
| @Min         | 字段值必须小于这个值，number                           |
| @NotBlank    | 字段不能为null同时不是 ""                              |
| @NotEmpty    | CharSequence 集合 map 数组 不是null 长度或者size 大于0 |
| @NotNull     | 字段不能为Null                                         |
| @Null        | 字段必须为Null                                         |
| @Pattern     | 字段CharSequence 必须满足这个正则                        |
| @Size        | 字段 CharSequence 集合 map 数组必须在这范围内          |
