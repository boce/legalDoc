package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.Stage;

/**
 * @author  XuBao
 *规范性文件
 * 2014年6月12日
 */
public interface INormativeFileDao  extends  Dao<NormativeFile> {
		
	/**
	 * 查找出所有的规范性文件
	 * @param stage  阶段
	 * @param applyUnit  申请单位
	 * @return  page
	 */
	@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and  n.applyUnit.id = :id  ")
    public  Page<NormativeFile>  findAllOnSetup(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
	
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param applyUnit  申请单位
		 * @return  page
		 */
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and  n.applyUnit.id = :id  ")
	    public  Page<NormativeFile>  findAllOnDrafting(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param drtUnit  主起草单位
		 * @param unionDrtUnit  联合起草单位
		 * @param involvedOrges  涉及部门
		 * @return  page
		 */
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and ( n.drtUnit.id = :id  or n.unionDrtUnit like %:unionDrtUnit% or n.involvedOrges like %:involvedOrges%)")
	    public  Page<NormativeFile>  findAllOnRequestComment(@Param("stage")Stage   stage, @Param("id")long id,@Param("unionDrtUnit")String unionDrtUnit,@Param("involvedOrges")String involvedOrges,Pageable pageable);
		
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param drtUnit  主起草单位
		 * @param unionDrtUnit  联合起草单位
		 * @param revUnit  审查单位
		 * @return  page
		 */
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and (n.revUnit.id = :id)")
	    public  Page<NormativeFile>  findAllOnLegalReview(@Param("stage")Stage stage, @Param("id")long id, Pageable pageable);
		
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param drtUnit  主起草单位
		 * @param unionDrtUnit  联合起草单位
		 * @return  page
		 */
		
		@Query("select  n  from  NormativeFile n  where   n.stage = :stage  and ( n.drtUnit.id = :id  or n.unionDrtUnit like %:unionDrtUnit%)")
	    public  Page<NormativeFile>  findOnDeliberationRequest(@Param("stage")Stage   stage, @Param("id")long id,@Param("unionDrtUnit")String unionDrtUnit,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where n.stage = :stage  and n.drtUnit.id = :id ")
	    public  Page<NormativeFile>  findOnDeliberationProtocol(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where n.stage = :stage  and n.drtUnit.id = :id ")
	    public  Page<NormativeFile>  findOnDeliberationModofily(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param drtUnit  主起草单位
		 * @param unionDrtUnit  联合起草单位
		 * @param decUnit  制定单位
		 * @return  page
		 */
		
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and ( n.drtUnit.id = :id or n.decUnit.id = :id)")
	    public  Page<NormativeFile>  findOnPublish(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		/**
		 * 查找出所有的规范性文件
		 * @param stage  阶段
		 * @param drtUnit  主起草单位
		 * @param unionDrtUnit  联合起草单位
		 * @param recRevUnit  备案审查单位
		 * @return  page
		 */
	    
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and n.recRevUnit.id = :id")
	    public  Page<NormativeFile>  findOnRecordRequest(@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where n.stage = :stage  and ( n.drtUnit.id = :id or n.recRevUnit.id = :id)")
	    public  Page<NormativeFile>  findOnRecordReview(@Param("stage")Stage stage, @Param("id")long id,Pageable pageable);
		
		
		@Query("select  n  from  NormativeFile n  where (n.stage = 'PUBLISH' or n.stage = 'RECORD_REQUEST' or n.stage = 'RECORD_REVIEW' or n.stage = 'RECORD_REGISTER') and (n.drtUnit.id = :id  or n.unionDrtUnit like %:unionDrtUnit% or n.decUnit.id = :id)")
	    public  Page<NormativeFile>  findAllForAdjust(@Param("id")long id,@Param("unionDrtUnit")String unionDrtUnit,Pageable pageable);
		
		/**
		 * 通过文件名模糊查询规范性文件
		 * @param name
		 * @param drtUnit
		 * @param applyUnit
		 * @param pageable
		 * @return  page
		 */
		@Query("select n  from  NormativeFile n  where  n.name like %:name% and  n.stage = :stage  and ( n.drtUnit.id = :id or n.applyUnit.id = :id ) ")
		 public   Page<NormativeFile>    findByNameOnSetup(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id , Pageable pageable);
		
		/**
		 * 通过文件名模糊查询规范性文件
		 * @param name
		 * @param drtUnit
		 * @param applyUnit
		 * @param pageable
		 * @return  page
		 */
		@Query("select n  from  NormativeFile n  where  n.name like %:name% and  n.stage = :stage  and ( n.drtUnit.id = :id or n.applyUnit.id = :id ) ")
		 public   Page<NormativeFile>    findByNameOnDrafting(@Param("name")String name,@Param("stage")Stage stage, @Param("id")long id , Pageable pageable);
		
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and (n.involvedOrges like %:involvedOrges%)")
	    public  Page<NormativeFile>  findByNameOnRequestComment(@Param("name")String name,@Param("stage")Stage stage,@Param("involvedOrges")String involvedOrges,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and (n.involvedOrges like %:involvedOrges%)")
	    public  Page<NormativeFile>  findOnRequestComment(@Param("stage")Stage stage, @Param("involvedOrges")String involvedOrges, Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and ( n.drtUnit.id = :id)")
	    public  Page<NormativeFile>  findByNameOnRequestComment(@Param("name")String name,@Param("stage")Stage stage, @Param("id")long id, Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.stage = :stage  and ( n.drtUnit.id = :id)")
	    public  Page<NormativeFile>  findOnRequestComment(@Param("stage")Stage stage, @Param("id")long id, Pageable pageable);

		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage and ( n.revUnit.id = :id)")
	    public  Page<NormativeFile>  findByNameOnLegalReview(@Param("name")String name, @Param("stage")Stage  stage, @Param("id")long id, Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and ( n.drtUnit.id = :id  or n.unionDrtUnit like %:unionDrtUnit%)")
	    public  Page<NormativeFile>  findByNameOnDeliberationRequest(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id,@Param("unionDrtUnit")String unionDrtUnit,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and n.drtUnit.id = :id ")
	    public  Page<NormativeFile>  findByNameOnDeliberationProtocol(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and n.drtUnit.id = :id ")
	    public  Page<NormativeFile>  findByNameOnDeliberationModofily(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and ( n.drtUnit.id = :id or n.decUnit.id = :id)")
	    public  Page<NormativeFile>  findByNameOnPublish(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and n.recRevUnit.id = :id")
	    public  Page<NormativeFile>  findByNameOnRecordRequest(@Param("name")String name,@Param("stage")Stage   stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and n.stage = :stage  and ( n.drtUnit.id = :id or n.recRevUnit.id = :id)")
	    public  Page<NormativeFile>  findByNameOnRecordReview(@Param("name")String name,@Param("stage")Stage stage, @Param("id")long id,Pageable pageable);
		
		@Query("select  n  from  NormativeFile n  where  n.name like %:name% and (n.stage = 'PUBLISH' or n.stage = 'RECORD_REQUEST' or n.stage = 'RECORD_REVIEW' or n.stage = 'RECORD_REGISTER') and (n.drtUnit.id = :id  or n.unionDrtUnit like %:unionDrtUnit% or n.decUnit.id = :id)")
	    public  Page<NormativeFile>  findByNameForAdjust(@Param("name")String name,@Param("id")long id,@Param("unionDrtUnit")String unionDrtUnit,Pageable pageable);
		
		/**
		 * 通过文件名查找
		 * @param name  规范新文件名称
		 * @return
		 */
		@Query("select  n  from NormativeFile n  where n.name = :name ")
		public  NormativeFile  findByNorFileName(@Param("name")String  name);
		
		/**
		 * 根据当前日期查询单据号
		 * @param date
		 * @return
		 */
		@Query("select max(n.docNo) from NormativeFile n where n.docNo like :date%")
		public String findDocNo(@Param("date") String date);
		
		/**
		 * 查询本部门今天备案文件的最大备案号
		 * @param str
		 * @return
		 */
		@Query("select max(n.registerCode) from NormativeFile n where n.registerCode like :str%")
		public String findRegisterCode(@Param("str") String str);
		
		/**
		 * 查询制定部门或者起草部门等于orgId的规范性文件
		 * @param orgId
		 * @param unionDraft
		 * @param pageable
		 * @return
		 */
		@Query("select n from NormativeFile n where n.decUnit.id = :orgId or n.drtUnit.id = :orgId or n.unionDrtUnit like %:unionDraft% ")
		public Page<NormativeFile> findAllForOwnOrg(@Param("orgId") Long orgId, @Param("unionDraft") String unionDraft, Pageable pageable);
		
		/**
		 * 查询制定部门或者起草部门等于orgId的规范性文件
		 * @param name
		 * @param orgId
		 * @param unionDraft
		 * @param pageable
		 * @return
		 */
		@Query("select n from NormativeFile n where n.name like %:name% and (n.decUnit.id = :orgId or n.drtUnit.id = :orgId or n.unionDrtUnit like %:unionDraft%) ")
		public Page<NormativeFile> findAllForOwnOrgByName(@Param("name") String name,@Param("orgId") Long orgId, @Param("unionDraft") String unionDraft, Pageable pageable);
		
		
		/**
		 * 查询制定单位或者起草单位已发布的规范性文件
		 * @param stage
		 * @param orgIds
		 * @param pageable
		 * @return
		 */
		@Query("select  n  from  NormativeFile n  where  (n.stage = 'PUBLISH' or n.stage = 'RECORD_REQUEST' or n.stage = 'RECORD_REVIEW' or n.stage = 'RECORD_REGISTER') and (n.decUnit.id in (:orgIds))")
	    public  Page<NormativeFile>  findNorFileOnPublish(@Param("orgIds") Set<Long> orgIds, Pageable pageable);
		
		/**
		 * 根据名称模糊查询制定单位或者起草单位已发布的规范性文件
		 * @param stage
		 * @param name
		 * @param orgIds
		 * @param pageable
		 * @return
		 */
		@Query("select n from NormativeFile n where n.name like :name and (n.stage = 'PUBLISH' or n.stage = 'RECORD_REQUEST' or n.stage = 'RECORD_REVIEW' or n.stage = 'RECORD_REGISTER') and  (n.decUnit.id in (:orgIds))")
	    public  Page<NormativeFile>  findNorFileOnPublishByName( @Param("name") String name, @Param("orgIds") Set<Long> orgIds, Pageable pageable);
		
		/**
		 * 更新文件状态
		 * @param id
		 * @param fileStatus
		 */
		@Transactional
		@Modifying
		@Query("update NormativeFile set status = :fileStatus where id = :id")
		public void updateLegalBasisAtta(@Param("id") Long id, @Param("fileStatus") FileStatus fileStatus);
}
