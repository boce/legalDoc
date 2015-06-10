package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.SignAndPublishVo;

/**
 * @author XuBao 签署发布 2014年6月19日
 */
public interface ISignAndPublishService {

	/**
	 * 签署发布和规范性文件的保存与更新
	 * 
	 * @param deliberationRequestVo
	 * @param path
	 *            文件路径
	 * @param content
	 *            写入Word内的内容
	 * @return DeliberationRequestVo
	 * @throws ServiceException
	 * @throws SchedulerException 
	 */
	public SignAndPublishVo saveOrUpdate(SignAndPublishVo signAndPublishVo, String path, String fileName) throws ServiceException, SchedulerException;

	/**
	 * 删除签署发布和规范文件内的签署领导，签署日期，发布日期，发文号，有效期，失效日期，规范性文件内容
	 * 
	 * @param id
	 * @param path
	 *            文件路径
	 * @throws ServiceException
	 * @throws SchedulerException 
	 */
	public boolean delete(Long id, String path) throws ServiceException, SchedulerException;

	/**
	 * 文件的提交
	 * 
	 * @return boolean
	 */
	public boolean submit(SignAndPublishVo signAndPublishVo);

	/**
	 * 文件的审核
	 * 
	 * @return boolean
	 */
	public boolean approve(SignAndPublishVo signAndPublishVo);

	/**
	 * 文件的弃审
	 * 
	 * @return boolean
	 */
	public boolean unApprove(SignAndPublishVo signAndPublishVo);

	/**
	 * 当前文件的流程
	 * 
	 * @return boolean
	 */
	public boolean flow(SignAndPublishVo signAndPublishVo);

	/**
	 * 查找所有的签署发布文件
	 * 
	 * @param page
	 *            当前页数
	 * @param size
	 *            每页的记录条数
	 * @return page
	 */
	public Page<SignAndPublishVo> find(int page, int size, String name, Set<Long> orgIds);

	/**
	 * 将定稿的规范性文件发布到法制网或公众信息网
	 * 
	 * @param recordReview
	 * @return
	 */
	public boolean publish(SignAndPublishVo signAndPublishVo);

	/**
	 * 通过文件名查找
	 * 
	 * @param name
	 * @return
	 */
	public SignAndPublishVo findSignAndPublishByName(String name);

	/**
	 * 通过Id查很早
	 * 
	 * @param id
	 * @return
	 */
	public SignAndPublishVo findById(Long id);

	/**
	 * 通过规范性文件id查找
	 * 
	 * @param id
	 * @return
	 */
	public SignAndPublishVo findByNorId(Long id);
}
