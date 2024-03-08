// Define your constants
const TABLE_CONTAINER_ID = 'OPTIONSHISTORY';
const DOWNLOAD_BUTTON_ID = 'download_OPTIONSHISTORY';
const CLEAR_BUTTON_ID = 'clear_OPTIONSHISTORY';
const DELETE_BUTTON_ID = 'delete_OPTIONSHISTORY';
const HISTORY_URL = 'history';
const DELETE_URL = 'delete';
var ticker;
// Initialize Tabulator
const table = createOPTIONSHISTORYTable();

function createOPTIONSHISTORYTable() {
    return new Tabulator(`#${TABLE_CONTAINER_ID}`, {
        height: '78.5%',
        maxHeight: '78.5%',
        placeholder: "<div style='display:inline-block; border:4px solid #333; border-radius:10px; background:#fff; font-weight:bold; font-size:16px; color:#000; padding:10px 20px;margin-left: -7500px;'>Loading Data</div",
        data: [],
        layout: 'fitDataTable',
        pagination: 'local',
        paginationSize: 20,
        paginationCounter: 'rows',
        movableColumns: true,
        //resizableRows: true,
        initialSort: [
            { column: 'date', dir: 'desc' }
        ],
        columns: [
            { title: 'DATE', field: 'histDate', headerFilter: true, width: 80, frozen: true },
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 60, frozen: true },            
			{ title: 'NAME', field: 'name', headerFilter: true, width: 130, frozen: true },
           { title: 'LOW', field:'low', headerFilter:true,width:65},
			{ title: 'PRICE', field:'price', headerFilter:true,width:67},						
			{ title: 'CHG', field:'change', sorter:'number',headerFilter:true, width:60},
			
			{ title: 'HIGH', field:'high', headerFilter:true,width:65},
			{ title: 'OPEN', field:'open', headerFilter:true,width:67},
			{ title: 'VOL', field:'volume', headerFilter:true,width:60},
			{ title: 'O-INT', field:'interest', headerFilter:true,width:70},
			{ title: 'D', field:'delta', headerFilter:true,width:50},
			{ title: 'G', field:'gamma', headerFilter:true,width:50},
			{ title: 'T', field:'theta', headerFilter:true,width:50},
			{ title: 'IV', field:'iv', headerFilter:true,width:50},
			{ title: 'S-PRICE', field:'stockPrice', headerFilter:true,width:85},
			{ title: 'SP-CHG', field:'stockPriceChg', headerFilter:true,width:80},
			{ title: 'A-PRICE', field:'stockPriceOnAdd', headerFilter:true,width:85},
			{ title: 'DAYS', field:'daysToExpire', headerFilter:true,width:65},
			{ title: 'STATUS', field:'status', headerFilter:true,width:78},
			{ title: 'SOURCE', field:'source', headerFilter:true,width:80},          
			{ title: 'ADD-ON', field:'addedDate', headerFilter:true,width:90},
			{ title: 'EXT-ON', field:'exitDate', headerFilter:true,width:80},
			 { title: 'TYPE', field: 'type', headerFilter: true, width: 75},
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
    table.download('xlsx', 'OPTIONSHISTORY.xlsx', { sheetName: 'OPTIONSHISTORY' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

document.getElementById(DELETE_BUTTON_ID).addEventListener('click', () => {	
	$.ajax({
        type: 'POST',
        url: DELETE_URL+"?ticker="+ticker,
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
});

fetchOPTIONSHISTORYHistory();

function fetchOPTIONSHISTORYHistory() {
    $.ajax({
        type: 'GET',
        url: HISTORY_URL+"?ticker="+ticker,
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