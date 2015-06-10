package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.enums.OrgType;

/**
 * @author  XuBao
 *
 * 2014年6月17日
 */
public interface IOrganizationDao  extends  Dao<Organization>
{
	
	/**
	 * 查询组织机构
	 * @param name 组织机构名称
	 * @return
	 */
	@Query("select n from Organization n where n.name = :name")
	public Organization findByName(@Param("name")String name);
	
	/**
	 * 查询组织机构
	 * @param name 组织机构名称
	 * @return
	 */
	@Query("select n from Organization n where n.name like %:orgName%")
	public Page<Organization> findLikeName(@Param("orgName")String orgName, Pageable pageable);
	
	/**
	 * 查询跟节点
	 * @return
	 */
	@Query("select n from Organization n where n.parentOrganization.id is null")
	public List<Organization> findRoot();
	
	/**
	 * 根据区域id查询所属根节点
	 * @return
	 */
	@Query("select n from Organization n where n.district.id = :districtId and n.parentOrganization.id is null")
	public List<Organization> findRootByDistrict(@Param("districtId")Long district);
	
	/**
	 * 查询子组织机构
	 * @param name 组织机构名称
	 * @return
	 */
	@Query("select n from Organization n where n.parentOrganization.id = :id")
	public List<Organization> findChildren(@Param("id")long id);
	
	/**
	 * 删除组织机构
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update Organization set isUsed = 'f' where id = :id")
	public void deleteById(@Param("id")long id);
	
	/**
	 * 根据id查询
	 * @return
	 */
	@Query("select n from Organization n where n.id in :ids")
	public List<Organization> findOrgByIds(@Param("ids")Set<Long> ids);
	
	/**
	 * 根据id查询组织机构类型
	 * @return
	 */
	@Query("select n.orgType from Organization n where n.id=:id")
	public OrgType findOrgType(@Param("id")Long id);
	
	/**
	 * 根据区域id和组织机构类型查询组织机构
	 * @return
	 */
	@Query("select n from Organization n where n.district.id = :districtId and n.orgType = :orgType")
	public List<Organization> findOrgByDistrictAndOrgType(@Param("districtId")Long district, @Param("orgType")OrgType orgType);
}
