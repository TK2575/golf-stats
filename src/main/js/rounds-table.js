google.charts.load('current', {'packages':['table']});
google.charts.setOnLoadCallback(drawTable);

function drawTable() {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('string', 'Course');
    data.addColumn('number', 'Strokes');
    data.addColumn('number', 'Score');
    data.addColumn('number', 'TOT');
    data.addColumn('number', 'OTT');
    data.addColumn('number', 'APP');
    data.addColumn('number', 'ARG');
    data.addColumn('number', 'PUTT');
    data.addColumn('number', 'RCVR');
    data.addColumn('number', 'p75 Drive');
    data.addColumn('number', 'Great Shots');
    data.addColumn('number', 'Poor Shots');
    data.addColumn('number', 'Differential');
    data.addColumn('number', 'Handicap Index');
    data.addColumn('number', 'Yards');
    data.addRows([
    ['2024-01-27', 'Boulder Oaks GC', 85, 15, -22.62, -9.01, -1.92, -2.4, -9.57, 0.28, 232, 3, 22, 24.1, 13.6, 4774],
    ['2024-01-14', 'Cottonwood GC', 86, 14, -17.16, -3.13, -4.38, -2.74, -6.91, null, 242, 1, 14, 15.5, 12.8, 6289],
    ['2023-12-17', 'Weston GC - Oaks North (East)', 90, 24, -27.01, -7.4, -7.07, -2.65, -9.38, -0.51, 240, 0, 22, 22.7, 13, 4961],
    ['2023-10-29', 'Boulder Oaks GC', 91, 21, -26.9, -7.65, -9.16, -0.83, -9.26, null, 233, 3, 25, 20.7, 12.7, 5174],
    ['2023-09-17', 'Boulder Oaks GC', 91, 21, -27.7, -7.2, -8.74, -3.43, -8, -0.33, 229, 3, 21, 21.7, 12.3, 5057],
    ['2023-07-10', 'Sea \'n Air', 93, 22, -24.26, -5.9, -10.86, -2.52, -5.02, 0.04, 256, 2, 19, 17.5, 12, 6241],
    ['2023-07-04', 'Mt. Woodson GC', 83, 13, -18.61, -7.24, -1.28, -1.77, -8.32, null, 263, 3, 17, 14.9, 11.8, 5108],
    ['2023-06-25', 'Oaks North (North-East)', 72, 12, -15.09, -0.47, -2, -2.17, -10.45, null, 252, 5, 18, 19.7, 11.8, 3405],
    ['2023-04-30', 'Mission Trails GC', 94, 23, -27.49, -8.09, -10.23, -3.07, -6.1, null, 251, 1, 21, 20, 11.8, 5742]
    ]);
    
    var table = new google.visualization.Table(document.getElementById('rounds-table'));
    
    table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});
}

drawChart();