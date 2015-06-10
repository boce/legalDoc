package com.cdrundle.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

/**
 * 最核心的地方，就是提供某个资源对应的权限定义，即getAttributes方法返回的结果。 此类在初始化时，应该取到所有资源及其对应角色的定义。
 * 
 */
@Service
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource,IMySecurityMetadataSource {

	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;
	private JdbcTemplate jdbcTemplate;

	public MySecurityMetadataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		loadResourceDefine();
	}

	private void loadResourceDefine() {
		// 查找到所有权限
		List<String> query = loadAuthorityByQuery();
		/*
		 * 应当是资源为key， 权限为value。 资源通常为url， 权限就是那些以ROLE_为前缀的角色。 一个资源可以由多个权限来访问。
		 */
		resourceMap = new HashMap<String, Collection<ConfigAttribute>>();

		for (String auth : query) {
			/*
			 * //获取authority的authorityName String
			 * auth=authority.getAuthorityName();
			 */
			ConfigAttribute ca = new SecurityConfig(auth);
			// 获取authority的resources
			List<String> resourceses = loadResourcesByQuery(auth);
			for (String res : resourceses) {
				// 获取res的url
				String url = res;
				/*
				 * 判断资源文件和权限的对应关系，如果已经存在相关的资源url，则要通过该url为key提取出权限集合，将权限增加到权限集合中。
				 */
				if (resourceMap.containsKey(url)) {
					Collection<ConfigAttribute> value = resourceMap.get(url);
					value.add(ca);
					resourceMap.put(url, value);
				} else {
					Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
					atts.add(ca);
					resourceMap.put(url, atts);
				}
			}

		}
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		// object 是一个URL，被用户请求的url。
		String url = ((FilterInvocation) object).getRequestUrl();

		int firstQuestionMarkIndex = url.indexOf("?");

		if (firstQuestionMarkIndex != -1) {
			url = url.substring(0, firstQuestionMarkIndex);
		}
		Iterator<String> ite = resourceMap.keySet().iterator();

		while (ite.hasNext()) {
			String resURL = ite.next();
			if (resURL.equals(url)) {
				return resourceMap.get(resURL);
			}
		}

		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	public List<String> loadAuthorityByQuery() {
		String sql = "select name from sysmgt_role where is_used=true";
		List<String> strLst = this.jdbcTemplate.queryForList(sql, String.class);
		return strLst;
	}

	public List<String> loadResourcesByQuery(String authName) {
		String sql = "select DISTINCT sp.url from sysmgt_entitlement se"
					+" left join sysmgt_role sr on se.role=sr.id"
					+" inner join sysmgt_page sp on se.page=sp.id"
					+" where sr.is_used=true and sr.name=?"
					+" union all"
					+" select DISTINCT spu.url from sysmgt_entitlement se"
					+" left join sysmgt_role sr on se.role=sr.id"
					+" inner join sysmgt_pagesource sp on se.page_source=sp.id"
					+" inner join sysmgt_pagesource_url spu on sp.id=spu.pagesource_id"
					+" where sr.is_used=true and sr.name=?";
		List<String> strLst = this.jdbcTemplate.queryForList(sql, String.class, authName, authName);
		return strLst;
	}

	@Override
	public void reloadResourceDefine() {
		loadResourceDefine();
	}
}
