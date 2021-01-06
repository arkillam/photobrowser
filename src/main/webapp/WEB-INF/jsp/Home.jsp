<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="k" uri="/WEB-INF/tlds/killam.tld"%>

<html>

<head>
<title>Photo Browser</title>
<k:PhotoBrowserHeader />
</head>

<body>

	<k:PhotoBrowserNavigation />

	<div class="container-fluid">

		<div class="row">
			<div class="col-sm-12">
				<p>Current Directory: ${dir}</p>
			</div>
		</div>

		<div class="row">
			<c:forEach var="x" items="${files}">
				<div class="col-sm">
					<c:choose>
						<c:when test="${x.dir}">
							Open Directory: <a href="/?dir=${x.encodedPath}">${x.descriptiveName}</a>
						</c:when>
						<c:otherwise>
							<a href="/single?path=${x.encodedPath}"><img
								style='display: block; width: 100px; height: 100px;'
								id='base64image'
								src='data:image/${x.filetype};base64, ${x.thumbnail}' /></a>
						</c:otherwise>
					</c:choose>

				</div>
			</c:forEach>
		</div>

	</div>

</body>

</html>