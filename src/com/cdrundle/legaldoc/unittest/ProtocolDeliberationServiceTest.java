package com.cdrundle.legaldoc.unittest;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.ProtocolDeliberation;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IProtocolDeliberationService;
import com.cdrundle.legaldoc.vo.ProtocolDeliberationVo;

/**
 * @author  XuBao
 *
 * 2014年6月23日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("applicationContext.xml")
public class ProtocolDeliberationServiceTest{
	
	@Autowired
	private   IProtocolDeliberationService   protocolDeliberationlService;
	@Autowired
	private   INormativeFileDao       normativeFileDao;
	@Autowired
	private  IOrganizationDao  organizationDao;
	
	@Before
	public void init() throws Exception{
		
		NormativeFile  normativeFile  =new  NormativeFile();
		normativeFile.setName("123");
		normativeFileDao.save(normativeFile);
		
		Organization		organization  =   new  Organization();
		organization.setName("内江");
		organizationDao.save(organization);
	}
	
	@After
	public void destory() throws  Exception{
		
		ProtocolDeliberation protocolDeliberation = protocolDeliberationlService.findProtocolDeliberationByName("123");
		if( protocolDeliberation != null)
			protocolDeliberationlService.delete(protocolDeliberation.getId());
		
		NormativeFile  normativeFile=normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);
		
		Organization		organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
	}
	
	@Test
	public void testSaveOrUpdate() throws ServiceException{
		ProtocolDeliberationVo    protocolDeliberationVo   = new ProtocolDeliberationVo();
		Organization		organization = organizationDao.findByName("内江");
		protocolDeliberationVo.setDeliberationUnit(organization);
		protocolDeliberationVo.setDeliberationDate(new  Date());
		protocolDeliberationlService.saveOrUpdate(protocolDeliberationVo);
	}

	@Test
	public void testDelete() throws ServiceException{
		ProtocolDeliberation   protocolDeliberation  =  protocolDeliberationlService.findProtocolDeliberationByName("123");
		protocolDeliberationlService.delete(protocolDeliberation.getId());
	}

	@Test
	public void testFindAll() {
		Page<ProtocolDeliberation> page = protocolDeliberationlService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testFindByName() {
		Page<ProtocolDeliberation> page = protocolDeliberationlService.findByName("123", 1, 1);
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

}
