
## 全局request,response获取
```text
       //全局获取 可以夸线程
       Webkit webKit = HServerContextHolder.getWebKit();
            if (webKit != null) {
                return webKit.httpRequest.getRequestId();
            }
```
