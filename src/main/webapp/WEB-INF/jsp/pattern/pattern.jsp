<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>PATTERN</title>
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
		<h2 style="text-align: left;">PATTERN(s)</h2>
		<a id="clear_PATTERN" href="#" style="font-size: larger;">Clear Filters</a>
		<a id="download_PATTERN" href="#" style="font-size: larger; margin-left: 15px;">Download (XLSX)</a>
		<input type="text" id="ticker" style="font-size: larger; width: 75px;">		
		<a id="SHOW" href="#" style="font-size: larger; margin-left: 15px;">SHOW</a>
		<br>
		<div id="PATTERN" style="width: 90.6%; height: 60%; max-height: 80%; font: small-caption;"></div>
		<script type="text/javascript">
			var ticker='${TICKER}';		 
 		</script>
	
		<script src="/stock-v1/static/js/pattern.js"></script>
	</body>

</html>