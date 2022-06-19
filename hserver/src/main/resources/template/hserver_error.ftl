<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <title>错误提示</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <style>
        body {
            font: 16px arial, 'Microsoft Yahei', 'Hiragino Sans GB', sans-serif;
        }
        ::-webkit-scrollbar {
            display: none
        }
        * {
            color: #ffffff;
        }

        h1 {
            margin: 0;
            color: #ffffff;
            font-size: 26px;
        }

        .content {
            min-height: 94vh;
            padding: 20px;
            border-radius: 12px;
            overflow: hidden;
            overflow-x: scroll;
            background: #000000;
        }

        .content dl {
            line-height: 3vh;
        }

        .flex {
            display: flex;
            align-items: flex-start;
        }

        .flex > div {
            padding-top: 3vh;
            flex: 1;
            display: flex;
            justify-content: center;
        }

        .flex > div > div {
            text-align: center;
        }

        .flex2 {
            display: flex;
            justify-content: center;
        }

        .flex2 > div {
            margin: 0 20px;
        }

        .content div div {
            text-align: center;
        }
    </style>
</head>
<body>
<div class="content">
    <div>
        <h1>错误描述： ${business.errorDesc}</h1>

        <div class="flex">
            <div>
                <div>HServer: ${business.version}</div>
            </div>
            <div>
                <div>错误状态码: ${business.code}</div>
            </div>
            <div>
                <div>请求方式:${business.method}</div>
            </div>
        </div>

        <dl>
            <dt>URI:</dt>
            <dd>${business.url}</dd>
        </dl>
        <dl>
            <dt>请求参数:</dt>
            <dd>${business.args}</dd>
        </dl>
        <dl>
            <dt>原因:</dt>
            <dd>${business.errorMsg}</dd>
        </dl>

        <div class="flex2">
            <div>
                <a target="_blank" href="${business.bugAddress}">有Bug?反馈?</a>
            </div>
            <div>
                <a target="_blank"
                   href="https://qm.qq.com/cgi-bin/qm/qr?k=_zBDA2Wsf851BI5zKr2fn_hvoH0n9vM5&jump_from=webapi"><img
                            border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="HServer扯皮交流群"
                            title="HServer扯皮交流群"></a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
