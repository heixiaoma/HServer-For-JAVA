```java
    /**
     * 文件下载
     *
     * @param response
     * @return
     */
    @GET("/downFile")
    public void downFile(HttpRequest request, HttpResponse response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
    }

    @GET("/downInputStream")
    public void downInputStream(HttpRequest request, HttpResponse response) throws Exception {
        File file = new File("D:\\Java\\HServer\\README.md");
        InputStream fileInputStream = new FileInputStream(file);
        response.setDownloadFile(fileInputStream, "README.md");
    }

   //超大文件下载
    @GET("/downBigFile")
    public void downBigFile(HttpRequest request, HttpResponse response) throws Exception {
        final File file = new File("D:\\Java\\HServer\\README.md");
        response.setDownloadBigFile(file, new ProgressStatus() {
        @Override
        public void operationComplete(String s) {
             log.info("file {} transfer complete.", file.getName());
        }
        
        @Override
        public void downloading(long progress, long total) {
            if (total < 0) {
                log.warn("file {} transfer progress: {}", file.getName(), progress);
            } else {
                log.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
            }
        }
        }, request.getCtx());

    }

    /**
     * 上传文件测试
     *
     * @param request
     * @return
     */
    @POST("/file")
    public Map file(HttpRequest request) {

        Map<String, PartFile> fileItems = request.getMultipartFile();
        fileItems.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
            byte[] data = v.getData();
            System.out.println(data);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        return res;
    }
```