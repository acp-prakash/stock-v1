const TABLE_CONTAINER_ID = 'TRACK';
const DOWNLOAD_BUTTON_ID = 'download_TRACK';
const CLEAR_BUTTON_ID = 'clear_TRACK';
const DATA_URL = 'stock/getMyTrackList';
var ticker;
let TRACKTable;

function createTRACKTable() {
    TRACKTable = new Tabulator(`#${TABLE_CONTAINER_ID}`, {
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
            { column: 'change', dir: 'desc' }
        ],
        columns: [
			{title:"", field:"ticker", width:57, headerSort:false, frozen:true,
				formatter: function () {
                    return "<a href=\"#\" >Earnings</a>";
                },			
				cellClick:function(e, cell)
				{
					openEarnings(cell.getRow().getData().ticker);
				},
			},
			{title:"", field:"ticker", width:50, headerSort:false, frozen:true,
				formatter: function () {
                    return "<a href=\"#\" >Pattern</a>";
                },			
				cellClick:function(e, cell)
				{
					openPattern(cell.getRow().getData().ticker);
				},
			},
			/*{title:"", field:"ticker", width:50, headerSort:false, frozen:true,
				formatter: function () {
                    return "<a href=\"#\" >Option</a>";
                },			
				cellClick:function(e, cell)
				{
					openOption(cell.getRow().getData().ticker);
				},
			},*/
            { title: 'TICK', field: 'ticker', headerFilter: true, width: 60, frozen: true,
            	formatter: function (cell, formatterParams) {
                    return "<a href=\"#\" >"+cell.getRow().getData().ticker+"</a>";
                },
            	cellClick:function(e, cell)
				{
					openHistory(cell.getRow().getData().ticker);
				},
			 },
            /*{ title: 'NAME', field: 'name', headerFilter: true, width: 140, frozen: true },*/
            { title: 'CHG', field: 'change', sorter:'number',headerFilter:true, frozen: true, width:60},
            { title: 'PRICE', field: 'price', sorter:'number',headerFilter:"number", headerFilterFunc:"<=", frozen: true, width:70},
            { title: 'UP-HI', field: 'upHigh', sorter:'number',headerFilter:true, width:68},            			
			{ title: 'DON-LO', field: 'downLow', sorter:'number',headerFilter:true, width:84},
			{ title: 'UP', field: 'upDays', headerFilter:true,width:53},
			{ title: 'U-$', field: 'upBy', sorter:'number',headerFilter:true, width:55},
			{ title: 'DOWN', field: 'downDays', headerFilter:true,width:75},
			{ title: 'D-$', field: 'downBy', sorter:'number',headerFilter:true, width:55},			
			{ title: 'A', field:'pattern.count', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:43},
            { title: 'P', field:'pattern.bull', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:43},
            { title: 'N', field:'pattern.bear', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:44},
			{ title: 'EARN', field: 'daysToEarnings', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:70},
			{ title: 'BT', field:'buyStrength', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:49},
            { title: 'ST', field:'sellStrength', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:48},
            { title: 'B', field:'buyTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
            { title: 'S', field:'sellTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
			/*{ title: 'LAST EAR', field: 'lastEarningsDate', headerFilter:true, width:90},
			{ title: 'NEXT EAR', field: 'nextEarningsDate', headerFilter:true, width:92},
			{ title: 'Earnings', field: 'earningsCount', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'Earning%', field: 'earningsSuccess', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:88},*/
			/*{ title: 'BT-ALY', field: 'rating.btAnalysts', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:76},
			{ title: 'BT-ANA', field: 'rating.btAnalystRating', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:80},*/
			{ title: 'BT-SHORT', field: 'rating.btShortRating', sorter:'string',headerFilter:true, width:93},
			{ title: 'BT-LONG', field: 'rating.btLongRating', sorter:'string',headerFilter:true, width:88},
			{ title: 'BT-RAT', field: 'rating.btRating', sorter:'string',headerFilter:true, width:98},
			{ title: 'BT-TREND', field: 'rating.btTrend', sorter:'string',headerFilter:true, width:98},			
			{ title: 'Z-RANK', field: 'rating.zacksRank', sorter:'number',headerFilter:"list", headerFilterParams:{valuesLookup:true, clearable:true}, headerFilterFunc:">=", width:80},
			{ title: 'Z-RAT', field: 'rating.zacksRating', sorter:'string',headerFilter:true, width:70},
			{ title: 'SI-SCO', field: 'rating.siusScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:73},
			{ title: 'SI-RAT', field: 'rating.siusRating', sorter:'string',headerFilter:true, width:72},
			{ title: 'SI-DAY', field: 'rating.siusDays', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:75},
			{ title: 'TIP-RAT', field: 'rating.tipRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'TIP-BHS', field: 'rating.tipBuyHoldSell', sorter:'string',headerFilter:true, width:85},
			{ title: 'ST-RAT', field: 'rating.streetRating', sorter:'string',headerFilter:true, width:80},
			{ title: 'ST-SCO', field: 'rating.streetScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:77},						
			{ title: 'ZN-RAT', field: 'rating.zenRating', sorter:'string',headerFilter:true, width:80},			
			{ title: 'ZN-UPD', field: 'rating.zenTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'ZN-SCO', field: 'rating.zenScore', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:80},
			{ title: 'STA-RAT', field: 'rating.stockAnalysisRating', sorter:'string',headerFilter:true, width:85},
			{ title: 'STA-UPD', field: 'rating.stockAnalysisTargetUpDown', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},			
			{ title: 'IOBS-SCO', field: 'rating.investObserveScore', sorter:'string',headerFilter:"number", headerFilterFunc:">=", width:90},
			{ title: 'INV-RAT', field: 'rating.investComRating', sorter:'string',headerFilter:true, width:86},			
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
			{ title: 'P123-AT', field: 'rating.portfolio123Analysts', sorter:'string',headerFilter:true, width:86},						
            {
                title: 'EARNINGS DATE', field: 'earningsDate', sorter: 'string', headerFilter: true, width: 130,
                formatter: function (cell) {
                    const earningsDate = cell.getRow().getData().earningsDate;
                    return getFmtTime(earningsDate);
                }
            },
        ],
    });
}

function openHistory(key) {    	
    window.open("stock/stock-history?ticker="+key, "_blank");
};

function openEarnings(key) {    	
    window.open("earnings?ticker="+key, "_blank");
};

function openPattern(key) {    	
    window.open("pattern/getPatternHistory?ticker="+key, "_blank");
};

function openOption(key) {    	
    window.open("option/getOptionHistory?ticker="+key, "_blank");
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
    TRACKTable.download('xlsx', 'MASTER.xlsx', { sheetName: 'MASTER' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    TRACKTable.clearHeaderFilter();
    TRACKTable.clearFilter();
    clearCheckBoxes();
});


fetchTrackList();

function fetchTrackList() {
    createTRACKTable();
    $.ajax({
        type: 'GET',
        url: DATA_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            TRACKTable.replaceData(response);
            TRACKTable.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}