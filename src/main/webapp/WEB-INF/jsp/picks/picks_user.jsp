<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>PICKS</title>
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
 		<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.5/dist/js/tabulator.min.js"></script>
 		<script type="text/javascript" src="https://oss.sheetjs.com/sheetjs/xlsx.full.min.js"></script> 		
		<link href="https://unpkg.com/tabulator-tables@5.5/dist/css/tabulator.min.css" rel="stylesheet">		
    </head>
	<body>
		<%@ include file="../common/header.jsp" %>
		<!-- <h2 style="text-align: left;">PICK(s)</h2> -->				
		<a id="hit" href="javascript:filterRows('hit');" style="font-size: 15px;color: black;background-color: greenyellow;font-weight: bolder;">HIT:() %</a>
		<a id="miss" href="javascript:filterRows('miss');" style="font-size: 15px;margin-left: 12px;color: white;background-color: orangered;font-weight: bolder;">MISS:() %</a>
		<a id="pending" href="javascript:filterRows('pending');" style="font-size: 15px;margin-left: 12px;color: black;background-color: yellow;font-weight: bolder;">PENDING:() %</a>
		<a id="clear_PICKS" href="#" style="font-size: small;margin-left: 12px;">Clear Filters</a>
		<a id="download_PICKS" href="#" style="font-size: small; margin-left: 12px;">Download (XLSX)</a>
		<br>
		<div id="PICKS" style="width: 84.3%; height: 63%; max-height: 63%; font: small-caption; font-size:17px;"></div>
		<script type="text/javascript">
			var ticker='${TICKER}';		 
 		</script>
	
		<script src="/stock-v1/static/js/picks_user.js"></script>
	</body>

</html>