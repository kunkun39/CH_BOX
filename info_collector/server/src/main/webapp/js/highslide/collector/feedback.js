var sta_container1 ={
    chart: {
        type: 'line',
        renderTo: 'container1'
    },
    title: {
        text: '用户反馈统计'
    },
    subtitle: {
        text: ''
    },
    xAxis: {
        categories: []
    },
    yAxis: {
        title: {
            text: '次数'
        },
        min: 0
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' + this.x + '号: ' + this.y + '次';
        }
    },
    legend: {
        enabled: true
    },
    plotOptions: {
        line: {
            dataLabels: {
                enabled: true
            },
            enableMouseTracking: true
        }
    }, series: [
        {
            name: '用户反馈',
            data: []
        }
    ],
    credits: {
        text: ''
    }
    };

$(document).ready(function(){
    $('#getcsv').click(function () {
        var chart=new Highcharts.Chart(sta_container1);
        alert(chart.getCSV());
    });
    $("#download").click(function(){
        var chart=new Highcharts.Chart(sta_container1);
        Highcharts.post('http://export.hcharts.cn/cvs.php', {
            csv: chart.getCSV()
        });
    })
});

