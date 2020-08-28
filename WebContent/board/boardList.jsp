<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>게시판</title>
</head>
<body>

	<table width="500" align="center">
		<tr>
			<td align="right" style="font-family: Gulim; font-size: 12px;">총&nbsp;${count}건  ${currentPage}페이지</td>
		</tr>
	</table>
	<table width="500" border="1" cellpadding="0" cellspacing="0" align="center">
			<tr>
				<th style="font-family: Gulim; font-size: 12px;">번호</th>
				<th style="font-family: Gulim; font-size: 12px;">제목</th>
				<th style="font-family: Gulim; font-size: 12px;">글쓴이</th>
				<th style="font-family: Gulim; font-size: 12px;">날짜</th>
				<th style="font-family: Gulim; font-size: 12px;">조회</th>
			</tr>
		<tbody>
			<c:forEach var="board" items="${list}">
				<tr>
					<td align="center" width="50" style="font-size: 13px;">${number}<c:set var="number" value="${number-1}" />
					</td>
					<td width="210" style="font-family: Gulim; font-size: 12px;"><a href="****.do?seq=${board.seq}" style="text-decoration: none;">&nbsp;${board.title}</a></td>
					<td align="center" width="70" style="font-family: Gulim; font-size: 12px;">${board.name}</td>
					<td width="120" style="font-family: Gulim; font-size: 12px;" align="center">
					    <fmt:formatDate	value="${board.regdate}" pattern="yyyy/MM/dd HH:mm" /></td>
					<td width="50" align="center" style="font-family: Gulim; font-size: 12px;">${board.hit}</td>
				</tr>
			</c:forEach>
			<c:if test="${count==0}">
				<tr>
					<td colspan="5" align="center" style="font-family: Gulim; font-size: 12px;">게시물이 없습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>

	<form action="list.do" name="search" method="post" onsubmit="return seachCheck()">
		<table width="500" align="center">
			<tr>
				<td align="center" width="90%"><select name="keyField">
						<option value="title">제목</option>
						<option value="name">이름</option>
						<option value="content">내용</option>
						<option value="all">전체</option>
				</select> 
				<input type="text" size="16" name="keyWord">
				<input type="submit" value="검색" style="font-family: Gulim; font-size: 12px;">
				</td>
				<td align="right"><input type="button" value="글쓰기" onclick="location.href='write.do'" style="font-family: Gulim; font-size: 12px;"></td>
			</tr>
		</table>
	</form>
	<table align="center">
		<c:if test="${count>0}">
			<tr>
				<td align="center">${pagingHtml}</td>
			</tr>
		</c:if>
	</table>
</body>
</html>