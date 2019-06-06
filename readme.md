### pushletExtend

  是基于pushlet的扩展，支持向指定的session发送消息。扩展支持集群等



该或者代码需要与业务系统结合，业务系统可以获取指定用户的所有HttpSession。向指定用户的发送消息。

大概流程：

1、获取用户的id。得到用户的所有httpSession（该session比如在登陆的时候存入了某个map中，需要业务系统的配合）

2、调用Dispatcher.getInstance().unicast(anEvent, pSessionId);向指定的session发送消息。

​    Dispatcher.getInstance().multicast(anEvent);向所有的客户端发送消息

使用扩展：

1、可以支持账号在其他地方登录，向之前的账号的session发送消息提示（您的账号在别处登录...）



相关修改：

1、ajax-pushlet-client.js

```javascript
var PL = {
	jsessionid:'',
	
	
_doRequest: function(anEvent, aQuery) {
    
    // Construct base URL for GET
	var url = PL.pushletURL + '?p_event=' + anEvent;

	url = url + '&jsessionid=' + PL.jsessionid; 
```



2、Pushlet.java

```java
doRequest() 方法中
      Session session = null;
			if (eventType.startsWith(Protocol.E_JOIN)) {
				// Join request: create new subscriber
				session = SessionManager.getInstance().createSession(anEvent);

				String userAgent = request.getHeader("User-Agent");
				if (userAgent != null) {
					userAgent = userAgent.toLowerCase();
				} else {
					userAgent = "unknown";
				}
				session.setUserAgent(userAgent);
				String jsessionid = anEvent.getField("jsessionid");
				if (jsessionid != null && !"".equals(jsessionid)) {
				    UserSessionManager.getInstance().registerUserSession(jsessionid, session.getId());
				}

			} 
```

3、SessionManager

该类中，更换了sessions的容器为concurrentHashMap，去掉某些类中的加锁。

使用session容器，都使用getSessionsContainer方法来获取。支持扩展存放缓存。


```java
private Map<String, Session> sessions = null;
private Map<String, Session> getSessionsContainer() {

	//可以放入redis 或 ehcache..等缓存中，支持集群等...
	
	//直接放在内存中
	if (sessions == null) {
		sessions = new ConcurrentHashMap<String, Session>();
	}
	return sessions;

}
```
4、新添加UserSessionManager

该类的作用，是把每个HttpSession的id对应的pushlet的session.id集合。以便于获取到指定的HttpSession.id向该对象发送消息。

```java
  private Map<String, List<String>> continer;

	private Map<String, List<String>> getSessionContiner() {
		//可以放入redis 或 ehcache..等缓存中，支持集群等...
		
		//直接放在内存中
		if (continer == null) {
			continer = new ConcurrentHashMap<String, List<String>>();
		}
		return continer;

	}
```

5、其他类中，部分代码的优化，更换线程安全的容器，添加泛型等。
