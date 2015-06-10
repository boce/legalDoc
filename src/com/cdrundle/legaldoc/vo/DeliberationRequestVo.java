package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.DeliberationRequest;
import com.cdrundle.legaldoc.enums.Status;


/**
 * @author  XuBao
 *
 * 2014年6月9日
 */
public class DeliberationRequestVo  implements  Serializable {
				/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
				
				/**
				 * 审议申请Vo对象Id
				 */
				private  Long  id;

				/**
				 * 审议申请单文件名
				 */
			    private  String  name;

				/**
				 * 规范新文件名
				 */
				private  NormativeFileVo  normativeFile;

				/**
				 * 主起草单位
				 */
				private  OrgShortVo  draftingUnit;

				/**
				 * 主起草单位负责人
				 */
				private  UserShortVo  draftingUnitLeader;

				/**
				 * 主起草单位经办员
				 */
				private  UserShortVo  draftingUnitClerk;
				
				/**
				 * 联合起草单位
				 */
				private   String   unionDraUnit;

				/**
				 * 联合起草单位经办员
				 */
				private  String   unionDraUnitClerk;

				/**
				 * 联合起草单位负责人
				 */
				private  String  unionDraUnitLeader;

				/**
				 * 文件状态
				 */
				private  Status  status;

				/**
				 * 审议单位
				 */
				private  String  deliberationUnit;

				/**
				 * 报请日期
				 */
				private  Date   requestDate;

				/**
				 * 草案
				 */
				private  String protocol;

				/**
				 * 起草说明
				 */
				private  String  draftingInstruction;

				/**
				 * 征求意见的相关材料
				 */
				private  String  requestComments;

				/**
				 * 法律审查意见书
				 */
				private  String   reviewComments;

				/**
				 * 审议请示
				 */
				private  String   reviewInstruction;
				
				/**
				 * 联合单位名称
				 */
				private String  unionDrtUnitName;
				
				/**
				 * 联合单位负责人名称
				 */
				private String  unionDrtUnitLeaderName;
					
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

				public OrgShortVo getDraftingUnit()
				{
					return draftingUnit;
				}

				public void setDraftingUnit(OrgShortVo draftingUnit)
				{
					this.draftingUnit = draftingUnit;
				}

				public UserShortVo getDraftingUnitLeader()
				{
					return draftingUnitLeader;
				}

				public void setDraftingUnitLeader(UserShortVo draftingUnitLeader)
				{
					this.draftingUnitLeader = draftingUnitLeader;
				}

				public UserShortVo getDraftingUnitClerk()
				{
					return draftingUnitClerk;
				}

				public void setDraftingUnitClerk(UserShortVo draftingUnitClerk)
				{
					this.draftingUnitClerk = draftingUnitClerk;
				}

				public String getUnionDraUnit()
				{
					return unionDraUnit;
				}

				public void setUnionDraUnit(String unionDraUnit)
				{
					this.unionDraUnit = unionDraUnit;
				}

				public String getUnionDraUnitClerk()
				{
					return unionDraUnitClerk;
				}

				public void setUnionDraUnitClerk(String unionDraUnitClerk)
				{
					this.unionDraUnitClerk = unionDraUnitClerk;
				}

				public String getUnionDraUnitLeader()
				{
					return unionDraUnitLeader;
				}

				public void setUnionDraUnitLeader(String unionDraUnitLeader)
				{
					this.unionDraUnitLeader = unionDraUnitLeader;
				}

				public Status getStatus()
				{
					return status;
				}

				public void setStatus(Status status)
				{
					this.status = status;
				}

				public String getDeliberationUnit()
				{
					return deliberationUnit;
				}

				public void setDeliberationUnit(String deliberationUnit)
				{
					this.deliberationUnit = deliberationUnit;
				}

				public Date getRequestDate()
				{
					return requestDate;
				}

				public void setRequestDate(Date requestDate)
				{
					this.requestDate = requestDate;
				}

				public String getProtocol()
				{
					return protocol;
				}

				public void setProtocol(String protocol)
				{
					this.protocol = protocol;
				}

				public String getDraftingInstruction()
				{
					return draftingInstruction;
				}

				public void setDraftingInstruction(String draftingInstruction)
				{
					this.draftingInstruction = draftingInstruction;
				}

				public String getRequestComments()
				{
					return requestComments;
				}

				public void setRequestComments(String requestComments)
				{
					this.requestComments = requestComments;
				}

				public String getReviewComments()
				{
					return reviewComments;
				}

				public void setReviewComments(String reviewComments)
				{
					this.reviewComments = reviewComments;
				}

				public String getReviewInstruction()
				{
					return reviewInstruction;
				}

				public void setReviewInstruction(String reviewInstruction)
				{
					this.reviewInstruction = reviewInstruction;
				}

				public String getUnionDrtUnitName()
				{
					return unionDrtUnitName;
				}

				public void setUnionDrtUnitName(String unionDrtUnitName)
				{
					this.unionDrtUnitName = unionDrtUnitName;
				}

				public String getUnionDrtUnitLeaderName()
				{
					return unionDrtUnitLeaderName;
				}

				public void setUnionDrtUnitLeaderName(String unionDrtUnitLeaderName)
				{
					this.unionDrtUnitLeaderName = unionDrtUnitLeaderName;
				}

				/**
				 * 	将实体对象转换为Vo对象
				 * @param deliberationRequest
				 * @return   DeliberationRequestVo
				 */
				public  static  DeliberationRequestVo  createVo(DeliberationRequest deliberationRequest){
					DeliberationRequestVo  deliberationRequestVo = new  DeliberationRequestVo();
					if( deliberationRequest == null){
						return  deliberationRequestVo;
					}
					deliberationRequestVo.setId(deliberationRequest.getId());
					deliberationRequestVo.setDeliberationUnit(deliberationRequest.getDeliberationUnit()); 
					deliberationRequestVo.setDraftingInstruction(deliberationRequest.getDraftingInstruction());
					deliberationRequestVo.setDraftingUnit(OrgShortVo.createVo(deliberationRequest.getDraftingUnit()));
					deliberationRequestVo.setDraftingUnitClerk(UserShortVo.createVo(deliberationRequest.getDraftingUnitClerk()));
					deliberationRequestVo.setDraftingUnitLeader(UserShortVo.createVo(deliberationRequest.getDraftingUnitLeader()));
					deliberationRequestVo.setName(deliberationRequest.getName());
					deliberationRequestVo.setNormativeFile(NormativeFileVo.createVo(deliberationRequest.getNormativeFile()));
					deliberationRequestVo.setProtocol(deliberationRequest.getProtocol());
					deliberationRequestVo.setRequestComments(deliberationRequest.getRequestComments());
					deliberationRequestVo.setReviewInstruction(deliberationRequest.getReviewInstruction());
					deliberationRequestVo.setRequestDate(deliberationRequest.getRequestDate());
					deliberationRequestVo.setReviewComments(deliberationRequest.getReviewComments());
					deliberationRequestVo.setStatus(deliberationRequest.getStatus());
					deliberationRequestVo.setUnionDraUnit(deliberationRequest.getUnionDraUnit());
					deliberationRequestVo.setUnionDraUnitClerk(deliberationRequest.getUnionDraUnitClerk());
					deliberationRequestVo.setUnionDraUnitLeader(deliberationRequest.getUnionDraUnitLeader());
					return deliberationRequestVo;
						
				}
				
				/**
				 * 	将实体集合转换为Vo集合
				 * @param deliberationRequestList
				 * @return		deliberationRequestVoList
				 */
			public static  List<DeliberationRequestVo> createVoList(List<DeliberationRequest> deliberationRequestList){//
					List<DeliberationRequestVo>  deliberationRequestVoList = new ArrayList<DeliberationRequestVo>();
				   for (DeliberationRequest deliberationRequest : deliberationRequestList) {//将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
					   deliberationRequestVoList.add(DeliberationRequestVo.createVo(deliberationRequest));//调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
				}
				return deliberationRequestVoList;
			}
			
			
			
}
