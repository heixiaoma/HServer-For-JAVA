
## 全局request,response获取
```text
       只能在和控制器同级别的线程才能获取。
       Webkit webKit = HServerContextHolder.getWebKit();
            if (webKit != null) {
                return webKit.httpRequest.getRequestId();
            }
```
