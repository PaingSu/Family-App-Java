<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
	
	<!-- Css  -->
	<spring:url value="/resources/css/style.css" var="styleCss" />
	<spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
	
	<!-- JS  -->
	<spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs" />
	
	<!-- Images  -->
	<spring:url value="/resources/images/header_bar_bg.png" var="headerImage" />
	<spring:url value="/resources/images/successful_signup_icon.png" var="successImage" />
	<spring:url value="/resources/images/footer.png" var="footerImage" />
	<link href="${styleCss}" rel="stylesheet" />
	<link href="${bootstrapCss}" rel="stylesheet" />
    <script src="${bootstrapJs}"></script>
</head>
<body>
	 <div>     
    	<img class="header_img img-responsive" src="${headerImage}" alt="">   	
    </div>
	<div class="col-sm-12">
		<div class="col-sm-4"></div>
		<div class="col-sm-4 center_box">
			<h2 class="second_title">Successful!</h2>
			<img class="img" src="${successImage}" alt="">
			<p class="text">${message}</p>
			<!-- <a href="#" class="back_to_home">Back to home</a> -->
		</div>
		<div class="col-sm-4"></div>
	</div>
<footer>
    <div class="footer_img">
        <img src="${footerImage}" class="img-responsive" width="100%" alt="Footer Image" />
    </div>
</footer>
</body>
</html>