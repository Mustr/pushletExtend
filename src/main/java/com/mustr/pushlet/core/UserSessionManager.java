package com.mustr.pushlet.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class UserSessionManager {

    private static UserSessionManager instance = new UserSessionManager();

    private Map<String, List<String>> continer;

	private Map<String, List<String>> getSessionContiner() {
		//可以放入redis 或 ehcache..等缓存中，支持集群等...
		
		//直接放在内存中
		if (continer == null) {
			continer = new ConcurrentHashMap<String, List<String>>();
		}
		return continer;

	}

    public static UserSessionManager getInstance() {
        return instance;
    }
    
    public void registerUserSession(String jsessionid, String sessionId) {
        if (jsessionid == null || "".equals(jsessionid) || sessionId == null || "".equals(sessionId)) {
            return;
        }
        List<String> list = getSessionContiner().get(jsessionid);
        if (list == null) {
            list = new Vector<String>();
            list.add(sessionId);
            getSessionContiner().put(jsessionid, list);
        } else {
            list.add(sessionId);
        }
        
        //如果是放入第三方缓存，这里需要重新放入缓存
    }
    
    public List<String> getUserSessionIds(String jsessionid) {
        List<String> list = getSessionContiner().get(jsessionid);
        if (list != null) {
            return new ArrayList<String>(list);
        }
        return Collections.emptyList();
    }
    
    public synchronized void clearUserSessionId(String sessionId) {
        Map<String, List<String>> sessionContiner = getSessionContiner();
        Set<Entry<String, List<String>>> entrySet = sessionContiner.entrySet();
        List<String> value = null;
        for (Entry<String, List<String>> entry : entrySet) {
            value = entry.getValue();
            if (value.contains(sessionId)) {
                value.remove(sessionId);
                if (value.size() == 0) {
                    sessionContiner.remove(entry.getKey());
                }
                break;
            }
        }
      //如果是放入第三方缓存，这里需要重新放入缓存
    }
}
