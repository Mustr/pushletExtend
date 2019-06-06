<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax-pushlet-client.js"></script>
<script type="text/javascript">  
  PL.webRoot = '${pageContext.request.contextPath}/';
  PL._init();   
  PL.jsessionid = '${pageContext.session.id}';
  PL.joinListen('/cuige/he');  
  function onData(event) {   
      console.log(event.get("mess"));   
      // 离开  
      // PL.leave();  
  }
</script>
</head>
<body>
<h3>test</h3>
</body>
</html>