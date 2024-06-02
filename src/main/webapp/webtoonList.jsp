<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- https://getbootstrap.com/ -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" 
		integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" 
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<title>웹툰 관리 앱</title>
</head>
<body>

<div class="container w-75 mt-5 mx-auto">
<h2>웹툰 목록</h2>
<hr>

 <ul class="list-group">
 <!-- <li><a href="">[번호] 뉴스 제목, 등록일</a><a href="">삭제 버튼</a></li> -->
	<c:forEach var="webtoon"  items="${webtoonlist}" varStatus="status">
		
		<li class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" >
			<img src="../img/${webtoon.title}/${webtoon.thumbnail}" alt="썸네일" width="500">
			<a href="webtoon.nhn?action=getWebtoon&aid=${webtoon.aid}" class="text-decoration-none">
				[ ${status.count} ] ${webtoon.title}, ${webtoon.date}
				
			</a>
			<a href="webtoon.nhn?action=deleteWebtoon&aid=${webtoon.aid}">
				<span class="badge bg-secondary">&times;</span>
			</a>
		</li>
	</c:forEach>
</ul>

<hr>

<c:if test="${error != null }" >
	<div>
		에러발생: ${error}
		<button type="button" class="btn-close"></button>
	</div>
</c:if>

  <button type="button" class="btn btn-outline-info mb-3" data-bs-toggle="collapse" 
		data-bs-target="#addForm" aria-expanded="false" aria-control="addForm">웹툰 등록</button>

  <div class="collapse" id="addForm">
  	<div class="card card-body">
		<form method="post" action="webtoon.nhn?action=addWebtoon" enctype="multipart/form-data">
					
			<label class="form-label">제목 <input name="title" class="form-control" ></label>
			<label class="form-label">웹툰 썸네일 <input type="file" name="thumbnail" class="form-control"></label>  	
			<label class="form-label">웹툰 파일<input type="file" name="fileUpload" class="form-control" multiple="multiple"></label>

			<button type="submit" class="btn btn-success mt-3">저장</button>  		
		</form>  	
  	</div>
  </div>

</div>

</body>
</html>
