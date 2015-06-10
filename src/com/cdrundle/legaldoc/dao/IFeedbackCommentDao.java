package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.FeedbackComment;

/**
 * 意见征求--反馈意见
 * @author xiaokui.li
 *
 */
public interface IFeedbackCommentDao extends Dao<FeedbackComment>
{
	/**
	 * 通过名称查询反馈意见单
	 * @param name
	 * @return
	 */
	@Query("select f from FeedbackComment f where f.name = :name")
	public FeedbackComment findByName(@Param("name")String name);
	
	/**
	 * 通过名称查询反馈意见单
	 * @param name
	 * @return
	 */
	@Query("select f from FeedbackComment f where f.name = :name and f.feedbackUnit.id = :feedbackUnitId")
	public FeedbackComment findByNameAndUnit(@Param("name")String name, @Param("feedbackUnitId")Long feedbackUnitId);
	
	/**
	 * 通过名称查找反馈意见单
	 * @param name
	 * @return
	 */
	@Query("select d from FeedbackComment d where (d.draftingUnit.id in(:ids) or d.feedbackUnit.id in(:ids)) and d.name like %:name%")
	public Page<FeedbackComment> findByName(@Param("ids")Set<Long> orgIds, @Param("name")String name, Pageable pageable);
	
	@Query("select a from FeedbackComment a where a.normativeFile.id = :id and a.feedbackUnit.id=:feedbackUnitId")
	public FeedbackComment findByNorFileId(@Param("id")Long id,@Param("feedbackUnitId")Long feedbackUnitId);
	
	@Query("select a from FeedbackComment a where a.normativeFile.id = :id")
	public List<FeedbackComment> findByNorFile(@Param("id")Long id);
	
	@Query("select a from FeedbackComment a where a.draftingUnit.id in(:ids) or a.feedbackUnit.id in(:ids)")
	public Page<FeedbackComment> findAll(@Param("ids")Set<Long> ids,Pageable pageable);
}
