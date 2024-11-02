const TABLE_CONTAINER_ID = 'MASTER';
const DOWNLOAD_BUTTON_ID = 'download_MASTER';
const CLEAR_BUTTON_ID = 'clear_MASTER';
const DATA_URL = 'stock/masterList';
var ticker;
let MASTERTable;

function createMASTERTable() {
    MASTERTable = new Tabulator(`#${TABLE_CONTAINER_ID}`, {
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
                    return "<a href=\"#\">Earnings</a>";
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
            { title: 'NAME', field: 'name', headerFilter: true, width: 140, frozen: true },
            { title: 'PRICE', field: 'price', sorter:'number',headerFilter:"number", headerFilterFunc:"<=", width:70},
            { title: 'CHG', field: 'change', sorter:'number',headerFilter:true, frozen: true, width:65},
            /*{ title: 'L', field:'l', headerFilter:true, width:57},
			{ title: 'E', field:'e', headerFilter:true, width:57},
			{ title: 'PRICE', field: 'price', sorter:'number',headerFilter:"number", headerFilterFunc:"<=", width:70},
			{ title: 'H', field:'h', headerFilter:true, width:57},
			{ title: 'ED', field:'entryDate', headerFilter:true, width:65},
            
            { title: 'MX-L', field:'maxLoss', headerFilter:true, width:66},
            {title:'PL', field:"profitLoss", headerFilter:true, width:57, formatter:function(cell){
				var result = cell.getRow().getData().profitLoss;
				if(result < 0 ){
					 return "<span style='background-color:black; color:orangered; font-weight:bold; display: grid;font-size: 14px;width:70px'>" + (result *-1) + "</span>";
			    }else{
					 return "<span style='background-color:black; color:yellowgreen; font-weight:bold; display: grid;font-size: 14px;width:70px'>" + cell.getValue() + "</span>";
			    }
			}},
			{ title: 'MX-P', field:'maxProfit', headerFilter:true, width:66},
			
			{ title: 'L-PT', field:'maxLosspt', headerFilter:true, width:66},
            {title:'PT', field:"profitLosspt", headerFilter:true, width:57, formatter:function(cell){
				var result = cell.getRow().getData().profitLosspt;
				if(result < 0 ){
					 return "<span style='background-color:black; color:orangered; font-weight:bold; display: grid;font-size: 14px;width:70px'>" + (result *-1) + "</span>";
			    }else{
					 return "<span style='background-color:black; color:yellowgreen; font-weight:bold; display: grid;font-size: 14px;width:70px'>" + cell.getValue() + "</span>";
			    }
			}},
			{ title: 'P-PT', field:'maxProfitpt', headerFilter:true, width:66},*/
			            
            //{ title: 'TRP', field: 'trackingPrice', sorter:'number',headerFilter:"number", headerFilterFunc:"<=",  width:70},
            //{ title: 'TRD', field: 'trackingDiff', sorter:'number',headerFilter:"number", headerFilterFunc:"<=",  width:70},
            /*{ title: 'UP-HI', field: 'upHigh', sorter:'number',headerFilter:true, width:68},
			{ title: 'DON-LO', field: 'downLow', sorter:'number',headerFilter:true, width:84},*/
            //{ title: 'MB-PT', field: 'rating.markbeatPT', sorter:'number',headerFilter:true, width:76},
            /*{ title: 'ZN-PT', field: 'rating.zenTarget', sorter:'string',headerFilter:true, width:70},
            { title: 'STA-PT', field: 'rating.stockAnalysisTarget', sorter:'number',headerFilter:true, width:76},
            { title: 'IOBS-LT', field: 'rating.investObserveLT', sorter:'number',headerFilter:true, width:80},
			{ title: 'IOBS-HT', field: 'rating.investObserveHT', sorter:'number',headerFilter:true, width:83},
			{ title: 'INV-PT', field: 'rating.investComTarget', sorter:'number',headerFilter:true, width:75},
			{ title: 'P123-HT', field: 'rating.portfolio123HighPT', sorter:'string',headerFilter:true, width:83},
			{ title: 'P123-LT', field: 'rating.portfolio123LowPT', sorter:'string',headerFilter:true, width:80},*/			
			/*{ title: 'UP', field: 'upDays', headerFilter:true,width:53},
			{ title: 'UP $', field: 'upBy', sorter:'number',headerFilter:true, width:65},			
			{ title: 'DOWN', field: 'downDays', headerFilter:true,width:75},
			{ title: 'DOWN $', field: 'downBy', sorter:'number',headerFilter:true, width:84},*/			
			{ title: 'EARN-DAYS', field: 'daysToEarnings', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:103},
			{ title: 'P-STREAK', field:'positiveStreak', sorter:'number',headerFilter:"number", headerFilterFunc:">=",width:95},
			{ title: 'N-STREAK', field:'negativeStreak', sorter:'number',headerFilter:"number", headerFilterFunc:">=",width:95},
			{ title: 'CUR-LAST-DIFF', field:'currAndLastPriceEffectDiff', headerFilter:true,width:125},
			{ title: 'B', field:'buyTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
            { title: 'S', field:'sellTrend', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:45},
            { title: 'GS', field:'gCShortDays', headerFilter:true, width:75},
            { title: 'GL', field:'gCLongDays',  headerFilter:true, width:75},
            { title: 'DS', field:'dCShortDays', headerFilter:true, width:75},
            { title: 'DL', field:'dCLongDays',  headerFilter:true, width:75},
            /*{ title: 'F', field: 'isFuture', sorter:'boolean',headerFilter:true, width:45},*/            
			{ title: 'LAST EAR', field: 'lastEarningsDate', headerFilter:true, width:90},
			{ title: 'NEXT EAR', field: 'nextEarningsDate', headerFilter:true, width:92},
			{ title: 'Earnings', field: 'earningsCount', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:85},
			{ title: 'Earning%', field: 'earningsSuccess', sorter:'number',headerFilter:"number", headerFilterFunc:">=", width:88},
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
    MASTERTable.download('xlsx', 'MASTER.xlsx', { sheetName: 'MASTER' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    MASTERTable.clearHeaderFilter();
    MASTERTable.clearFilter();
    clearCheckBoxes();
});

function clearCheckBoxes()
{
	document.getElementById("Barchart").checked = false;
  	document.getElementById("StockInvest").checked = false;
	document.getElementById("TipRank").checked = false;
	document.getElementById("Street").checked = false;
	document.getElementById("Marketbeat").checked = false;
	document.getElementById("Zen").checked = false;
	document.getElementById("StcokAnalysis").checked = false;
	document.getElementById("Investing").checked = false;
	document.getElementById("SeekingAlpha").checked = false;
	document.getElementById("TradingView").checked = false;
	document.getElementById("Tickeron").checked = false;
}

fetchMasterList();

function fetchMasterList() {
    createMASTERTable();
    $.ajax({
        type: 'GET',
        url: DATA_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            MASTERTable.replaceData(response);
            MASTERTable.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function AllClick(cb) {
	
  if(cb.checked)
  {
	  document.getElementById("Barchart").checked = true;
	  document.getElementById("StockInvest").checked = true;
	  document.getElementById("TipRank").checked = true;
	  document.getElementById("Street").checked = true;
	  document.getElementById("Marketbeat").checked = true;
	  document.getElementById("Zen").checked = true;
	  document.getElementById("StcokAnalysis").checked = true;
	  document.getElementById("Investing").checked = true;
	  document.getElementById("SeekingAlpha").checked = true;
	  document.getElementById("TradingView").checked = true;
	  document.getElementById("Tickeron").checked = true;
	  
	  var cb={};cb.checked=true;
	  BarchartClick(cb);
	  StockInvestClick(cb);
	  TipRankClick(cb);
	  StreetClick(cb);
	  MarketbeatClick(cb);
	  ZenClick(cb);
	  StcokAnalysisClick(cb);
	  InvestingClick(cb);
	  SeekingAlphaClick(cb);
	  TradingViewClick(cb);
	  TickeronClick(cb);
  }
  else
  {
	  clearCheckBoxes();
	  MASTERTable.clearFilter();
  }
}

function BarchartClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.btShortRating", "like", "Buy");
	  MASTERTable.addFilter("rating.btLongRating", "like", "Buy");
	  MASTERTable.addFilter("rating.btRating", "like", "Buy");
	  MASTERTable.addFilter("rating.btTrend", "like", "Buy");
  } 
}
function StockInvestClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.siusRating", "like", "Buy");	  
  }  
}
function TipRankClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.tipRating", "like", "Buy");	  
  }
}
function StreetClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.streetRating", "like", "Buy");	  
  }
}
function MarketbeatClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.markbeatRating", "like", "Buy");
  }
}
function ZenClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.zenRating", "like", "Buy");	  
  }
}
function StcokAnalysisClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.stockAnalysisRating", "like", "Buy");
  }
}
function InvestingClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.investComRating", "like", "Buy");	  
  }
}
function SeekingAlphaClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.seekAlphaQuantRating", "like", "Buy");
	  MASTERTable.addFilter("rating.seekAlphaWallstreetRating", "like", "Buy");
	  MASTERTable.addFilter("rating.seekAlphaAnalystsRating", "like", "Buy");
  }
}
function TradingViewClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.tradingViewAnalystsRating", "like", "Buy");	  
  }
}
function TickeronClick(cb) {
  if(cb.checked)
  {
	  MASTERTable.addFilter("rating.tickeronRating", "like", "Buy");	  
	  MASTERTable.addFilter("rating.tickeronAIRating", "like", "B");
  }
}