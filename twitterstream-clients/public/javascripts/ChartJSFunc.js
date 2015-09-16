function pieChart(top)
{
    var data = [];

    top.forEach(function(entry) {

        data.push({
          value: entry.value,
          color: entry.color,
          highlight: "#FF5A5E",
          label: entry.label
        });
    });

    console.log(data)

    window.onload = function(){
        var ctx = document.getElementById("pie-chart-area").getContext("2d");
        window.myPie = new Chart(ctx).Pie(data);
    };

}

pieChart([{label:"lalal",value:300,color:"#F7464A"}, {label:"tetetet",value:40,color:"#46BFBD"}, {label:"tututut",value:500,color:"#FDB45C"}])