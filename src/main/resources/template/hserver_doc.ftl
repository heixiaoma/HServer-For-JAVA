<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>HServer-API</title>
  <link rel="stylesheet" href="/css/bootstrap.css">
  <link rel="stylesheet" href="/css/font-awesome.min.css">
  <style type="text/css">
    .header {
      text-align: center;
      line-height: 30px;
    }

    .demo {
      padding: 2em 0;
      background: #fff;
    }

    a:hover,
    a:focus {
      text-decoration: none;
      outline: none;
    }

    #accordion .panel {
      border: none;
      box-shadow: none;
      border-radius: 0;
      margin: 0 0 15px 10px;
    }

    #accordion .panel-heading {
      padding: 0;
      border-radius: 30px;
    }

    #accordion .panel-title a {
      display: block;
      padding: 12px 20px 12px 50px;
      background: #ebb710;
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      border: 1px solid transparent;
      border-radius: 30px;
      position: relative;
      transition: all 0.3s ease 0s;
    }

    #accordion .panel-title a.collapsed {
      background: #fff;
      color: #0d345d;
      border: 1px solid #ddd;
    }

    #accordion .panel-title a:after,
    #accordion .panel-title a.collapsed:after {
      content: "";
      font-family: fontawesome;
      width: 55px;
      height: 55px;
      line-height: 55px;
      border-radius: 50%;
      background: #ebb710;
      font-size: 25px;
      color: #fff;
      text-align: center;
      border: 1px solid transparent;
      box-shadow: 0 3px 10px rgba(0, 0, 0, 0.58);
      position: absolute;
      top: -5px;
      left: -20px;
      transition: all 0.3s ease 0s;
    }

    #accordion .panel-title a.collapsed:after {
      content: "";
      background: #fff;
      color: #0d345d;
      border: 1px solid #ddd;
      box-shadow: none;
    }

    #accordion .panel-body {
      padding: 0px 25px 10px 9px;
      background: transparent;
      font-size: 14px;
      color: #8c8c8c;
      line-height: 25px;
      border-top: none;
      position: relative;
    }

    #accordion .panel-body:nth-child(1) {
      padding: 20px 25px 10px 9px;
      background: transparent;
      font-size: 14px;
      color: #8c8c8c;
      line-height: 25px;
      border-top: none;
      position: relative;
    }

    #accordion .panel-body p {
      padding-left: 25px;
      border-left: 1px dashed #8c8c8c;
    }

  </style>
</head>
<body>

<header class="header">
  <h2>API文档 </h2>
  <p>HServer提供</p>
</header>
<div class="demo">
  <div class="container">
    <div class="row">
      <div class=" col-md-12">
        <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
              <#list data as list>
                <div class="panel panel-default">
                  <div class="panel-heading" role="tab" id="headingThree${list_index}">
                    <h4 class="panel-title">
                      <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion"
                         href="#collapseThree${list_index}"
                         aria-expanded="false" aria-controls="collapseThree${list_index}">
                        ${list.name}
                      </a>
                    </h4>
                  </div>
                  <div id="collapseThree${list_index}" class="panel-collapse collapse" role="tabpanel"
                       aria-labelledby="headingThree${list_index}">

          <#list list.apiData as api>
            <div class="panel-body">
              <div class="panel">
                <div class="panel-heading">
                  <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#" href="#four${api.name}${api_index}">
                      ${api.name}
                    </a>
                  </h4>
                </div>
                <div id="four${api.name}${api_index}" class="panel-collapse collapse in">
                  <div class="panel-body">
                    <div class="api-sub-content">
                      <div class="simpleline"><strong>接口地址：</strong><span
                        class="url"> ${api.url}</span></div>
                      <div class="simpleline"><strong>请求方式：</strong><span class="url">http
                        <#list api.requestMethod as req>
                          ${req}
                        </#list>
                        </span></div>
                    </div>
                    <div class="simpleTable">
                      <table class="table table-bordered">
                        <caption>请求参数说明：</caption>
                        <thead>
                        <tr class="title">
                          <th width="100">名称</th>
                          <th width="60">必填</th>
                          <th width="80">类型</th>
                          <th>说明</th>
                        </tr>
                        </thead>
                        <tbody>
                         <#list api.reqDataList as reqdata>
                         <tr>
                           <td class="url">${reqdata.name}</td>
                           <td class="url">
                               <#if reqdata.required>
                                 是
                               <#else>
                                否
                               </#if>
                           </td>
                           <td class="url">${reqdata.dataType}</td>
                           <td>${reqdata.value}</td>
                         </tr>
                         </#list>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </#list>
                  </div>
                </div>
              </#list>

        </div>
      </div>
    </div>
  </div>
</div>

<script src="/js/jquery-1.11.0.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
</body>
</html>
