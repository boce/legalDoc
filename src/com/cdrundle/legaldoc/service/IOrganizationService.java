package com.cdrundle.legaldoc.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.vo.DistrictVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;


public interface IOrganizationService
{
	/**
	 * 更新或者保存组织机构
	 * @param organizationVo
	 * @return
	 */
	public OrganizationVo saveOrUpdate(OrganizationVo organizationVo);
	
	/**
	 * 更新或者保存区域
	 * @param districtVo
	 * @return
	 */
	public DistrictVo saveDistrict(DistrictVo districtVo);
	
	/**
	 * 根据组织机构对象删除组织机构
	 * @param organization
	 * @return
	 */
	public boolean delete(OrganizationVo organizationVo);
	
	/**
	 * 根据id删除组织机构,非物理删除，设置为不启用
	 * @param id
	 * @return
	 */
	public boolean deleteById(Long id);
	
	/**
	 * 根据区域id删除区域,非物理删除，设置为不启用
	 * @param districtId
	 * @return
	 */
	public boolean deleteDistrictById(Long districtId);
	
	/**
	 * 根据名称查询组织机构
	 * @param name
	 * @return
	 */
	public OrganizationVo find(String name);
	
	/**
	 * 查询所有组织机构
	 * @param name
	 * @return
	 */
	public List<OrganizationVo> findAll();
	
	/**
	 * 查询所有区域、组织机构
	 * @param name
	 * @return
	 */
	public List<DistrictVo> findAllShort();
	
	/**
	 * 查询所有组织机构
	 * @param name
	 * @return
	 */
	public List<OrganizationVo> findAllOrg();
	
	/**
	 * 查询所有组织机构
	 * @param name
	 * @return
	 */
	public Page<OrganizationVo> findAllForQuery(int page, int size);
	
	/**
	 * 根据id查询组织机构
	 * @param id
	 * @return
	 */
	public OrganizationVo findById(long id);
	
	/**
	 * 根据id查询组织机构
	 * @param id
	 * @return
	 */
	public OrgShortVo findByIdShort(long id);
	
	/**
	 * 根据多个id查询组织机构
	 * @param id
	 * @return
	 */
	public List<OrgShortVo> findByIdsShort(Set<Long> ids);
	
	/**
	 * 根据名称模糊查询组织机构
	 * @param name
	 * @return
	 */
	public Page<OrganizationVo> findLikeName(String name, int page, int size);
	
	/**
	 * 查找子组织机构
	 * @param id
	 * @return
	 */
	public List<OrganizationVo> findChildren(long id);
	
	/**
	 * 查找父组织机构
	 * @param id
	 * @return
	 */
	public OrganizationVo findParent(long id);
	
	/**
	 * 查询所有区域
	 * @return
	 */
	public List<DistrictVo> findAllDistrict();
	
	/**
	 * 根据区域查询组织机构
	 * @param districtId
	 * @return
	 */
	public List<OrgShortVo> findOrgByDistrict(Long districtId);
	
	/**
	 * 根据区域id查询区域
	 * @param districtId
	 * @return
	 */
	public DistrictVo findDistrictById(Long districtId);
	
	/**
	 * 获取当前登录用户对应的组织机构的类型
	 * @return
	 */
	public OrgType findUserOrgType();
	
	/**
	 * 根据当前登录用户查询授权组织机构
	 * @return
	 */
	public Set<Long> findAuthOrgId();
	
	/**
	 * 根据组织机构id查询所有下属组织机构id
	 * @param id
	 * @return
	 */
	public Set<Long> findOwnOrgId(Long id);
}
