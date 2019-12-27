<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <p>${user2.name}</p>
    <p>${user2.age}</p>
    <p>${user2.sex}</p>
    <p>${list?size}</p>
    <#list list as being>
        <tr>
            <td>${being}<td>
        </tr>
    </#list>

</body>
</html>