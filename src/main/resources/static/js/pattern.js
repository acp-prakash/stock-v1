// Define your constants
const TABLE_CONTAINER_ID = 'PATTERN';
const DOWNLOAD_BUTTON_ID = 'download_PATTERN';
const CLEAR_BUTTON_ID = 'clear_PATTERN';
const SHOW_BUTTON_ID = 'SHOW';
const DATA_URL = 'getPatternHistory?ticker=';
const SHOW_DATA_URL = 'showPatterns?ticker=';
var ticker;
const table = createPATTERNTable();

function createPATTERNTable() {
    return new Tabulator(`#${TABLE_CONTAINER_ID}`, {
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
        initialSort:[
	        {column:"histDate", dir:"desc"}, //then sort by this second	        
	        {column:"ticker", dir:"desc"}, //sort by this first	       
	        {column:"bull", dir:"desc"}, //then sort by this second 
	        
	    ],
        columns: [
            { title: 'CHG', field: 'all.change', sorter:'number',headerFilter:true, frozen: true, width:60},
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 63, frozen: true },            
            { title: 'PRICE', field: 'all.price', sorter:'number',headerFilter:"number", headerFilterFunc: function(headerValue, rowValue) {
                return parseFloat(rowValue) <= parseFloat(headerValue);
            }, width:70},
            { title: 'MN-PT', field:'minPT', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:75},
			{ title: 'MX-PT', field:'maxPT', headerFilter:"number", headerFilterFunc:">=", width:73},            
            { title: 'TGT-DT', field:'targetDate', headerFilter:true, width:80},            
            { title: 'TREND', field: 'trend', headerFilter: true, width: 75 },
            //{ title: 'STATUS', field:'status', headerFilter:true, width:78},            
            { title: 'AL', field:'count', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:50},
            { title: 'BL', field:'bull', sorter:'number',headerFilter:"number", headerFilterFunc:"<", width:50},
            { title: 'BR', field:'bear', sorter:'number',headerFilter:"number", headerFilterFunc:"<", width:50},                        
			{ title: 'ENTRY', field:'entry', headerFilter:"number", headerFilterFunc:">=", width:75},			
			{ title: 'STOP', field:'stop', headerFilter:"number", headerFilterFunc:">=", width:65},
			{ title: 'PT%', field:'fromPtPc', headerFilter:true, width:60},		
			{ title: 'DATE', field: 'histDate', headerFilter: true, width: 80 },
            { title: 'NAME', field: 'name', headerFilter: true, width: 110 },	
			{ title: 'GS', field:'all.gCShortDays', headerFilter:true, width:75},
            { title: 'GL', field:'all.gCLongDays',  headerFilter:true, width:75},
            { title: 'DS', field:'all.dCShortDays', headerFilter:true, width:75},
            { title: 'DL', field:'all.dCLongDays',  headerFilter:true, width:75},
			{ title: 'UP', field: 'all.upDays', headerFilter:true,width:53},
			{ title: 'UP $', field: 'all.upBy', sorter:'number',headerFilter:true, width:65},
			{ title: 'DOWN', field: 'all.downDays', headerFilter:true,width:75},
			{ title: 'DOWN $', field: 'all.downBy', sorter:'number',headerFilter:true, width:84},
			{ title: 'B', field:'all.buyTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
            { title: 'S', field:'all.sellTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
			
            //{ title: 'MB-PT', field: 'all.rating.markbeatPT', sorter:'number',headerFilter:true, width:76},
            { title: 'ZN-PT', field: 'all.rating.zenTarget', sorter:'string',headerFilter:true, width:70},
            { title: 'STA-PT', field: 'all.rating.stockAnalysisTarget', sorter:'number',headerFilter:true, width:76},
            { title: 'IOBS-LT', field: 'all.rating.investObserveLT', sorter:'number',headerFilter:true, width:80},
			{ title: 'IOBS-HT', field: 'all.rating.investObserveHT', sorter:'number',headerFilter:true, width:83},
			{ title: 'INV-PT', field: 'all.rating.investComTarget', sorter:'number',headerFilter:true, width:75},
			{ title: 'P123-HT', field: 'all.rating.portfolio123HighPT', sorter:'string',headerFilter:true, width:83},
			{ title: 'P123-LT', field: 'all.rating.portfolio123LowPT', sorter:'string',headerFilter:true, width:80},			
			
			{ title: 'EARN-DAYS', field: 'all.daysToEarnings', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:100},
			{ title: 'LAST EAR', field: 'all.lastEarningsDate', headerFilter:true, width:90},
			{ title: 'NEXT EAR', field: 'all.nextEarningsDate', headerFilter:true, width:92},
			{ title: 'Earnings', field: 'all.earningsCount', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'Earning%', field: 'all.earningsSuccess', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:88},
			{ title: 'BT-ALY', field: 'all.rating.btAnalysts', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:76},
			{ title: 'BT-ANA', field: 'all.rating.btAnalystRating', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:80},
			{ title: 'BT-SHORT', field: 'all.rating.btShortRating', sorter:'string',headerFilter:true, width:93},
			{ title: 'BT-LONG', field: 'all.rating.btLongRating', sorter:'string',headerFilter:true, width:88},
			{ title: 'BT-RAT', field: 'all.rating.btRating', sorter:'string',headerFilter:true, width:98},
			{ title: 'BT-TREND', field: 'all.rating.btTrend', sorter:'string',headerFilter:true, width:98},
			//{ title: 'ME-RAT', field: 'all.rating.marketEdgeRating', sorter:'string',headerFilter:true, width:118},
			//{ title: 'ME-CS', field: 'all.rating.marketEdgeConfScore', sorter:'string',headerFilter:true, width:73},
			{ title: 'Z-RANK', field: 'all.rating.zacksRank', sorter:'number',headerFilter:"list", headerFilterParams:{valuesLookup:true, clearable:true}, headerFilterFunc:">=", width:80},
			{ title: 'Z-RAT', field: 'all.rating.zacksRating', sorter:'string',headerFilter:true, width:70},
			{ title: 'SI-SCO', field: 'all.rating.siusScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:73},
			{ title: 'SI-RAT', field: 'all.rating.siusRating', sorter:'string',headerFilter:true, width:72},
			{ title: 'SI-DAYS', field: 'all.rating.siusDays', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:81},
			//{ title: 'NQ-RAT', field: 'all.rating.nasdaqRating', sorter:'string',headerFilter:true, width:85},
			{ title: 'TIP-RAT', field: 'all.rating.tipRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'TIP-BHS', field: 'all.rating.tipBuyHoldSell', sorter:'string',headerFilter:true, width:85},
			{ title: 'ST-RAT', field: 'all.rating.streetRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'ST-SCO', field: 'all.rating.streetScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:77},
			//{ title: 'MB-RAT', field: 'all.rating.markbeatRating', sorter:'string',headerFilter:true, width:85},
			//{ title: 'MB-PT', field: 'all.rating.markbeatPT', sorter:'number',headerFilter:true, width:76},
			//{ title: 'MB-UPD', field: 'all.rating.markbeatUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:112},			
			{ title: 'ZN-RAT', field: 'all.rating.zenRating', sorter:'string',headerFilter:true, width:80},
			//{ title: 'ZN-PT', field: 'all.rating.zenTarget', sorter:'string',headerFilter:true, width:70},
			{ title: 'ZN-UPD', field: 'all.rating.zenTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'ZN-SCO', field: 'all.rating.zenScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:80},
			{ title: 'STA-RAT', field: 'all.rating.stockAnalysisRating', sorter:'string',headerFilter:true, width:85},
			//{ title: 'STA-PT', field: 'all.rating.stockAnalysisTarget', sorter:'number',headerFilter:true, width:76},
			{ title: 'STA-UPD', field: 'all.rating.stockAnalysisTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},			
			{ title: 'IOBS-SCO', field: 'all.rating.investObserveScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:90},
			//{ title: 'IOBS-LPT', field: 'all.rating.investObserveLT', sorter:'number',headerFilter:true, width:90},
			//{ title: 'IOBS-HPT', field: 'all.rating.investObserveHT', sorter:'number',headerFilter:true, width:90},
			{ title: 'INV-RAT', field: 'all.rating.investComRating', sorter:'string',headerFilter:true, width:86},
			//{ title: 'INV-PT', field: 'all.rating.investComTarget', sorter:'number',headerFilter:true, width:86},			
			{ title: 'SKAP-Q', field: 'all.rating.seekAlphaQuantRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'SKAP-W', field: 'all.rating.seekAlphaWallstreetRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'SKAP-A', field: 'all.rating.seekAlphaAnalystsRating', sorter:'string',headerFilter:true, width:83},
			
			{ title: 'TV-ANA', field: 'all.rating.tradingViewAnalystsRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-TECH', field: 'all.rating.tradingViewTechRating', sorter:'string',headerFilter:true, width:86},
			{ title: 'TV-MA', field: 'all.rating.tradingViewMARating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-OS', field: 'all.rating.tradingViewOSRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-BB', field: 'all.rating.tradingViewBullBearPower', sorter:'number',headerFilter:true, width:83},
			
			{ title: 'TKON-RAT', field: 'all.rating.tickeronRating', sorter:'string',headerFilter:true, width:95},
			{ title: 'TKON-AT', field: 'all.rating.tickeronRatingAt', sorter:'string',headerFilter:true, width:89},
			{ title: 'TKON-ON', field: 'all.rating.tickeronRatingOn', sorter:'string',headerFilter:true, width:91},
			{ title: 'TKON-AI', field: 'all.rating.tickeronAIRating', sorter:'string',headerFilter:true, width:86},
			{ title: 'TKON-VAL', field: 'all.rating.tickeronUnderOver', sorter:'number',headerFilter:true, width:95},
			
			{ title: 'P123-RAT', field: 'all.rating.portfolio123Rating', sorter:'string',headerFilter:true, width:95},
			//{ title: 'P123-HT', field: 'all.rating.portfolio123HighPT', sorter:'string',headerFilter:true, width:89},
			//{ title: 'P123-LT', field: 'all.rating.portfolio123LowPT', sorter:'string',headerFilter:true, width:91},
			{ title: 'P123-AT', field: 'all.rating.portfolio123Analysts', sorter:'string',headerFilter:true, width:86},			
			
			//{ title: 'FS-RAT', field: 'all.rating.finscreenerRating', sorter:'string',headerFilter:true, width:95},
			//{ title: 'FS-PT', field: 'all.rating.finscreenerPT', sorter:'string',headerFilter:true, width:89},
			//{ title: 'FS-AT', field: 'all.rating.finscreenerAnalysts', sorter:'number',headerFilter:true, width:86},
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
    table.download('xlsx', 'PATTERN.xlsx', { sheetName: 'PATTERN' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

getPatternHistory();

function getPatternHistory() {
    $.ajax({
        type: 'POST',
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

document.getElementById(SHOW_BUTTON_ID).addEventListener('click', () => {	
	showPatterns(document.getElementById("ticker").value);	
});

function showPatterns(tick) {

	$.ajax({
        type: 'GET',
        url: SHOW_DATA_URL+tick,
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
};