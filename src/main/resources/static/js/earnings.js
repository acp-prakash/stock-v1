// Define your constants
const TABLE_CONTAINER_ID = 'EARNINGS';
const DOWNLOAD_BUTTON_ID = 'download_EARNINGS';
const CLEAR_BUTTON_ID = 'clear_EARNINGS';
const DATA_URL = 'earnings/history?ticker=';
var ticker;
// Initialize Tabulator
const table = createEARNINGSTable();

function createEARNINGSTable() {
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
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 75, frozen: true },
            { title: 'EARNING DATE', field: 'date', headerFilter: true, width: 122 },
            { title: 'TIME', field:'time', headerFilter:true, width:90},
			{ title: 'EPS EST', field:'epsEst', headerFilter:true,width:78},						
			{ title: 'EPS ACT', field:'epsAct', sorter:'string',headerFilter:true, width:81},
			{ title: 'PRICE EFFECT', field:'priceEffect', sorter:'number',headerFilter:true,width:110},
			//{ title: 'STATUS', field:'isBeat', headerFilter:true,width:78},
			{ title: 'PRICE BEFORE', field:'priceBefore', headerFilter:true,width:115},
			//{ title: 'PRICE ON', field:'priceOn', headerFilter:true,width:90},
			{ title: 'PRICE AFTER', field:'priceAfter', headerFilter:true,width:110},
			{ title: 'PRICE NOW', field:'currPrice', headerFilter:true,width:105},
			{ title: 'P-STREAK', field:'positiveStreak', headerFilter:true,width:95},
			{ title: 'N-STREAK', field:'negativeStreak', headerFilter:true,width:95},
			{ title: 'CUR-LAST-DIFF', field:'currAndLastPriceEffectDiff', headerFilter:true,width:125},
			
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
    table.download('xlsx', 'EARNINGS.xlsx', { sheetName: 'EARNINGS' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

fetchEarningsHistory();

function fetchEarningsHistory() {
    $.ajax({
        type: 'GET',
        url: DATA_URL+ticker,
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