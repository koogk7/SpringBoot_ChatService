// 자기 이전의 문서의 모든 콘텐츠(script, css 등)이 로드된 후 발생되는 이벤트
// window.addEventListener("load", ()=>{
//     WebSocket.init();
// });

// WebSocket 은 클로저
// JS는 클로저를 통해 캡슐화, 모듈화 작업을 수행 할 수 있다.
// 클로저는 함수 밖에서 선언된 변수를 함수 내부에서 사용할 때 생겨난다.
let WebSocket = (()=>{
    const SERVER_SOCKET_API = "room"; // endpoint
    const ENTER_KEY = 13;
    let stompClient;
    let textArea = document.getElementById( "chatOutput");
    let inputEle = document.getElementById("chatInput");
    let roomId;
    let userNumber;
    let userName;

    function init(_roomId, _userNumber, _userName) {
        console.log("Socket Init......");
        roomId = _roomId;
        userNumber = _userNumber;
        userName = _userName;
        connect();
        inputEle.addEventListener("keydown", chatKeyDownHandler);
    }

    //-------소켓을 연결한다 ------------
    function connect() {

        //localhost:8080/{SERVER_SOCKET_API}와 연결하는 소켓을 만든다.
        let socket = new SockJS(SERVER_SOCKET_API)

        // 만든 소켓을 서버에 등록
        stompClient = Stomp.over(socket)
        stompClient.connect({}, (frame)=>{ // frame에는 연결정보가 담겨있다.
            console.log("Connect Success");
            console.log(frame);

        // /topic/message로 구독을 신청한다. 해당 url로 메시지가 올 경우 콜백함수가 호출
        // 주로 일대다 채팅은 /topic, 일대일 통신은 /queue를 사용한다.
           stompClient.subscribe('/topic/message/' + roomId , (msg)=>{
               msg = JSON.parse(msg.body);
               console.log(msg);
               console.log(msg.readCnt);
               //sendMsg = '';
                /*
               if(msg.readCnt == -1)
                    sendMsg = msg.content;
               else
                    sendMsg = msg.member.name +" : " +  msg.content;
                */
               printMessage(msg);
               //todo 스크롤 내리기
            });
        });
    }

    //-------ENTER 입력시 서버에 데이터 전송------------
    function chatKeyDownHandler(e) {
        if (e.which == ENTER_KEY && inputEle.value.trim() !== ""){
            sendMessage(inputEle.value);
            clear(inputEle);
        }
    }

    //-------서버에서 온 message 채팅창에 출력------------
    function printMessage(message) {
        if(message.member.name === userName)
            appendHtml(textArea,'<div class="d-flex justify-content-end mb-4">'+
                '<div class="msg_cotainer_send">'+message.content+
                '<span class="msg_time_send">8:55 AM, Today</span>'+
                '</div>'+
                '<div class="img_cont_msg">'+
                '<img src=" ' + message.member.img + ' " class="rounded-circle user_img_msg">'+
                '</div>'+
                '</div>');
        else
            appendHtml(textArea,'<div class="d-flex justify-content-start mb-4">\n' +
                            '<div class="img_cont_msg">\n' +
                            '<img src='+ '"' +message.member.img+'"'+'class="rounded-circle user_img_msg">\n' +
                            '</div>\n' +
                            '<div class="msg_cotainer">\n' +
                            message.content+
                            '<span class="msg_time">8:40 AM, Today</span>\n' +
                        '</div>\n' +
                        '</div>');
        // TODO ( " 가 제대로 안찍힘 ..)
    }

    //-------서버에 message 전송------------
    function sendMessage(text) {
        console.log("Send Message")
        stompClient.send("/app/chat/"+ roomId, {},
            JSON.stringify({"content" : text, "cid": roomId, "readCnt": userNumber, "userName": userName}));
    }

    function sendEixt() {
        stompClient.send("/app/exit_room/"+ roomId, {},
            // JSON.stringify({"memberName" : userName}));
            userName);
    }
    
    function clear(input) {
        input.value = "";
    }

    function appendHtml(el, str) {
        var div = document.createElement('div');
        div.innerHTML = str;
        while (div.children.length > 0) {
            el.appendChild(div.children[0]);
        }
    }

    return {
        init : init,
        sendEixt : sendEixt
    }
})();