const TABLE_CONTAINER_ID = 'STOCK-HISTORY';
const DOWNLOAD_BUTTON_ID = 'download_STOCK-HISTORY';
const CLEAR_BUTTON_ID = 'clear_STOCK-HISTORY';
const DATA_URL = 'history/';
var ticker;
let STOCKHISTORYTable;

function createSTOCKHISTORYTable() {
    STOCKHISTORYTable = new Tabulator(`#${TABLE_CONTAINER_ID}`, {
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
            { column: 'date', dir: 'desc' }
        ],
        columns: [
            { title: 'DATE', field: 'date', headerFilter: true, width: 90, frozen: true },
            { title: 'PRICE', field: 'price', headerFilter: true, width: 70, frozen: true },
            { title: 'Y-PRICE', field: 'prevPrice', headerFilter:true,width:80},
            { title: 'N-PRICE', field: 'nextPrice', headerFilter:true,width:83},
            { title: 'CHG', field: 'change', sorter:'string',headerFilter:true, width:70},
            { title: 'OPEN', field: 'open', headerFilter:true, width:75},
			{ title: 'HIGH', field: 'high', headerFilter:true, width:75},
			{ title: 'LOW', field: 'low', headerFilter:true,width:75},						
			{ title: 'VOLUME', field: 'volume', sorter:'string',headerFilter:true, width:85},			
			{ title: 'EARNING', field: 'earningDay', sorter:'string',headerFilter:true,width:88},
			{ title: 'CHG5', field: 'priceChg5', sorter:'string',headerFilter:true, width:66},
			{ title: 'CHG10', field: 'priceChg10', sorter:'string',headerFilter:true, width:74},						
			{ title: 'BT-ALY', field: 'rating.btAnalysts', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:76},
			{ title: 'BT-ANA', field: 'rating.btAnalystRating', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:80},
			{ title: 'BT-SHORT', field: 'rating.btShortRating', sorter:'string',headerFilter:true, width:93},
			{ title: 'BT-LONG', field: 'rating.btLongRating', sorter:'string',headerFilter:true, width:88},
			{ title: 'BT-RAT', field: 'rating.btRating', sorter:'string',headerFilter:true, width:98},
			{ title: 'BT-TREND', field: 'rating.btTrend', sorter:'string',headerFilter:true, width:98},
			//{ title: 'ME-RAT', field: 'rating.marketEdgeRating', sorter:'string',headerFilter:true, width:118},
			//{ title: 'ME-CS', field: 'rating.marketEdgeConfScore', sorter:'string',headerFilter:true, width:73},
			{ title: 'Z-RANK', field: 'rating.zacksRank', sorter:'number',headerFilter:"list", headerFilterParams:{valuesLookup:true, clearable:true}, headerFilterFunc:">=", width:80},
			{ title: 'Z-RAT', field: 'rating.zacksRating', sorter:'string',headerFilter:true, width:70},
			{ title: 'SI-SCO', field: 'rating.siusScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:73},
			{ title: 'SI-RAT', field: 'rating.siusRating', sorter:'string',headerFilter:true, width:72},
			{ title: 'SI-DAYS', field: 'rating.siusDays', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:81},
			//{ title: 'NQ-RAT', field: 'rating.nasdaqRating', sorter:'string',headerFilter:true, width:85},
			{ title: 'TIP-RAT', field: 'rating.tipRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'TIP-BHS', field: 'rating.tipBuyHoldSell', sorter:'string',headerFilter:true, width:85},
			{ title: 'ST-RAT', field: 'rating.streetRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'ST-SCO', field: 'rating.streetScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:77},
			//{ title: 'MB-RAT', field: 'rating.markbeatRating', sorter:'string',headerFilter:true, width:85},
			//{ title: 'MB-PT', field: 'rating.markbeatPT', sorter:'number',headerFilter:true, width:76},
			//{ title: 'MB-UPD', field: 'rating.markbeatUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:112},			
			{ title: 'ZN-RAT', field: 'rating.zenRating', sorter:'string',headerFilter:true, width:80},
			//{ title: 'ZN-PT', field: 'rating.zenTarget', sorter:'string',headerFilter:true, width:70},
			{ title: 'ZN-UPD', field: 'rating.zenTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'ZN-SCO', field: 'rating.zenScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:80},
			{ title: 'STA-RAT', field: 'rating.stockAnalysisRating', sorter:'string',headerFilter:true, width:85},
			//{ title: 'STA-PT', field: 'rating.stockAnalysisTarget', sorter:'number',headerFilter:true, width:76},
			{ title: 'STA-UPD', field: 'rating.stockAnalysisTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},			
			{ title: 'IOBS-SCO', field: 'rating.investObserveScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:90},
			//{ title: 'IOBS-LPT', field: 'rating.investObserveLT', sorter:'number',headerFilter:true, width:90},
			//{ title: 'IOBS-HPT', field: 'rating.investObserveHT', sorter:'number',headerFilter:true, width:90},
			{ title: 'INV-RAT', field: 'rating.investComRating', sorter:'string',headerFilter:true, width:86},
			//{ title: 'INV-PT', field: 'rating.investComTarget', sorter:'number',headerFilter:true, width:86},			
			{ title: 'SKAP-Q', field: 'rating.seekAlphaQuantRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'SKAP-W', field: 'rating.seekAlphaWallstreetRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'SKAP-A', field: 'rating.seekAlphaAnalystsRating', sorter:'string',headerFilter:true, width:83},
			
			{ title: 'TV-ANA', field: 'rating.tradingViewAnalystsRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-TECH', field: 'rating.tradingViewTechRating', sorter:'string',headerFilter:true, width:86},
			{ title: 'TV-MA', field: 'rating.tradingViewMARating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-OS', field: 'rating.tradingViewOSRating', sorter:'string',headerFilter:true, width:83},
			{ title: 'TV-BB', field: 'rating.tradingViewBullBearPower', sorter:'number',headerFilter:true, width:83},
			
			{ title: 'TKON-RAT', field: 'rating.tickeronRating', sorter:'string',headerFilter:true, width:95},
			{ title: 'TKON-AT', field: 'rating.tickeronRatingAt', sorter:'string',headerFilter:true, width:89},
			{ title: 'TKON-ON', field: 'rating.tickeronRatingOn', sorter:'string',headerFilter:true, width:91},
			{ title: 'TKON-AI', field: 'rating.tickeronAIRating', sorter:'string',headerFilter:true, width:86},
			{ title: 'TKON-VAL', field: 'rating.tickeronUnderOver', sorter:'number',headerFilter:true, width:95},
			
			{ title: 'P123-RAT', field: 'rating.portfolio123Rating', sorter:'string',headerFilter:true, width:95},
			//{ title: 'P123-HT', field: 'rating.portfolio123HighPT', sorter:'string',headerFilter:true, width:89},
			//{ title: 'P123-LT', field: 'rating.portfolio123LowPT', sorter:'string',headerFilter:true, width:91},
			{ title: 'P123-AT', field: 'rating.portfolio123Analysts', sorter:'string',headerFilter:true, width:86},			
			
			//{ title: 'FS-RAT', field: 'rating.finscreenerRating', sorter:'string',headerFilter:true, width:95},
			//{ title: 'FS-PT', field: 'rating.finscreenerPT', sorter:'string',headerFilter:true, width:89},
			//{ title: 'FS-AT', field: 'rating.finscreenerAnalysts', sorter:'number',headerFilter:true, width:86},
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
        if (time < 10)
            time = '0' + time;
        val[11] = time[0];
        val[12] = time[1];
    }
    return val;
}

document.getElementById(DOWNLOAD_BUTTON_ID).addEventListener('click', () => {
    STOCKHISTORYTable.download('xlsx', 'STOCKHISTORY.xlsx', { sheetName: 'MASTER' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    STOCKHISTORYTable.clearHeaderFilter();
    STOCKHISTORYTable.clearFilter();
});

fetchStockHistory();

function fetchStockHistory() {
    createSTOCKHISTORYTable();
    $.ajax({
        type: 'GET',
        url: DATA_URL+ticker,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            STOCKHISTORYTable.replaceData(response);
            STOCKHISTORYTable.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}