package com.cdrundle.legaldoc.unittest;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.service.IRoleService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.MD5Util;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.RoleVo;
import com.cdrundle.legaldoc.vo.UserVo;

/**
 * 
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class UserServiceTest
{
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private IOrganizationDao organizationDao;
	
	@Before
	public void init() throws Exception 
	{
		Organization parentOrganization = organizationDao.findOne(1L);
		Organization organization = new Organization();
		organization.setName("内江");
		organization.setParentOrganization(parentOrganization);
		organization.setIsUsed(true);
		organizationDao.save(organization);
		
		User user = new User();
		user.setName("许报");
		user.setUserName("xubao");
		user.setPassword(MD5Util.getMd5Str("1"));
		user.setOrganization(organization);
		userDao.save(user);
		
		RoleVo role = new RoleVo();
		role.setName("ROLE_TEST");
		roleService.saveOrUpdate(role);
	}
	
	@Test
	public void testFindByUserName()
	{
		userService.findByUserName("xubao");
	}

	@Test
	@Transactional
	public void testFindByName() throws NoSuchAlgorithmException
	{
		Organization organization = organizationDao.findByName("内江");
		User user1 = new User();
		user1.setName("许报1");
		user1.setUserName("xubao1");
		user1.setPassword(MD5Util.getMd5Str("1"));
		user1.setOrganization(organization);
		userDao.save(user1);
//		Page<User> pageUser = userService.findByName("许报", 1, 1);
//		Assert.assertEquals(1, pageUser.getNumberOfElements());
	}
	
	@Test
	@Transactional
	public void testSaveOrUpdate() throws NoSuchAlgorithmException
	{
		Organization organization = organizationDao.findByName("内江");
		UserVo user = new UserVo();
		user.setName("李晓奎");
		user.setUserName("lixiaokui");
		user.setPassword(MD5Util.getMd5Str("1"));
		user.setOrganization(OrgShortVo.createVoNoChild(organization));
		userService.saveOrUpdate(user);
	}

	@Test
	@Transactional
	public void testDelete()
	{
		UserVo user = userService.findByUserName("xubao");
		userService.delete(user);
	}

	@Test
	public void testAuthorize()
	{
		UserVo user = userService.findByUserName("xubao");
		List<Role> roles = new ArrayList<Role>();
		RoleVo role = roleService.findByName("ROLE_TEST");
//		roles.add(role);
//		user.setRoles(roles);
//		userService.saveOrUpdate(user);
	}

	@After
	public void destory() throws Exception 
	{
		Organization organization = organizationDao.findByName("内江");
		if(organization != null)
		{
			organizationDao.delete(organization);
		}
		User user = userDao.findByUserName("xubao");
		if(user != null)
		{
			userDao.delete(user);
		}
		RoleVo role = roleService.findByName("ROLE_TEST");
		if(role != null)
		{
			roleService.delete(role);
		}
	}
}
