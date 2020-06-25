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

        * {
            color: #2d6a88;
        }

        h1 {
            margin: 0;
            color: #3a87ad;
            font-size: 26px;
        }

        .content {
            margin: 5vh auto;
            padding: 20px;
            border-radius: 12px;
            overflow: hidden;
            overflow-x: scroll;
            width: 50%;
            background: #d9edf7;
        }

        .content dl {
            line-height: 40px;
        }

        .flex {
            display: flex;
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
                <a target="_blank" href="${business.communityAddress}">这个问题需要社区力量？</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
