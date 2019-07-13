var sendMsg = document.querySelector("#send");
var leaveConv = document.querySelector("#leave");
var messageInput = document.querySelector('#message');
var registration = document.querySelector('#registration');
var registrationInput = document.querySelector('#name');
var registrationForm = document.querySelector('#username-page');
var chatForm = document.querySelector('#chat-page');
var messageArea = document.querySelector('#message-area');
var userName = null;
var userRole = null;
var socket = null;
var companionColor = null;
var userColor = null;
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

window.onload = function () {

    userColor = getColor();
    companionColor = getAnotherColor();

    registration.onclick = function (event) {
        var input = registrationInput.value.trim();
        if (input.match("^\\/register+(?:\\s\\S+){2}$")) {
            var inputData = input.split(" ");
            userName = inputData[2];
            userRole = inputData[1];
            if (inputData[1].toUpperCase() === "CLIENT" || inputData[1].toUpperCase() === "AGENT") {
                connectClient();
                registrationForm.classList.add('hidden');
                chatForm.classList.remove('hidden');
                event.preventDefault();
            } else {
                alert("Регистрация не пройдена");
            }
        } else {
            alert("Регистрация не пройдена");
        }
    }

    sendMsg.onclick = function (event) {
        event.preventDefault();
        var content = messageInput.value.trim();
        if (content.toUpperCase() === "/EXIT") {
            socket.onclose();
            location.href="logout";
        } else if (content !== "") {
            var msg = new Object();
            msg.from = userName;
            msg.content = content;
            msg.timestamp = getTimestamp();
            socket.send(JSON.stringify(msg));
        }
        messageInput.value = "";
    }

    leaveConv.onclick = function (event) {
        event.preventDefault();
        var msg = new Object();
        msg.from = userName;
        msg.content = "/leave";
        msg.timestamp = getTimestamp();
        socket.send(JSON.stringify(msg));
    }

}

window.onbeforeunload = function () {
    socket.onclose();
}

function connectClient() {

    var host = document.location.host;
    var pathname = document.location.pathname;
    socket = new WebSocket("ws://" + host + pathname + "/" + userRole + "/" + userName);

    socket.onopen = function (event) {
    }

    socket.onclose = function (event) {
    }

    socket.onerror = function (event) {
    }

    socket.onmessage = function (event) {
        var message = JSON.parse(event.data);
        var from = message.from;
        var content = message.content;
        var timestamp = message.timestamp;
        var color;
        var name;

        if(from === 'SERVER' && content.substring(0, 26) === "Ваш идентификатор сессии: "){
            return
        }

        if (from === userName) {
            name = userName + " (Вы)";
            color = userColor;
        } else {
            name = from;
            color = companionColor;
        }
        var messageElement = document.createElement('li');
        if (message.from === 'SERVER') {
            messageElement.classList.add('event-message');
        } else {
            messageElement.classList.add('chat-message');
            var avatarPicture = document.createElement('i');
            var avatarText = document.createTextNode(name.charAt(0));
            avatarPicture.appendChild(avatarText);
            avatarPicture.style['background-color'] = color;
            messageElement.appendChild(avatarPicture);
            var nameElement = document.createElement('span');
            var nameText = document.createTextNode(name);
            nameElement.appendChild(nameText);
            messageElement.appendChild(nameElement);
        }
        var italicText = document.createElement('sup');
        italicText.appendChild(document.createTextNode(timestamp))
        var timestampElement = document.createElement('p');
        timestampElement.appendChild(italicText);
        var contextElement = document.createElement('p');
        contextElement.appendChild(document.createTextNode(content));
        messageElement.appendChild(timestampElement);
        messageElement.appendChild(contextElement);
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }
}

function getTimestamp() {
    var today = new Date();
    var dd = today.getDate();
    var MM = today.getMonth() + 1;
    var yyyy = today.getFullYear();

    var ss = today.getSeconds();
    var mm = today.getMinutes();
    var HH = today.getHours();

    if (dd < 10) {
        dd = '0' + dd
    }

    if (MM < 10) {
        MM = '0' + MM
    }
    if (ss < 10) {
        ss = '0' + ss
    }
    if (mm < 10) {
        mm = '0' + mm
    }
    if (HH < 10) {
        HH = '0' + HH
    }

    return '[' + dd + '.' + MM + '.' + yyyy + ' ' + HH + ':' + mm + ':' + ss + ']';
}

function getColor() {
    var index = Math.round(Math.random() * colors.length);
    return colors[index];
}

function getAnotherColor() {
    var index;
    var tempColor;
    while (true) {
        index = Math.round(Math.random() * colors.length);
        tempColor = colors[index];
        if (userColor !== tempColor) {
            break;
        }
    }
    return tempColor;
}