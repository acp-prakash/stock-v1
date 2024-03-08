const TABLE_CONTAINER_ID = 'FUTURES';
const DOWNLOAD_BUTTON_ID = 'download_FUTURES';
const CLEAR_BUTTON_ID = 'clear_FUTURES';
const DATA_URL = 'getFutures';
var ticker;
let FUTURESTable;

function createFUTURESTable() {
    FUTURESTable = new Tabulator(`#${TABLE_CONTAINER_ID}`, {
        height: '78.3%',
        maxHeight: '78.3%',
        placeholder: "<div style='display:inline-block; border:4px solid #333; border-radius:10px; background:#fff; font-weight:bold; font-size:16px; color:#000; padding:10px 20px;margin-left: -7500px;'>Loading Data</div",
        data: [],
        layout: 'fitDataTable',
        pagination: 'local',
        paginationSize: 20,
        paginationCounter: 'rows',
        movableColumns: true,
        //resizableRows: true,
        initialSort: [
            { column: 'contractExpiry', dir: 'asc' }
        ],
        columns: [			
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 60, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().ticker+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openHistory(cell.getRow().getData().ticker);
				},
			 },
            { title: 'NAME', field: 'contractName', headerFilter: true, width: 180, frozen: true },
            { title: 'CHANGE', field: 'change', sorter:'number',headerFilter:true, frozen: true, width:85},
            { title: 'PRICE', field: 'price', sorter:'number',headerFilter:"number", headerFilterFunc:"<=", frozen: true, width:70},
            { title: 'EXPIRY', field: 'contractExpiry', sorter:'number',headerFilter:true, width:84},
            { title: 'OPEN-INT', field: 'openInterest', sorter:'number',headerFilter:true, width:95},
            { title: 'TRP', field: 'trackingPrice', sorter:'number',headerFilter:"number", headerFilterFunc:"<=",  width:70},
            { title: 'TRD', field: 'trackingDiff', sorter:'number',headerFilter:"number", headerFilterFunc:"<=",  width:70},
            { title: 'UP-HI', field: 'upHigh', sorter:'number',headerFilter:true, width:68},
			{ title: 'DON-LO', field: 'downLow', sorter:'number',headerFilter:true, width:84},            	
			{ title: 'UP', field: 'upDays', headerFilter:true,width:53},
			{ title: 'UP $', field: 'upBy', sorter:'number',headerFilter:true, width:65},			
			{ title: 'DOWN', field: 'downDays', headerFilter:true,width:75},
			{ title: 'DOWN $', field: 'downBy', sorter:'number',headerFilter:true, width:84},			
			{ title: 'POINTS', field: 'contractPoint', sorter:'number',headerFilter:true, width:84},
			{ title: 'MARGIN', field: 'contractMargin', sorter:'number',headerFilter:true, width:84},
			
        ],
    });
}

function openHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};

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
        if (time < 10)
            time = '0' + time;
        val[11] = time[0];
        val[12] = time[1];
    }
    return val;
}

document.getElementById(DOWNLOAD_BUTTON_ID).addEventListener('click', () => {
    FUTURESTable.download('xlsx', 'FUTURES.xlsx', { sheetName: 'FUTURES' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    FUTURESTable.clearHeaderFilter();
    FUTURESTable.clearFilter();
    clearCheckBoxes();
});

fetchFutures();

function fetchFutures() {
    createFUTURESTable();
    $.ajax({
        type: 'GET',
        url: DATA_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            FUTURESTable.replaceData(response);
            FUTURESTable.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}