// Define your constants
const TABLE_CONTAINER_ID = 'OPTIONS';
const DOWNLOAD_BUTTON_ID = 'download_OPTIONS';
const CLEAR_BUTTON_ID = 'clear_OPTIONS';
const DATA_URL = "ws://localhost:8070/stock-v1/wsoptions?page=options&user=ACP";
var ticker;
// Initialize Tabulator
const table = createOPTIONSTable();

function createOPTIONSTable() {
    return new Tabulator(`#${TABLE_CONTAINER_ID}`, {
        height: '78.5%',
        maxHeight: '78.5%',
        placeholder: "<div style='display:inline-block; border:4px solid #333; border-radius:10px; background:#fff; font-weight:bold; font-size:16px; color:#000; padding:10px 20px;margin-left: -7500px;'>Loading Data</div",
        data: [],
        layout: 'fitDataTable',
        pagination: 'local',
        paginationSize: 24,
        paginationCounter: 'rows',
        movableColumns: true,
        //resizableRows: true,
        initialSort: [
            { column: 'date', dir: 'desc' }
        ],
        columns: [
			{title:"", field:"ticker", width:40, headerSort:false, frozen:true,
				formatter: function () {
                    return "<a href=\"#\" >Patn</a>";
                },			
				cellClick:function(e, cell)
				{
					openPattern(cell.getRow().getData().ticker);
				},
			},
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 60, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().ticker+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openStockHistory(cell.getRow().getData().ticker);
				},
			 },
            { title: 'NAME', field: 'name', headerFilter: true, width: 140, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().name+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openHistory(cell.getRow().getData().key);
				},
			 },
			 { title: 'ADD-ON', field:'addedDate', headerFilter:true,width:85},
			{ title: 'PT%', field:'fromPtPc', headerFilter:true, width:60},
			{ title: 'A', field:'pattern.count', headerFilter:true, width:43},
            { title: 'P', field:'pattern.bull', headerFilter:true, width:43},
            { title: 'N', field:'pattern.bear', headerFilter:true, width:44},
           
            { title: 'AL', field:'aLow', headerFilter:true,width:50},
			{ title: 'LOW', field:'low', headerFilter:true,width:64},
			{ title: 'PRICE', field:'price', headerFilter:true,width:67},						
			{ title: 'HIGH', field:'high', headerFilter:true,width:65},
			{ title: 'AH', field:'aHigh', headerFilter:true,width:53},
			{ title: 'CHG', field:'change', sorter:'number',headerFilter:true, width:60},
			{ title: 'OPEN', field:'open', headerFilter:true,width:67},			
			{ title: 'VOL', field:'volume', headerFilter:true,width:60},
			{ title: 'O-INT', field:'interest', headerFilter:true,width:70},
			{ title: 'DAY', field:'daysToExpire', headerFilter:true,width:60},
			{ title: 'D', field:'delta', headerFilter:true,width:45},
			{ title: 'G', field:'gamma', headerFilter:true,width:45},
			{ title: 'T', field:'theta', headerFilter:true,width:45},
			{ title: 'IV', field:'iv', headerFilter:true,width:48},			
			{ title: 'S-PRICE', field:'stockPrice', headerFilter:true,width:80},
			{ title: 'SP-CHG', field:'stockPriceChg', headerFilter:true,width:78},
			{ title: 'A-PRICE', field:'stockPriceOnAdd', headerFilter:true,width:80},			
			{ title: 'STATUS', field:'status', headerFilter:true,width:78},
			{ title: 'SOURCE', field:'source', headerFilter:true,width:80},			
			{ title: 'EXT-ON', field:'exitDate', headerFilter:true,width:80},
			 { title: 'TYPE', field: 'type', headerFilter: true, width: 64},
            { title: 'ENTRY', field: 'entry', headerFilter: true, width: 75 },
            { title: 'EXIT', field:'exit', headerFilter:true, width:60},
        ],
    });
}

function getFmtTime(dt) {
    if (dt == null) {
        return dt;
    }
    const newDt = new Date(dt);
    let val = new Date(newDt.getTime() + (-300 * 60 * 1000)).toISOString().slice(0, 19);
    val = val.replace('T', ' ');
    let time = val[11] + val[12];
    if (time > 12) {
        time = time - 12;
        if (time < 10) {
            time = '0' + time;
        }
        val[11] = time[0];
        val[12] = time[1];
    }
    return val;
}

document.getElementById(DOWNLOAD_BUTTON_ID).addEventListener('click', () => {
    table.download('xlsx', 'OPTIONS.xlsx', { sheetName: 'OPTIONS' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

var socket = new WebSocket(DATA_URL);

socket.onopen = function (event) {
    console.log("WebSocket connection opened");
};

socket.onmessage = function (event) {
    var options = JSON.parse(event.data);
    console.log("Received options:", options);
    table.replaceData(options);
    table.redraw(true);
};

function openHistory(key) {    	
    window.open("options/option-history?ticker="+key, "_blank");
};

function openStockHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};

function openPattern(key) {    	
    window.open("pattern/getPatternHistory?ticker="+key, "_blank");
};