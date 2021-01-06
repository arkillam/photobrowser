<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="k" uri="/WEB-INF/tlds/killam.tld"%>

<html>

<head>
<title>Photo Browser: ${file.descriptiveName}</title>
<k:PhotoBrowserHeader />
</head>

<body>

	<k:PhotoBrowserNavigation />

	<div class="container-fluid">

		<div class="row">
			<div class="col-sm-12">${path}</div>
		</div>

		<div class="row">
			<div class="col-sm-12">
				<img class="img-fluid" src='data:image/jpeg;base64, ${file.content}' />
			</div>
		</div>

	</div>

</body>

</html>