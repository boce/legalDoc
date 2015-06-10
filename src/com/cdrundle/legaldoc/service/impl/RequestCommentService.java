package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.cdrundle.legaldoc.dao.IRequestCommentDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.AdoptComment;
import com.cdrundle.legaldoc.entity.FeedbackComment;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.RequestComment;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRequestCommentService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.RequestCommentVo;

@Service
public class RequestCommentService implements IRequestCommentService {
	@Autowired
	IRequestCommentDao requestCommentDao;

	@Autowired
	INormativeFileDao normativeFileDao;

	@Autowired
	IOrganizationDao organizationDao;

	@Autowired
	IUserDao userDao;
	
	@Autowired
	IFeedbackCommentDao feedbackCommentDao;
	
	@Autowired
	IAdoptCommentDao adoptCommentDao;

	@Override
	@Transactional
	public RequestCommentVo saveOrUpdate(RequestCommentVo requestCommentVo, String rootPath) throws ServiceException {
		if (requestCommentVo != null) {
			NormativeFileVo norFileVo = null;
			RequestComment requestComment = null;
			RequestComment savedRequestComment;
			if (requestCommentVo.getId() != null && requestCommentVo.getId() > 0) {

				requestComment = requestCommentDao.findOne(requestCommentVo.getId());
				NormativeFile norFile = requestComment.getNormativeFile();
				List<FeedbackComment> feedbackComments = feedbackCommentDao.findByNorFile(norFile.getId());
				if(feedbackComments != null && !feedbackComments.isEmpty()){
					throw new ServiceException("已存在下游业务,不允许修改");
				}else{
					AdoptComment adoptComment = adoptCommentDao.findByNorFileId(norFile.getId());
					if(adoptComment != null){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}
				if (requestCommentVo.getRequestFromUnit() != null) {
					requestComment.setRequestFromUnit(organizationDao.findOne(requestCommentVo.getRequestFromUnit().getId()));
				}
				if (requestCommentVo.getLatestFeedbackDate() != null) {
					requestComment.setLatestFeedbackDate(requestCommentVo.getLatestFeedbackDate());
				}
				requestComment.setRequestingDraft(requestCommentVo.getRequestingDraft());
				requestComment.setContent(requestCommentVo.getContent());
				norFileVo = NormativeFileVo.createVo(norFile);
				savedRequestComment = requestCommentDao.save(requestComment);
			} else {
				requestComment = new RequestComment();
				Long norId = requestCommentVo.getNormativeFile().getId();
				NormativeFile norFile = normativeFileDao.findOne(norId);
				String involvedOrges = norFile.getInvolvedOrges();
				if(StringUtils.isNotEmpty(involvedOrges)){
					involvedOrges = involvedOrges.replaceAll("\"", "");
					String[] involvedOrgArray = involvedOrges.split(",");
					List<RequestComment> requestComments = requestCommentDao.findByNorFile(norId);
					if((involvedOrgArray.length - 1) == requestComments.size()){
						int count = 0;
						for (Iterator<RequestComment> iterator = requestComments.iterator(); iterator.hasNext();) {
							RequestComment requestComment2 = iterator.next();
							for (int i = 0; i < involvedOrgArray.length; i++) {
								if(requestComment2.getRequestFromUnit().getId().equals(Long.parseLong(involvedOrgArray[i]))){
									count++;
									break;
								}
							}
						}
						if(count == (involvedOrgArray.length - 1)){
							norFile.setStage(Stage.REQUEST_COMMENT_REQUEST);
							normativeFileDao.save(norFile);
						}
					}
				}
				requestComment.setName(norFile.getName());
				requestComment.setNormativeFile(norFile);
				requestComment.setDraftingUnit(norFile.getDrtUnit());
				requestComment.setDraftingUnitLeader(norFile.getDrtUnitLeader());
				requestComment.setDraftingUnitClerk(norFile.getDrtUnitClerk());
				requestComment.setRequestFromUnit(organizationDao.findOne(requestCommentVo.getRequestFromUnit().getId()));
				requestComment.setLatestFeedbackDate(requestCommentVo.getLatestFeedbackDate());
				requestComment.setRequestingDraft(requestCommentVo.getRequestingDraft());
				requestComment.setContent(requestCommentVo.getContent());

				norFileVo = NormativeFileVo.createVo(norFile);

				savedRequestComment = requestCommentDao.save(requestComment);

			}
			String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTOPINIONLETTER + "-"
					+ savedRequestComment.getRequestFromUnit().getName() + ")" + SysUtil.EXTENSION_NAME;
			// 保存反馈处理意见文档
			WordUtils.htmlToWord(filePath, fileName, requestCommentVo.getContent());
			return RequestCommentVo.createVo(savedRequestComment);

		}
		return null;
	}

	@Override
	@Transactional
	public boolean delete(Long id, String rootPath) throws ServiceException {
		if (id != null) {
			RequestComment requestComment = requestCommentDao.findOne(id);
			NormativeFile norFile = requestComment.getNormativeFile();
			NormativeFileVo normativeFileVo = NormativeFileVo.createVo(norFile);
			Long norId = normativeFileVo.getId();
			
			FeedbackComment feedbackComment = feedbackCommentDao.findByNorFileId(norId, requestComment.getRequestFromUnit().getId());
			if(feedbackComment != null){
				throw new ServiceException("已存在下游业务,不允许删除");
			}
			
			norFile.setStage(Stage.DRAFTING);
			normativeFileDao.save(norFile);
			
			String filePath = WordUtils.getFilePath(rootPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTOPINIONLETTER + "-"
							+ requestComment.getRequestFromUnit().getName() + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;

			requestCommentDao.delete(id);
			// 删除反馈意见处理情况文件
			WordUtils.deleteWord(filePath);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public RequestCommentVo findById(long id) {
		RequestComment requestComment = requestCommentDao.findOne(id);
		return RequestCommentVo.createVo(requestComment);
	}

	@Override
	@Transactional(readOnly = true)
	public RequestCommentVo findByName(String name) {
		RequestComment requestComment = requestCommentDao.findByName(name);
		return RequestCommentVo.createVo(requestComment);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RequestCommentVo> findByName(String name, Set<Long> orgIds, int page, int size) {
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "latestFeedbackDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<RequestComment> pageRequestComment = null;
		if (StringUtils.isNotEmpty(name)) {
			pageRequestComment = requestCommentDao.findByName(orgIds, "%" + name + "%", pageable);
		} else {
			pageRequestComment = requestCommentDao.findAll(orgIds, pageable);
		}

		List<RequestCommentVo> volist = RequestCommentVo.createVoList(pageRequestComment.getContent());
		Page<RequestCommentVo> pages = new PageImpl<RequestCommentVo>(volist, pageable, pageRequestComment.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public RequestCommentVo findByNorFileId(Long norId, Long reqFromUnitId) {
		if (norId != null) {
			RequestComment requestComment = requestCommentDao.findByNorFileId(norId, reqFromUnitId);
			if (requestComment != null) {
				return RequestCommentVo.createVo(requestComment);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestCommentVo> findByNorFile(Long norId) {
		List<RequestComment> requestComments = requestCommentDao.findByNorFile(norId);
		return RequestCommentVo.createVoList(requestComments);
	}

	@Override
	@Transactional
	public boolean completeRequestback(Long id) throws ServiceException {
		RequestComment requestComment = requestCommentDao.findOne(id);
		Stage stage = requestComment.getNormativeFile().getStage();
		//如果还处于征求意见，不能结束反馈意见
		if(Stage.DRAFTING.equals(stage)){
			throw new ServiceException("征求意见未完成，不能结束反馈意见");
		}
		Date latestFeedbackDate = requestComment.getLatestFeedbackDate();
		SimpleDateFormat sdf = new SimpleDateFormat(SysUtil.DATE_FORMAT);
		String latestFeedback = sdf.format(latestFeedbackDate);
		Calendar c = Calendar.getInstance();
		String currentTime = sdf.format(c.getTime());
		if(currentTime.compareTo(latestFeedback) > 0){
			NormativeFile normativeFile = requestComment.getNormativeFile();
			normativeFile.setStage(Stage.REQUEST_COMMENT_FEEDBACK);
			normativeFileDao.save(normativeFile);
			return true;
		}else{
			return false;
		}
	}
}
