package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IAdoptCommentDao;
import com.cdrundle.legaldoc.dao.IFeedbackCommentDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.AdoptComment;
import com.cdrundle.legaldoc.entity.FeedbackComment;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IFeedbackCommentService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.FeedbackCommentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class FeedbackCommentService implements IFeedbackCommentService {

	@Autowired
	IFeedbackCommentDao feedbackCommentDao;

	@Autowired
	INormativeFileDao normativeFileDao;

	@Autowired
	IOrganizationDao organizationDao;

	@Autowired
	IUserDao userDao;
	
	@Autowired
	IAdoptCommentDao adoptCommentDao;

	@Override
	@Transactional
	public FeedbackCommentVo saveOrUpdate(FeedbackCommentVo feedbackCommentVo, String rootPath) throws ServiceException {
		if (feedbackCommentVo != null) {
			NormativeFileVo norFileVo = null;
			FeedbackComment feedbackComment = null;
			FeedbackComment savedFeedbackComment;
			if (feedbackCommentVo.getId() != null && feedbackCommentVo.getId() > 0) {

				feedbackComment = feedbackCommentDao.findOne(feedbackCommentVo.getId());
				NormativeFile normativeFile = feedbackComment.getNormativeFile();
				AdoptComment adoptComment = adoptCommentDao.findByNorFileId(normativeFile.getId());
				if(adoptComment != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
				feedbackComment.setModifyOpinions(feedbackCommentVo.getModifyOpinions());
				norFileVo = NormativeFileVo.createVo(normativeFile);

				savedFeedbackComment = feedbackCommentDao.save(feedbackComment);
			} else {
				feedbackComment = new FeedbackComment();
				Long norId = feedbackCommentVo.getNormativeFile().getId();
				NormativeFile norFile = normativeFileDao.findOne(norId);
				FeedbackComment feedbackCommentByName = feedbackCommentDao.findByNameAndUnit(norFile.getName(), feedbackCommentVo.getFeedbackUnit().getId());
				if(feedbackCommentByName != null){
					throw new ServiceException("保存出错！该反馈意见单已经填写完成，请通过查找修改！");
				}
				String involvedOrges = norFile.getInvolvedOrges();
				if(StringUtils.isNotEmpty(involvedOrges)){
					involvedOrges = involvedOrges.replaceAll("\"", "");
					String[] involvedOrgArray = involvedOrges.split(",");
					List<FeedbackComment> feedbackComments = feedbackCommentDao.findByNorFile(norId);
					if((involvedOrgArray.length - 1) == feedbackComments.size()){
						int count = 0;
						for (Iterator<FeedbackComment> iterator = feedbackComments.iterator(); iterator.hasNext();) {
							FeedbackComment feedbackComment2 = iterator.next();
							for (int i = 0; i < involvedOrgArray.length; i++) {
								if(feedbackComment2.getFeedbackUnit().getId().equals(Long.parseLong(involvedOrgArray[i]))){
									count++;
									break;
								}
							}
						}
						if(count == (involvedOrgArray.length -1)){
							norFile.setStage(Stage.REQUEST_COMMENT_FEEDBACK);
							normativeFileDao.save(norFile);
						}
					}
				}
				feedbackComment.setId(feedbackCommentVo.getId());
				feedbackComment.setName(norFile.getName());
				feedbackComment.setNormativeFile(norFile);
				feedbackComment.setDraftingUnit(norFile.getDrtUnit());
				feedbackComment.setDraftingUnitLeader(norFile.getDrtUnitLeader());
				feedbackComment.setDraftingUnitClerk(norFile.getDrtUnitClerk());
				feedbackComment.setFeedbackUnit(organizationDao.findOne(feedbackCommentVo.getFeedbackUnit().getId()));
				feedbackComment.setFeedbackUnitClerk(userDao.findOne(feedbackCommentVo.getFeedbackUnitClerk().getId()));

				feedbackComment.setLatestFeedbackDate(feedbackCommentVo.getLatestFeedbackDate());
				feedbackComment.setActualFeedbackDate(feedbackCommentVo.getActualFeedbackDate());

				if (!StringUtils.isEmpty(feedbackCommentVo.getRequestingDraft())) {
					feedbackComment.setRequestingDraft(feedbackCommentVo.getRequestingDraft());
				} else {
					feedbackComment.setRequestingDraft("");
				}
				feedbackComment.setModifyOpinions(feedbackCommentVo.getModifyOpinions());

				norFileVo = NormativeFileVo.createVo(norFile);

				savedFeedbackComment = feedbackCommentDao.save(feedbackComment);

			}
			String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_FEEDBACK + "-" + feedbackComment.getFeedbackUnit().getName() + ")"
					+ SysUtil.EXTENSION_NAME;
			// 保存反馈处理意见文档
			WordUtils.htmlToWord(filePath, fileName, feedbackCommentVo.getModifyOpinions());
			return FeedbackCommentVo.createVo(savedFeedbackComment);

		}
		return null;
	}

	@Override
	@Transactional
	public boolean delete(Long id, String rootPath) throws ServiceException {
		if (id != null) {
			FeedbackComment comment = feedbackCommentDao.findOne(id);
			
			NormativeFile norFile = comment.getNormativeFile();
			NormativeFileVo normativeFileVo = NormativeFileVo.createVo(norFile);
			AdoptComment adoptComment = adoptCommentDao.findByNorFileId(normativeFileVo.getId());
			if(adoptComment != null){
				throw new ServiceException("已存在下游业务,不允许删除");
			}
			
			norFile.setStage(Stage.REQUEST_COMMENT_REQUEST);
			normativeFileDao.save(norFile);
			
			String filePath = WordUtils.getFilePath(rootPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_FEEDBACK + "-" + comment.getFeedbackUnit().getName() + ")"
					+ SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			
			feedbackCommentDao.delete(id);
			// 删除反馈意见处理情况文件
			WordUtils.deleteWord(filePath);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackCommentVo findById(long id) {
		FeedbackComment feedbackComment = feedbackCommentDao.findOne(id);
		return FeedbackCommentVo.createVo(feedbackComment);
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackCommentVo findByName(String name) {
		FeedbackComment feedbackComment = feedbackCommentDao.findByName(name);
		return FeedbackCommentVo.createVo(feedbackComment);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FeedbackCommentVo> findByName(String name, Set<Long> orgIds, int page, int size) {
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "actualFeedbackDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<FeedbackComment> pageFeedbackComment = null;
		if (!StringUtils.isEmpty(name)) {
			pageFeedbackComment = feedbackCommentDao.findByName(orgIds, name, pageable);
		} else {
			pageFeedbackComment = feedbackCommentDao.findAll(orgIds, pageable);
		}

		List<FeedbackCommentVo> volist = FeedbackCommentVo.createVoList(pageFeedbackComment.getContent());
		Page<FeedbackCommentVo> pages = new PageImpl<FeedbackCommentVo>(volist, pageable, pageFeedbackComment.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackCommentVo findByNorFileId(Long id, Long feedbackUnit) {
		if (id != null) {
			if(feedbackUnit == null){
				WebPlatformUser userDetail = SysUtil.getLoginInfo();
				feedbackUnit = Long.parseLong(userDetail.getOrgId());
			}
			FeedbackComment feedbackComment = feedbackCommentDao.findByNorFileId(id, feedbackUnit);
			if (feedbackComment != null) {
				return FeedbackCommentVo.createVo(feedbackComment);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FeedbackCommentVo> findByNorFile(Long norId) {
		List<FeedbackComment> feedbackComments = feedbackCommentDao.findByNorFile(norId);
		return FeedbackCommentVo.createVoList(feedbackComments);
	}
}
