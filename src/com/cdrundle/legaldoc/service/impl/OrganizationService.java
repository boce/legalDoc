package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IDistrictDao;
import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.entity.District;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.DistrictVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class OrganizationService implements IOrganizationService {

	@Autowired
	IOrganizationDao organizationDao;

	@Autowired
	IDistrictDao districtDao;
	
	@Autowired
	IRoleDao roleDao;
	
	@Autowired
	IEntitlementDao entitlementDao;

	@Override
	@Transactional
	public OrganizationVo saveOrUpdate(OrganizationVo organizationVo) {
		Long id = organizationVo.getId();
		Organization savedOrg;
		if(id != null){
			Organization org = organizationDao.findOne(id);
			savedOrg = organizationDao.save(convertToOrganization(org, organizationVo));
		}else{
			Organization org = new Organization();
			savedOrg = organizationDao.save(convertToOrganization(org, organizationVo));
		}
		
		return OrganizationVo.createVo(savedOrg);
	}

	@Override
	@Transactional
	public boolean delete(OrganizationVo organization) {
		Long id = organization.getId();
		if(id == null){
			return false;
		}
		Organization org = organizationDao.findOne(id);
		organizationDao.delete(org);
		return true;
	}

	@Override
	@Transactional
	public boolean deleteById(Long id) {
		Organization organization = organizationDao.findOne(id);
		List<Organization> childOrganizations = organization.getChildOrganizations();
		if (childOrganizations != null) {
			for (Iterator<Organization> iterator = childOrganizations.iterator(); iterator.hasNext();) {
				Organization org = iterator.next();
				deleteById(org.getId());
			}

		}
		organizationDao.deleteById(id);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationVo find(String name) {
		return OrganizationVo.createVo(organizationDao.findByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrganizationVo> findLikeName(String name, int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		Page<Organization> pageOrganization = organizationDao.findLikeName(name, pageable);
		List<OrganizationVo> volist = OrganizationVo.createVoList(pageOrganization.getContent());
		Page<OrganizationVo> pages = new PageImpl<OrganizationVo>(volist, pageable, pageOrganization.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationVo> findChildren(long id) {
		return OrganizationVo.createVoList(organizationDao.findChildren(id));
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationVo findParent(long id) {
		Organization organization = organizationDao.findOne(id);
		return OrganizationVo.createVo(organization.getParentOrganization());
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationVo findById(long id) {
		return OrganizationVo.createVo(organizationDao.findOne(id));
	}

	@Override
	public OrgShortVo findByIdShort(long id) {
		return OrgShortVo.createVoNoChild(organizationDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationVo> findAll() {
		return OrganizationVo.createVoList(organizationDao.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<DistrictVo> findAllShort() {
		List<District> districts = districtDao.findRoot();
		List<DistrictVo> districtVoList = DistrictVo.createVoList(districts);
		List<OrgShortVo> orgShortVoList = OrgShortVo.createVoList(organizationDao.findRoot());
		genDistricts(districtVoList, orgShortVoList);
		return districtVoList;
	}

	private void genDistricts(List<DistrictVo> districtVoList, List<OrgShortVo> orgShortVoList) {
		for (Iterator<DistrictVo> iterator = districtVoList.iterator(); iterator.hasNext();) {
			DistrictVo districtVo = iterator.next();
			List<OrgShortVo> orgs = new ArrayList<>();
			for (Iterator<OrgShortVo> iterator2 = orgShortVoList.iterator(); iterator2.hasNext();) {
				OrgShortVo orgShortVo = iterator2.next();
				Long district = orgShortVo.getDistrict();
				if (districtVo.getId() == district) {
					orgs.add(orgShortVo);
				}
			}
			districtVo.setOrganizations(orgs);
			List<DistrictVo> childDistricts = districtVo.getChildDistricts();
			if (childDistricts != null && !childDistricts.isEmpty()) {
				genDistricts(childDistricts, orgShortVoList);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationVo> findAllOrg() {
		return OrganizationVo.createVoList(organizationDao.findRoot());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrganizationVo> findAllForQuery(int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		Page<Organization> pageOrgs = organizationDao.findAll(pageable);
		List<OrganizationVo> orgVoList = OrganizationVo.createVoList(pageOrgs.getContent());
		Page<OrganizationVo> pages = new PageImpl<OrganizationVo>(orgVoList, pageable, pageOrgs.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DistrictVo> findAllDistrict() {
		List<District> districts = districtDao.findRoot();
		return DistrictVo.createVoList(districts);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrgShortVo> findOrgByDistrict(Long districtId) {
		List<Organization> orgs = organizationDao.findRootByDistrict(districtId);
		return OrgShortVo.createVoList(orgs);
	}

	@Override
	@Transactional
	public DistrictVo saveDistrict(DistrictVo districtVo) {
		if (districtVo == null) {
			return null;
		}
		District savedDistrict;
		Long districtId = districtVo.getId();
		if (districtId != null) {
			District district = districtDao.findOne(districtId);
			district.setName(districtVo.getName());
			district.setIsUsed(districtVo.getIsUsed());
			DistrictVo parent = districtVo.getParent();
			if (parent != null && parent.getId() != null) {
				district.setParent(districtDao.findOne(parent.getId()));
			}
			savedDistrict = districtDao.save(district);
		} else {
			savedDistrict = districtDao.save(convertToDistrict(districtVo));
		}
		return DistrictVo.createVo(savedDistrict);
	}

	@Override
	@Transactional(readOnly = true)
	public DistrictVo findDistrictById(Long districtId) {
		DistrictVo districtVo = DistrictVo.createVoNoChild(districtDao.findOne(districtId));
		return districtVo;
	}
	
	@Override
	@Transactional
	public boolean deleteDistrictById(Long districtId) {
		districtDao.deleteById(districtId);
		return true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public OrgType findUserOrgType() {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		OrgType orgType = organizationDao.findOrgType(orgId);
		return orgType;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrgShortVo> findByIdsShort(Set<Long> ids) {
		List<Organization> orgList = organizationDao.findOrgByIds(ids);
		return OrgShortVo.createVoListNoChild(orgList);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<Long> findAuthOrgId() {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Set<Long> orgIds = new HashSet<>();
		orgIds.add(Long.parseLong(userDetail.getOrgId()));
		Set<Long> roleIds = new HashSet<>();
		List<GrantedAuthority> authorities = userDetail.getAuthorities();
		for (Iterator<GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext();) {
			GrantedAuthority grantedAuthority = iterator.next();
			String authority = grantedAuthority.getAuthority();
			Role role = roleDao.findByName(authority);
			roleIds.add(role.getId());
		}
		List<Organization> orgAuths = entitlementDao.findOrgAuth(roleIds);
		for (Iterator<Organization> iterator = orgAuths.iterator(); iterator.hasNext();) {
			Organization organization = iterator.next();
			orgIds.add(organization.getId());
		}
		return orgIds;
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Long> findOwnOrgId(Long id) {
		Set<Long> orgIds = new HashSet<>();
		Organization organization = organizationDao.findOne(id);
		OrgShortVo orgShortVo = OrgShortVo.createVo(organization);
		orgIds.add(orgShortVo.getId());
		List<OrgShortVo> childs = orgShortVo.getChildren();
		if(childs != null && !childs.isEmpty()){
			genOrgId(childs, orgIds);
		}
		return orgIds;
	}
	
	private void genOrgId(List<OrgShortVo> orgs, Set<Long> orgIds){
		for (Iterator<OrgShortVo> iterator = orgs.iterator(); iterator.hasNext();) {
			OrgShortVo vo = iterator.next();
			orgIds.add(vo.getId());
			List<OrgShortVo> childs = vo.getChildren();
			if(childs != null && !childs.isEmpty()){
				genOrgId(childs, orgIds);
			}
		}
	}
	
	private Organization convertToOrganization(Organization organization, OrganizationVo orgVo) {
		organization.setId(orgVo.getId());
		organization.setName(orgVo.getName());
		organization.setAddress(orgVo.getAddress());
		organization.setPhone(orgVo.getPhone());
		organization.setWebserviceUrl(orgVo.getWebserviceUrl());
		organization.setIsUsed(orgVo.getIsUsed());
		organization.setOrgType(orgVo.getOrgType());
		organization.setWebserviceUrl(orgVo.getWebserviceUrl());
		OrgShortVo parentOrg = orgVo.getParentOrganization();
		if (parentOrg != null && parentOrg.getId() != null) {
			organization.setParentOrganization(organizationDao.findOne(parentOrg.getId()));
		}
		DistrictVo district = orgVo.getDistrict();
		if (district != null && district.getId() != null) {
			organization.setDistrict(districtDao.findOne(district.getId()));
		}else{
			organization.setDistrict(null);
		}
		OrgShortVo reviewUnit = orgVo.getReviewUnit();
		if (reviewUnit != null && reviewUnit.getId() != null) {
			organization.setReviewUnit(organizationDao.findOne(reviewUnit.getId()));
		}else{
			organization.setReviewUnit(null);
		}
		OrgShortVo recordUnit = orgVo.getRecordUnit();
		if (recordUnit != null && recordUnit.getId() != null) {
			organization.setRecordUnit(organizationDao.findOne(recordUnit.getId()));
		}else{
			organization.setRecordUnit(null);
		}
		organization.setDisplayOrder(orgVo.getDisplayOrder());
		return organization;
	}

	private District convertToDistrict(DistrictVo districtVo) {
		District district = new District();
		district.setId(districtVo.getId());
		district.setName(districtVo.getName());
		district.setIsUsed(districtVo.getIsUsed());
		DistrictVo parent = districtVo.getParent();
		if (parent != null && parent.getId() != null) {
			district.setParent(districtDao.findOne(parent.getId()));
		}
		return district;
	}

}
