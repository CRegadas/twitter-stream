var data = [];
var canDrawPieChart = true;

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
            var top = JSON.parse(evt.data);
            console.log(evt.data)

            if (canDrawPieChart) {
                canDrawPieChart = false;
                pieChart(top)
                data = []
            }


        }
        console.log("Client received data and say TOP");
        websocket.send("TOP");

    };

    websocket.onerror = function(evt){
        console.log("Error detected: " + evt.data);
    };

}

setInterval(function () {
    console.log('interval called');
    canDrawPieChart = true;
}, 60000);


function pieChart(top)
{

    console.log("Entrei pieChart");
    console.log("pieChart data: ",top);

    data = top.map(function ( o ) {
        return {
          value: o.value,
          color: "#46BFBD",
          highlight: "#FF5A5E",
          label: o.label
        }
    });

    console.log("FINAL: ",data)


    var ctx = document.getElementById("pie-chart-area").getContext("2d");
    window.myPie = new Chart(ctx).Pie(data);


}

window.addEventListener("load", init, false);