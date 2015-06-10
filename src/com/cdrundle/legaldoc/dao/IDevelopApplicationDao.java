package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.DevelopApplication;

/**
 * 立项
 * @author xiaokui.li
 *
 */
public interface IDevelopApplicationDao extends Dao<DevelopApplication>
{
	/**
	 * 通过名称查找立项申请单
	 * @param name
	 * @return
	 */
	@Query("select d from DevelopApplication d where d.name = ?1")
	public DevelopApplication findByName(String name);
	
	/**
	 * 通过名称查找立项申请单
	 * @param name
	 * @return
	 */
	@Query("select d from DevelopApplication d where d.name like :name and d.applyOrg.id in (:applyOrgId)")
	public Page<DevelopApplication> findLikeName(@Param("applyOrgId")Set<Long> applyOrgId, @Param("name")String name, Pageable pageable);
	
	/**
	 * 通过名称查找立项申请单
	 * @param name
	 * @return
	 */
	@Query("select d from DevelopApplication d where d.applyOrg.id in (:applyOrgId)")
	public Page<DevelopApplication> findAll(@Param("applyOrgId")Set<Long> applyOrgId, Pageable pageable);
	
	@Query("select d from DevelopApplication d where d.normativeFile.id = :id")
	public DevelopApplication findByNorFileId(@Param("id")Long id);
	
	/**
	 * 更新制定依据
	 * @param id
	 * @param atta
	 */
	@Transactional
	@Modifying
	@Query("update DevelopApplication set legalBasisAttachment = :atta where id = :id")
	public void updateLegalBasisAtta(@Param("id") Long id, @Param("atta") String atta);
	
	/**
	 * 更新制定的必要性、合法性，以及社会稳定性风险评估附件
	 * @param id
	 * @param atta
	 */
	@Transactional
	@Modifying
	@Query("update DevelopApplication set necessityLegalAndRiskAttachment = :atta where id = :id")
	public void updatenecLegRiskAtta(@Param("id") Long id, @Param("atta") String atta);
	
	/**
	 * 更新拟解决的主要问题附件
	 * @param id
	 * @param atta
	 */
	@Transactional
	@Modifying
	@Query("update DevelopApplication set mainProblemAttachment = :atta where id = :id")
	public void updatemainProblemAtta(@Param("id") Long id, @Param("atta") String atta);
	
	/**
	 * 更新拟确定的制度或措施，以及可行性论证附件
	 * @param id
	 * @param atta
	 */
	@Transactional
	@Modifying
	@Query("update DevelopApplication set planRegulationMeasureAndFeasibilityAtta = :atta where id = :id")
	public void updateplanRegMeaAtta(@Param("id") Long id, @Param("atta") String atta);
}
