package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.DevelopApplication;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Status;

/**
 * 立项
 * @author xiaokui.li
 *
 */
public class DevelopApplicationVo
{

	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 规范性文件
	 */
	private NormativeFileVo normativeFile;
	
	/**
	 * 申报单位
	 */
	private OrgShortVo applyOrg;
	
	/**
	 * 申报单位负责人
	 */
	private UserShortVo applyLeader;
	
	/**
	 * 申报单位经办员
	 */
	private UserShortVo applyClerk;
	
	/**
	 * 批准申请领导
	 */
	private UserShortVo approvalLeader;
	
	
	/**
	 * 拟起草时间
	 */
	private Date planDraftDate;
	
	/**
	 * 拟送审时间
	 */
	private Date planReviewDate;
	
	/**
	 * 申报时间
	 */
	private Date applyDate;
	
	/**
	 * 有效期
	 */
	private int validDate;
	
	/**
	 * 优先级
	 */
	private Priority priority = Priority.NORMAL;
	
	/**
	 * 状态
	 */
	private Status status = Status.OPEN;
	
	/**
	 * 依据最早失效日期
	 */
	private Date basisInvalidDate;
	
	/**
	 * 制定依据
	 */
	private String legalBasis;
	
	/**
	 * 制定依据
	 */
	private String legalBasisAttachment;
	
	/**
	 * 制定依据-不带附件
	 */
	private String legalBasisNoAtta;
	
	/**
	 * 制定的必要性、合法性，以及社会稳定性风险评估
	 */
	private String necessityLegalAndRisk;
	
	/**
	 * 制定的必要性、合法性，以及社会稳定性风险评估附件
	 */
	private String necessityLegalAndRiskAttachment;

	/**
	 * 拟解决的主要问题
	 */
	private String mainProblem;
	
	/**
	 * 拟解决的主要问题附件
	 */
	private String mainProblemAttachment;
	
	/**
	 * 拟确定的制度或措施，以及可行性论证
	 */
	private String planRegulationMeasureAndFeasibility;
	
	/**
	 * 拟确定的制度或措施，以及可行性论证附件
	 */
	private String planRegulationMeasureAndFeasibilityAtta;
	
	/**
	 * 涉及的部门
	 */
	private String involvedOrges;
	
	/**
	 * 申报单位负责人意见
	 */
	private String applyLeaderComment;
	
	/**
	 * 批准申请领导意见
	 */
	private String approvalLeaderComment;
	
	/**
	 * 备注
	 */
	private String remarks;
	
	/**
	 * 制定依据
	 */
	private List<LegalBasisVo> legalBasises;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public NormativeFileVo getNormativeFile()
	{
		return normativeFile;
	}

	public void setNormativeFile(NormativeFileVo normativeFile)
	{
		this.normativeFile = normativeFile;
	}

	public OrgShortVo getApplyOrg()
	{
		return applyOrg;
	}

	public void setApplyOrg(OrgShortVo applyOrg)
	{
		this.applyOrg = applyOrg;
	}

	public UserShortVo getApplyLeader()
	{
		return applyLeader;
	}

	public void setApplyLeader(UserShortVo applyLeader)
	{
		this.applyLeader = applyLeader;
	}

	public UserShortVo getApplyClerk()
	{
		return applyClerk;
	}

	public void setApplyClerk(UserShortVo applyClerk)
	{
		this.applyClerk = applyClerk;
	}

	public UserShortVo getApprovalLeader()
	{
		return approvalLeader;
	}

	public void setApprovalLeader(UserShortVo approvalLeader)
	{
		this.approvalLeader = approvalLeader;
	}

	public Date getPlanDraftDate()
	{
		return planDraftDate;
	}

	public void setPlanDraftDate(Date planDraftDate)
	{
		this.planDraftDate = planDraftDate;
	}

	public Date getPlanReviewDate()
	{
		return planReviewDate;
	}

	public void setPlanReviewDate(Date planReviewDate)
	{
		this.planReviewDate = planReviewDate;
	}

	public Date getApplyDate()
	{
		return applyDate;
	}

	public void setApplyDate(Date applyDate)
	{
		this.applyDate = applyDate;
	}

	public int getValidDate()
	{
		return validDate;
	}

	public void setValidDate(int validDate)
	{
		this.validDate = validDate;
	}

	public Priority getPriority()
	{
		return priority;
	}

	public void setPriority(Priority priority)
	{
		this.priority = priority;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Date getBasisInvalidDate()
	{
		return basisInvalidDate;
	}

	public void setBasisInvalidDate(Date basisInvalidDate)
	{
		this.basisInvalidDate = basisInvalidDate;
	}

	public String getLegalBasis()
	{
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis)
	{
		this.legalBasis = legalBasis;
	}

	public String getLegalBasisAttachment()
	{
		return legalBasisAttachment;
	}

	public void setLegalBasisAttachment(String legalBasisAttachment)
	{
		this.legalBasisAttachment = legalBasisAttachment;
	}

	public String getLegalBasisNoAtta() {
		return legalBasisNoAtta;
	}

	public void setLegalBasisNoAtta(String legalBasisNoAtta) {
		this.legalBasisNoAtta = legalBasisNoAtta;
	}

	public String getNecessityLegalAndRisk()
	{
		return necessityLegalAndRisk;
	}

	public void setNecessityLegalAndRisk(String necessityLegalAndRisk)
	{
		this.necessityLegalAndRisk = necessityLegalAndRisk;
	}

	public String getNecessityLegalAndRiskAttachment()
	{
		return necessityLegalAndRiskAttachment;
	}

	public void setNecessityLegalAndRiskAttachment(
			String necessityLegalAndRiskAttachment)
	{
		this.necessityLegalAndRiskAttachment = necessityLegalAndRiskAttachment;
	}

	public String getMainProblem()
	{
		return mainProblem;
	}

	public void setMainProblem(String mainProblem)
	{
		this.mainProblem = mainProblem;
	}

	public String getMainProblemAttachment()
	{
		return mainProblemAttachment;
	}

	public void setMainProblemAttachment(String mainProblemAttachment)
	{
		this.mainProblemAttachment = mainProblemAttachment;
	}

	public String getPlanRegulationMeasureAndFeasibility()
	{
		return planRegulationMeasureAndFeasibility;
	}

	public void setPlanRegulationMeasureAndFeasibility(
			String planRegulationMeasureAndFeasibility)
	{
		this.planRegulationMeasureAndFeasibility = planRegulationMeasureAndFeasibility;
	}

	public String getPlanRegulationMeasureAndFeasibilityAtta()
	{
		return planRegulationMeasureAndFeasibilityAtta;
	}

	public void setPlanRegulationMeasureAndFeasibilityAtta(
			String planRegulationMeasureAndFeasibilityAtta)
	{
		this.planRegulationMeasureAndFeasibilityAtta = planRegulationMeasureAndFeasibilityAtta;
	}

	public String getInvolvedOrges()
	{
		return involvedOrges;
	}

	public void setInvolvedOrges(String involvedOrges)
	{
		this.involvedOrges = involvedOrges;
	}

	public String getApplyLeaderComment()
	{
		return applyLeaderComment;
	}

	public void setApplyLeaderComment(String applyLeaderComment)
	{
		this.applyLeaderComment = applyLeaderComment;
	}

	public String getApprovalLeaderComment()
	{
		return approvalLeaderComment;
	}

	public void setApprovalLeaderComment(String approvalLeaderComment)
	{
		this.approvalLeaderComment = approvalLeaderComment;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}
	
	public List<LegalBasisVo> getLegalBasises() {
		return legalBasises;
	}

	public void setLegalBasises(List<LegalBasisVo> legalBasises) {
		this.legalBasises = legalBasises;
	}

	/**
	 * 实体转VO
	 * @param developApplication
	 * @return
	 */
	public static DevelopApplicationVo createVo(DevelopApplication developApplication)
	{
		DevelopApplicationVo developApplicationVo = new DevelopApplicationVo();
		if(developApplication == null)
		{
			return developApplicationVo;
		}
		developApplicationVo.setId(developApplication.getId());
		developApplicationVo.setName(developApplication.getName());
		developApplicationVo.setNormativeFile(NormativeFileVo.createVo(developApplication.getNormativeFile()));
		developApplicationVo.setApplyOrg(OrgShortVo.createVo(developApplication.getApplyOrg()));
		developApplicationVo.setApplyLeader(UserShortVo.createVo(developApplication.getApplyLeader()));
		developApplicationVo.setApplyClerk(UserShortVo.createVo(developApplication.getApplyClerk()));
		developApplicationVo.setApprovalLeader(UserShortVo.createVo(developApplication.getApprovalLeader()));
		developApplicationVo.setPlanDraftDate(developApplication.getPlanDraftDate());
		developApplicationVo.setPlanReviewDate(developApplication.getPlanReviewDate());
		developApplicationVo.setApplyDate(developApplication.getApplyDate());
		developApplicationVo.setValidDate(developApplication.getValidDate());
		developApplicationVo.setPriority(developApplication.getPriority());
		developApplicationVo.setStatus(developApplication.getStatus());
		developApplicationVo.setBasisInvalidDate(developApplication.getBasisInvalidDate());
		developApplicationVo.setLegalBasisAttachment(developApplication.getLegalBasisAttachment());
		developApplicationVo.setNecessityLegalAndRisk(developApplication.getNecessityLegalAndRisk());
		developApplicationVo.setNecessityLegalAndRiskAttachment(developApplication.getNecessityLegalAndRiskAttachment());
		developApplicationVo.setMainProblem(developApplication.getMainProblem());
		developApplicationVo.setMainProblemAttachment(developApplication.getMainProblemAttachment());
		developApplicationVo.setPlanRegulationMeasureAndFeasibility(developApplication.getPlanRegulationMeasureAndFeasibility());
		developApplicationVo.setPlanRegulationMeasureAndFeasibilityAtta(developApplication.getPlanRegulationMeasureAndFeasibilityAtta());
		developApplicationVo.setInvolvedOrges(developApplication.getInvolvedOrges());
		developApplicationVo.setApplyLeaderComment(developApplication.getApplyLeaderComment());
		developApplicationVo.setApprovalLeaderComment(developApplication.getApprovalLeaderComment());
		developApplicationVo.setRemarks(developApplication.getRemarks());
		developApplicationVo.setLegalBasises(LegalBasisVo.createVoList(developApplication.getLegalBasises()));
		return developApplicationVo;
	}
	
	/**
	 * 实体集合转vo集合
	 * @param developApplicationList
	 * @return
	 */
	public static List<DevelopApplicationVo> createVoList(List<DevelopApplication> developApplicationList)
	{
		List<DevelopApplicationVo> developApplicationVoList = new ArrayList<>();
		for (Iterator<DevelopApplication> iterator = developApplicationList.iterator(); iterator.hasNext();)
		{
			DevelopApplication developApplication = iterator.next();
			developApplicationVoList.add(createVo(developApplication));
		}
		return developApplicationVoList;
	}
	
}
