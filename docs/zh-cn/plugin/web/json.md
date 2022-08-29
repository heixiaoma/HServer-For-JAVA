## **自定义JSON序列化**

```java

    //默认使用Jackson,可以自己实现这个接口进行替换，这段代码放在main 函数里
     HServerApplication.setJson(new JsonAdapter() {
            /**
             *  string 转对象
             * @param data
             * @param type
             * @return
             */
            @Override
            public Object convertObject(String data, Class type) {
                return null;
            }


            /**
             * 参数类型转换
             *
             * @param data
             * @param type
             * @return
             */
            @Override
            public Object convertObject(String data, Parameter type) {
                return null;
            }
            /**
             * map转对象
             *
             * @param data
             * @param type
             * @return
             */
            @Override
            public Object convertMapToObject(Map data, Class type) {
                return null;
            }
            /**
             * 对象转String
             *
             * @param data
             * @return
             */
            @Override
            public String convertString(Object data) {
                return null;
            }
        });
        

```

