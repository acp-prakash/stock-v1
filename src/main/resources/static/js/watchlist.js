// Define your constants
const TABLE_CONTAINER_ID = 'WATCHLIST';
const ADD_BUTTON_ID = 'add';
const DELETE_BUTTON_ID = 'delete';
const DOWNLOAD_BUTTON_ID = 'download';
const CLEAR_BUTTON_ID = 'clear';
const UPDATE_BUTTON_ID = 'datafetch';
const AUTOMATION_BUTTON_ID = 'automation';
const GET_DATA_URL = 'WatchList/getWatchList';
const ADD_DATA_URL = 'WatchList/addWatchList';
const DELETE_DATA_URL = 'WatchList/deleteWatchList';
const DATA_FETCH_URL = 'WatchList/dataFetch';
const AUTOMATION_URL = 'automation/StartAutomation';
var ticker;

// Initialize Tabulator
const table = createWATCHLISTTable();
function createWATCHLISTTable() {
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
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 65, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().ticker+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openStockHistory(cell.getRow().getData().ticker);
				},
			 },
			 { title: 'CHG', field: 'change', sorter:'number',headerFilter:true, frozen: true, width:65},
			 { title: 'PRICE', field: 'price', sorter:'number',headerFilter:"number", headerFilterFunc:"<=", width:70},
			 { title: 'EARN-DAYS', field: 'daysToEarnings', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:103},
			 { title: 'UP', field: 'upDays', headerFilter:true,width:53},			 			
			 { title: 'DOWN', field: 'downDays', headerFilter:true,width:75},
			 { title: 'A', field: 'pattern.count', headerFilter:true,width:75},
			 { title: 'P', field: 'pattern.bull', headerFilter:true,width:75},
			 { title: 'N', field: 'pattern.bear', headerFilter:true,width:75},
        ],        
    });
}

document.getElementById(ADD_BUTTON_ID).addEventListener('click', () => {	
	addWatchList(document.getElementById("ticker").value);	
});

document.getElementById(DELETE_BUTTON_ID).addEventListener('click', () => {	
    deleteWatchList(document.getElementById("ticker").value);
});

document.getElementById(DOWNLOAD_BUTTON_ID).addEventListener('click', () => {
    table.download('xlsx', 'WATCHLIST.xlsx', { sheetName: 'WATCHLIST' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

document.getElementById(UPDATE_BUTTON_ID).addEventListener('click', () => {
	updateAllData();    
});

document.getElementById(AUTOMATION_BUTTON_ID).addEventListener('click', () => {
	startAutomation();    
});


function updateAllData() {
    $.ajax({
        type: 'GET',
        url: DATA_FETCH_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {            
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function startAutomation() {
    $.ajax({
        type: 'GET',
        url: AUTOMATION_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {            
        },
        error: function (error) {
            console.log(error);
        }
    });
}

getWatchList();

function getWatchList() {
    $.ajax({
        type: 'GET',
        url: GET_DATA_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            table.replaceData(response);
            table.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function addWatchList(ticker) {

	var data = {
			"ticker": ticker.toUpperCase()				
		};
	$.ajax({
		type : "POST",
		url : ADD_DATA_URL,
		cache: false,
		contentType: "application/json;",
		data:JSON.stringify(data),
		success: function(response){
			table.replaceData(response);
			table.redraw(true);			
		},
		error: function(error){				
			console.log(error);
		}
	});
};

function deleteWatchList(ticker) {

	var data = {
			"ticker": ticker.toUpperCase()				
		};
	$.ajax({
		type : "POST",
		url : DELETE_DATA_URL,
		cache: false,
		contentType: "application/json;",
		data:JSON.stringify(data),
		success: function(response){
			table.replaceData(response);
			table.redraw(true);			
		},
		error: function(error){				
			console.log(error);
		}
	});
};

function openStockHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};

function openPattern(key) {    	
    window.open("pattern/getPatternHistory?ticker="+key, "_blank");
};
