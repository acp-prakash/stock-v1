// Define your constants
const TABLE_CONTAINER_ID = 'PICKS';
const DOWNLOAD_BUTTON_ID = 'download_PICKS';
const CLEAR_BUTTON_ID = 'clear_PICKS';
const DATA_URL = 'getPicks?ticker=';
var ticker;
const table = createPICKSTable();

function createPICKSTable() {
    return new Tabulator(`#${TABLE_CONTAINER_ID}`, {
       	height: '81.7%',
        maxHeight: '81.7%',
        placeholder: "<div style='display:inline-block; border:4px solid #333; border-radius:10px; background:#fff; font-weight:bold; font-size:16px; color:#000; padding:10px 20px;margin-left: -7500px;'>Loading Data</div",
        data: [],
        layout: 'fitDataTable',
        pagination: 'local',
        paginationSize: 16,
        paginationCounter: 'rows',
        movableColumns: true,
        //resizableRows: true,
        initialSort:[
	        {column:"addedDate", dir:"desc"}, //then sort by this second
	    ],
        columns: [			
			{ title: 'TICKER', field: 'ticker', headerFilter: true, width: 100, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().ticker+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openHistory(cell.getRow().getData().ticker);
				},
			 },
            {title:'RESULT', field:"result", headerFilter:"list", headerFilterParams:{valuesLookup:true, clearable:true}, width:130, formatter:function(cell){
				var result = cell.getRow().getData().result;
				if(result === "HIT-T1" || result === "HIT-T2"){
					 return "<span style='background-color:black; color:lawngreen; font-weight:bold; display: grid;font-size: 17px;width:130px'>" + cell.getValue() + "</span>";
			    }else if(result === "PENDING"){
					 return "<span style='background-color:black; color:aliceblue; font-weight:bold; display: grid;font-size: 17px;width:130px'>" + cell.getValue() + "</span>";
			    }else if(result === "DELAY-HIT-T1" || result === "DELAY-HIT-T2"){
					 return "<span style='background-color:black; color:yellow; font-weight:bold; display: grid;font-size: 17px;width:130px'>" + cell.getValue() + "</span>";
			    }else if(result === "STOP-HIT-T1" || result === "STOP-HIT-T2"){
					 return "<span style='background-color:black; color:yellowgreen; font-weight:bold; display: grid;font-size: 17px;width:130px'>" + cell.getValue() + "</span>";
			    }
			    else{
			       	return "<span style='background-color:black; color:orangered; font-weight:bold; display: grid;font-size: 17px;width:130px'>" + cell.getValue() + "</span>";
			    }
			}},
            { title: 'ADD-DATE', field:'addedDate', headerFilter:true, width:125},
            { title: 'PRICE ON ADD', field: 'addPrice', sorter:'number',headerFilter:true, width:155},
            { title: 'PRICE NOW', field: 'master.price', sorter:'number',headerFilter:true, width:130},
            { title: 'TARGET-1', field: 'exit1', headerFilter: true, width: 115 },
            { title: 'TARGET-2', field: 'exit2', headerFilter: true, width: 115 },
            { title: 'TARGET-DATE', field:'targetDate', headerFilter:true, width:150},
            { title: 'TODAY\'S CHG', field: 'master.change', sorter:'number',headerFilter:true, width:145},
            { title: 'LOW', field: 'l', sorter:'number',headerFilter:true, width:80},            
            { title: 'HIGH', field: 'h', sorter:'number',headerFilter:true, width:80},            
            { title: 'ENTRY', field: 'entry', sorter:'number',headerFilter:true, width:90},
            { title: 'STOP', field: 'stop', headerFilter: true, width: 85 },                                    
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
    table.download('xlsx', 'PICKS.xlsx', { sheetName: 'PICKS' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

getPicks();

function getPicks() {
    $.ajax({
        type: 'POST',
        url: DATA_URL+ticker,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            table.replaceData(response);
            table.redraw(true);
            let pending =0;
            let hitt1 = 0;
            let hitt2 = 0;
            let missed = 0;
            let stopped = 0;
            let stophitt1 = 0;
            let stophitt2 = 0;
            let delayhitt1 = 0;
            let delayhitt2 = 0;
            for(let i=0; i<response.length; i++)
            {
				if(response[i].result === "PENDING")
					pending++;
				if(response[i].result === "HIT-T1")
					hitt1++;
				if(response[i].result === "HIT-T2")
					hitt2++;
				if(response[i].result === "STOPPED")
					stopped++;
				if(response[i].result === "MISSED")
					missed++;
				if(response[i].result === "STOP-HIT-T1")
					stophitt1++;
				if(response[i].result === "STOP-HIT-T2")
					stophitt2++;				
				if(response[i].result === "DELAY-HIT-T1")
					delayhitt1++;
				if(response[i].result === "DELAY-HIT-T2")
					delayhitt2++;
			}
			const hit = hitt1 + hitt2 + stophitt1 + stophitt2 + delayhitt1 + delayhitt2;
			const miss = stopped + missed;
			const total = pending + hit + miss;
			document.querySelector('#hit').innerText = 'HIT:('+hit+') '+Math.round((hit/(total)*100).toFixed(2))+'%';
			document.querySelector('#miss').innerText = 'MISS:('+miss+') '+Math.round((miss/(total)*100).toFixed(2))+'%';
			document.querySelector('#pending').innerText = 'PENDING:('+pending+') '+Math.round((pending/(total)*100).toFixed(2))+'%';
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function filterRows(key)
{	
	if(key === "pending")	
		table.setFilter("result", "like", "PENDING");	
	else if(key === "miss")
		table.setFilter("result", "in", ["MISSED", "STOPPED"]);
	else if(key === "hit")
		table.setFilter("result", "in", ["HIT-T1", "HIT-T2", "STOP-HIT-T1", "STOP-HIT-T2", "DELAY-HIT-T1", "DELAY-HIT-T2"]);
}

function openHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};
function openPattern(key) {    	
    window.open("pattern/getPatternHistory?ticker="+key, "_blank");
};