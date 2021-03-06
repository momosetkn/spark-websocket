//Establish the WebSocket connection and set up event handlers
var webSocket = null;

var webSocketInit = function () {
	id('chatlogs').innerHTML = '';
	webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
	webSocket.onmessage = function (msg) { updateChat(msg); };
	webSocket.onopen = function () {
		var params = getQueryString();
		var nick = params['nick'];
		sendMessage( '/member '+nick);
		sendMessage( '/log');
	};
	webSocket.onclose = function () {
		webSocket = null;
	    setTimeout("webSocketInit()", 1500);
	};
};
webSocketInit();

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    if( "/member"==data.cmd ){
        id("userlist").innerHTML = "";
        data.userlist.forEach(function (user) {
            insert("userlist", "<li>" + user + "</li>");
        });
    }else if( "/msg"==data.cmd ||"/log"==data.cmd ){
    	var msg = data.message;
    	var msgHtml = "<article><b>"+data.sender+" says:</b><span class='timestamp'>"+data.saydate+"</span><div class='msgContent'>"+msg+"</div></article>"
    	insert("chatlogs", msgHtml);
    	var chatlogs = document.getElementById("chatlogs");
    	chatlogs.scrollTop = chatlogs.scrollHeight;//TODO ログ読んでるときはスクロールしないようにしたい
    	var msgContentTag = id('chatlogs').lastElementChild.getElementsByClassName("msgContent");
    	hljs.highlightBlock( msgContentTag[0] );
    }
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("beforeend", message);
     //hljs.initHighlightingOnLoad();
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

function getQueryString()
{
    var result = {};
    if( 1 < window.location.search.length )
    {
        // 最初の1文字 (?記号) を除いた文字列を取得する
        var query = window.location.search.substring( 1 );

        // クエリの区切り記号 (&) で文字列を配列に分割する
        var parameters = query.split( '&' );

        for( var i = 0; i < parameters.length; i++ )
        {
            // パラメータ名とパラメータ値に分割する
            var element = parameters[ i ].split( '=' );

            var paramName = decodeURIComponent( element[ 0 ] );
            var paramValue = decodeURIComponent( element[ 1 ] );

            // パラメータ名をキーとして連想配列に追加する
            result[ paramName ] = paramValue;
        }
    }
    return result;
}


(window.onload = function() {
	var resizelogs = function(){
		var chatControlsHeight = id('chatControls').offsetHeight;
		id('chatlogs').style.height = window.innerHeight - chatControlsHeight+'px';
	}
	resizelogs();
	window.addEventListener("resize", resizelogs);

	/** 改行コード：CrLf */
	var CRLF = "\r\n";
	/**
	 * 画面表示時
	 */
	document.getElementById("message").onkeydown = msgKeypress;
	function msgKeypress(e) {
	    if (e.which == 13) {
	        if (e.altKey || e.shiftKey) {
	            // alt + Enter -> CrLf
	            var first = e.currentTarget.value.substring(0, e.currentTarget.selectionStart);
	            var second = e.currentTarget.value.substr(e.currentTarget.selectionEnd);
	            e.currentTarget.value = first + CRLF + second;
	            e.currentTarget.selectionStart = first.length + 1;
	            e.currentTarget.selectionEnd = first.length + 1;
	            return false;
	        } else {
	        	var msg = e.target.value;
	        	if( msg ){
	        		sendMessage('/msg '+msg);
	        	}
	            // Enter key not working
	            return false;
	        }
	    }
	};
	document.getElementById("message").oncontextmenu = function(e){
		e.target.value = e.target.value + '<pre><code>'+CRLF+CRLF+'</code></pre>';
		return false;
	}

})();

