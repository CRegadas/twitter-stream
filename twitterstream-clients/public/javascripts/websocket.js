function init() {

    var websocket = new WebSocket("ws://localhost:9000/top");

    websocket.onopen = function(evt){
        websocket.send("INIT");
        console.log("connection opened.");
    };

    websocket.onclose = function(evt){
        websocket.close();
        console.log("connection closed.");
    };

    websocket.onmessage = function(evt){
        if (evt.data != "INIT")
        {
            var object = JSON.parse(evt.data);
            pieChart(object)
        }
        console.log("Client received data and say TOP");
        websocket.send("TOP");

    };

    websocket.onerror = function(evt){
        console.log("Error detected: " + evt.data);
    };

}

function pieChart(obj)
{
    var data = [];

    console.log("Entrei pieChart");
    console.log("pieChart data: ",obj);

    /*top.forEach(function(entry) {

        data.push({
          value: entry.value,
          color: "#46BFBD",
          highlight: "#FF5A5E",
          label: entry.label
        });
    });

    console.log(data)

    window.onload = function(){
        var ctx = document.getElementById("pie-chart-area").getContext("2d");
        window.myPie = new Chart(ctx).Pie(data);
    };*/

}

window.addEventListener("load", init, false);