<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>	
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- https://getbootstrap.com/ -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
	integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
	crossorigin="anonymous"></script>
<title>웹툰 관리 앱</title>

	<style>
		.small-image {
		width: 720px;
		height: 1200px;
		}
	</style>
</head>


<body>
	<div class="container w-75 mt-5 mx-auto">
		<h2>${webtoon.title}</h2>
		<hr>
		<div class="card w-75 mx-auto">
			<c:forEach var="image" items="${webtoon.images}">
				<img class="card-img-top small-image" src="../img/${webtoon.title}/${image}">
			</c:forEach>
		</div>

		<c:if test="${error != null }">
			<div>
				에러발생: ${error}
				<button type="button" class="btn-close"></button>
			</div>			
	</div>
	</c:if>	
	<hr>
	<a href="javascript:history.back()"><< Back </a>
	</div>
</body>
</html>