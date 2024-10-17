// Define your constants
const TABLE_CONTAINER_ID = 'OPTIONS';
const DOWNLOAD_BUTTON_ID = 'download';
const CLEAR_BUTTON_ID = 'clear';
const SAVE_BUTTON_ID = 'save';
const DATA_URL = 'options/getOptions';
const UPDATE_URL = 'options/updateOptions';
var ticker;
var updatedRows=[];
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
			//{ title: 'PT%', field:'fromPtPc', headerFilter:true, width:60},
						
			/*{ title: 'ML', field:'pctMaxL', headerFilter:true,width:52},
			{title:'PL', field:"pctPL", headerFilter:true, width:50, formatter:function(cell){
				var result = cell.getRow().getData().pctPL;
				if(result < 0 ){
					 return "<span style='background-color:black; color:orangered; font-weight:bold; display: grid;font-size: 14px;width:40px'>" + (result *-1) + "</span>";
			    }else{
					 return "<span style='background-color:black; color:yellowgreen; font-weight:bold; display: grid;font-size: 14px;width:40px'>" + cell.getValue() + "</span>";
			    }
			}},			
			{ title: 'MP', field:'pctMaxP', headerFilter:true,width:53},*/
			{ title: 'STATUS', field:'status', headerFilter:true,width:78,editor: "input",
				cellEdited:function(cell)
				{
					updateRows(cell.getRow());
				}},
			{ title: 'ENTRY', field:'addPrice', headerFilter:true,width:72,editor: "input",
				cellEdited:function(cell)
				{
					updateRows(cell.getRow());
				}},
			{ title: 'EXIT', field:'exit', headerFilter:true, width:60,editor: "input",
				cellEdited:function(cell)
				{
					updateRows(cell.getRow());
				}},           
            { title: 'CHG', field:'change', sorter:'number',headerFilter:true, width:60},
            { title: 'AL', field:'aLow', headerFilter:true,width:50},
			{ title: 'LO', field:'low', headerFilter:true,width:55},			
			{ title: 'PRICE', field:'price', headerFilter:true,width:67},
			{ title: 'HI', field:'high', headerFilter:true,width:55},
			{ title: 'AH', field:'aHigh', headerFilter:true,width:53},			
			//{ title: 'OPEN', field:'open', headerFilter:true,width:67},
			{ title: 'VOL', field:'volume', headerFilter:true,width:60},
			{ title: 'O-INT', field:'interest', headerFilter:true,width:70},			
			{ title: 'D', field:'delta', headerFilter:true,width:45},
			{ title: 'G', field:'gamma', headerFilter:true,width:45},
			{ title: 'A', field:'pattern.count', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:43},
            { title: 'P', field:'pattern.bull', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:43},
            { title: 'N', field:'pattern.bear', sorter:'number',headerFilter:"number", headerFilterFunc:"<", width:44},
            { title: 'B', field:'pattern.all.buyTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
            { title: 'S', field:'pattern.all.sellTrend', sorter:'number',headerFilter:"number", headerFilterFunc:"<", width:45},
            { title: 'UP', field: 'upDays', headerFilter:true,width:53},
			/*{ title: 'U$', field: 'upBy', sorter:'number',headerFilter:true, width:53},*/			
			{ title: 'DN', field: 'downDays', headerFilter:true,width:53},
			/*{ title: 'D$', field: 'downBy', sorter:'number',headerFilter:true, width:53},*/
			{ title: 'DAY', field:'daysToExpire', headerFilter:true,width:58},
			{ title: 'T', field:'theta', headerFilter:true,width:45},
			{ title: 'IV', field:'iv', headerFilter:true,width:48},			
			/*{ title: 'S-PRICE', field:'stockPrice', headerFilter:true,width:80,editor: "input"},
			{ title: 'SP-CHG', field:'stockPriceChg', headerFilter:true,width:78,editor: "input"},
			{ title: 'A-PRICE', field:'stockPriceOnAdd', headerFilter:true,width:80},*/
			{ title: 'SOURCE', field:'source', headerFilter:true,width:80,editor: "input",
				cellEdited:function(cell)
				{
					updateRows(cell.getRow());
				}},
			//{ title: 'EXT-ON', field:'exitDate', headerFilter:true,width:80},
			 { title: 'TYPE', field: 'type', headerFilter: true, width: 64},
            /*{ title: 'ENTRY', field: 'entry', headerFilter: true, width: 75 },
            { title: 'EXIT', field:'exit', headerFilter:true, width:60},*/
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

fetchOPTIONSHistory();

function fetchOPTIONSHistory() {
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

function openHistory(key) {    	
    window.open("options/option-history?ticker="+key, "_blank");
};

function openStockHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};

function openPattern(key) {    	
    window.open("pattern/getPatternHistory?ticker="+key, "_blank");
};

function updateRows(selectedRow) {	
	
	var existingIndex = updatedRows.findIndex(row => row.key === selectedRow.getData().key);

    if (existingIndex === -1) {
        // If the row doesn't exist in the store, add it
        updatedRows.push(selectedRow.getData());
    } else {
        // If the row exists, update the values
        updatedRows[existingIndex] = selectedRow.getData();
    }
};
	
document.getElementById(SAVE_BUTTON_ID).addEventListener('click', () => {
	if (updatedRows.length > 0) {
		$.ajax({
			type : "POST",
			url: UPDATE_URL,
	        cache: false,
	        contentType: 'application/json;',
			data:JSON.stringify(updatedRows),
			success: function (response) {
				updatedRows=[];
	            //table.replaceData(response);
	            //table.redraw(true);
	        },
	        error: function (error) {
	            console.log(error);
	        }
		});
	}
});