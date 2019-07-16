<#include "parts/security.ftl">

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TouchSoft Чат</title>
    <link rel="stylesheet" href="../static/css/index.css" />
    <link rel="shortcut icon" href="../static/favicon.ico"/>
</head>
<body>
<noscript>
    <h2>Приносим извинения! Ваш браузер не поддерживает Javascript</h2>
</noscript>
    <input type="text" id="username" placeholder="username" autocomplete="off" class="form-control" hidden value="${name}"/>
    <input type="text" id="role" placeholder="role" autocomplete="off" class="form-control" hidden value="${role}"/>

<div id="chat-page">
    <div class="chat-container">
        <div class="chat-header">
            <h2>TouchSoft чат</h2>
            <sup>Ваш логин: ${name}. Ваша роль: <#if role == "AGENT">Агент<#else>Клиент</#if> <a href="/logout">(выйти)</a></sup>
        </div>
        <ul id="message-area">

        </ul>
        <form id="messageForm" name="messageForm">
            <div class="form-group">
                <div class="input-group clearfix">
                    <input type="text" id="message" placeholder="Введите сообщение..." autocomplete="off" class="form-control"/>
                    <button  id="send" type="submit" class="primary" value="Normal Submit">Отпр.</button>
                    <button  id="leave" type="button" class="danger" value="Leave Button">Покин.</button>
                </div>
            </div>
        </form>
    </div>
</div>
<script src="../static/js/index.js"></script>
</body>
</html>