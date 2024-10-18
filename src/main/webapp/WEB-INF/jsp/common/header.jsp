<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>STOCK-V1</title>        
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link rel="stylesheet" href="/stock-v1/static/css/header.css">	
	</head>  
    <body>		 
	 	<nav style="text-align: -webkit-center;margin: 5px">
	        <ul class="menu">
	            <li><a href="/stock-v1/WatchList">WATCHLIST</a></li>
	            <li><a href="/stock-v1/Monitor">MONITOR</a></li>
	            <!-- <li><a href="/stock-v1/picks_user">PICKS</a></li> -->
	            <%-- <c:if test="${USER ne 'ACP'}"> --%>
		            <li><a href="/stock-v1/master">MASTER</a></li>
		            <li><a href="/stock-v1/pattern/getPatternHistory">PATTERNS</a></li>
		            <!-- <li><a href="/stock-v1/futures">FUTURES</a></li> -->
		            <li><a href="/stock-v1/options">OPTIONS</a></li>
	                <li><a href="/stock-v1/earnings">EARNINGS</a></li>	                
	                <!-- <li><a href="/stock-v1/track">TRACK</a></li>
	                <li><a href="/stock-v1/picks">CUST</a></li> -->
                <%-- </c:if> --%>
	        </ul>
	    </nav>   
    </body>
</html>