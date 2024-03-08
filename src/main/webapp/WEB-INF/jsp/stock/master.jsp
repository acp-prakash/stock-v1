<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>MASTER</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<script src="https://cdn.polyfill.io/v2/polyfill.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
		<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.css" />                       
		<link href="https://unpkg.com/tabulator-tables@5.5/dist/css/tabulator.min.css" rel="stylesheet">		
 		<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.5/dist/js/tabulator.min.js"></script>
 		<script type="text/javascript" src="https://oss.sheetjs.com/sheetjs/xlsx.full.min.js"></script>
    </head>
	<body>
		<%@ include file="../common/header.jsp" %>
		<h2 style="text-align: left;">MASTER TRACKER</h2>
		<div>
			<label><b>ALL</b></label><input type="checkbox" onclick="AllClick(this);" id="ALL" name="ALL">
			<label><b>Barchart</b></label><input type="checkbox" onclick="BarchartClick(this);" id="Barchart" name="Barchart">
			<label><b>StockInvest</b></label><input type="checkbox" onclick="StockInvestClick(this);" id="StockInvest" name="StockInvest">
			<label><b>TipRank</b></label><input type="checkbox" onclick="TipRankClick(this);" id="TipRank" name="TipRank">
			<label><b>Street</b></label><input type="checkbox" onclick="StreetClick(this);" id="Street" name="Street">
			<!-- <label><b>Marketbeat</b></label><input type="checkbox" onclick="MarketbeatClick(this);" id="Marketbeat" name="Marketbeat"> -->
			<label><b>WallstreetZen</b></label><input type="checkbox" onclick="ZenClick(this);" id="Zen" name="Zen">
			<label><b>StcokAnalysis</b></label><input type="checkbox" onclick="StcokAnalysisClick(this);" id="StcokAnalysis" name="StcokAnalysis">
			<label><b>Investing.com</b></label><input type="checkbox" onclick="InvestingClick(this);" id="Investing" name="Investing">
			<label><b>SeekingAlpha</b></label><input type="checkbox" onclick="SeekingAlphaClick(this);" id="SeekingAlpha" name="SeekingAlpha">										
			<label><b>TradingView</b></label><input type="checkbox" onclick="TradingViewClick(this);" id="TradingView" name="TradingView">
			<label><b>Tickeron</b></label><input type="checkbox" onclick="TickeronClick(this);" id="Tickeron" name="Tickeron">
		</div>
		<a id="clear_MASTER" href="#" style="font-size: larger;">Clear Filters</a>
		<a id="download_MASTER" href="#" style="font-size: larger; margin-left: 15px;">Download (XLSX)</a>
		<br>
		<div id="MASTER" style="width: 95.6%; height: 80%; max-height: 80%; font: small-caption;"></div>
	
		<script src="/stock-v1/static/js/master.js"></script>
	</body>

</html>