package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.SignAndPublish;

/**
 * @author  XuBao
 *
 * 2014年6月10日
 */
public class SignAndPublishVo  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
			private  Long  id;
			private  String  name;
			private  NormativeFileVo  normativeFile;
			private  OrgShortVo  decisionMakingUnit;
			private  UserShortVo  decisionMakingUnitLeader;
			private  UserShortVo  decisionMakingUnitClerk;
			private  String  signLeaders;
			private  Date  signDate;
			private  String publishNo;
			private  Date  publishDate;
		    private  Integer   validDate;
			private  Date 	invalidDate;
			private String  legalDoc;
			private  OrgShortVo  draftingUnit;
			
			public OrgShortVo getDraftingUnit()
			{
				return draftingUnit;
			}
			public void setDraftingUnit(OrgShortVo draftingUnit)
			{
				this.draftingUnit = draftingUnit;
			}
			
			public Long getId()
			{
				return id;
			}
			public void setId(Long id)
			{
				this.id = id;
			}
			public void setId(long id)
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
			public OrgShortVo getDecisionMakingUnit()
			{
				return decisionMakingUnit;
			}
			public void setDecisionMakingUnit(OrgShortVo decisionMakingUnit)
			{
				this.decisionMakingUnit = decisionMakingUnit;
			}
			public UserShortVo getDecisionMakingUnitLeader()
			{
				return decisionMakingUnitLeader;
			}
			public void setDecisionMakingUnitLeader(UserShortVo decisionMakingUnitLeader)
			{
				this.decisionMakingUnitLeader = decisionMakingUnitLeader;
			}
			public UserShortVo getDecisionMakingUnitClerk()
			{
				return decisionMakingUnitClerk;
			}
			public void setDecisionMakingUnitClerk(UserShortVo decisionMakingUnitClerk)
			{
				this.decisionMakingUnitClerk = decisionMakingUnitClerk;
			}
			public String getSignLeaders()
			{
				return signLeaders;
			}
			public void setSignLeaders(String signLeaders)
			{
				this.signLeaders = signLeaders;
			}
			public Date getSignDate()
			{
				return signDate;
			}
			public void setSignDate(Date signDate)
			{
				this.signDate = signDate;
			}
			public String getPublishNo()
			{
				return publishNo;
			}
			public void setPublishNo(String publishNo)
			{
				this.publishNo = publishNo;
			}
			public Date getPublishDate()
			{
				return publishDate;
			}
			public void setPublishDate(Date publishDate)
			{
				this.publishDate = publishDate;
			}
			
			public Integer getValidDate()
			{
				return validDate;
			}
			public void setValidDate(Integer validDate)
			{
				this.validDate = validDate;
			}
			public Date getInvalidDate()
			{
				return invalidDate;
			}
			public void setInvalidDate(Date invalidDate)
			{
				this.invalidDate = invalidDate;
			}
			public String getLegalDoc()
			{
				return legalDoc;
			}
			public void setLegalDoc(String legalDoc)
			{
				this.legalDoc = legalDoc;
			}
			public  static  SignAndPublishVo  createVo(SignAndPublish signAndPublish){//将实体对象转换为Vo对象
				SignAndPublishVo   signAndPublishVo = new  SignAndPublishVo();
				
				if( signAndPublish == null){
					return signAndPublishVo;
				}
				
				signAndPublishVo.setId(signAndPublish.getId());
				signAndPublishVo.setDecisionMakingUnit(OrgShortVo.createVo(signAndPublish.getDecisionMakingUnit()));
				signAndPublishVo.setDecisionMakingUnitClerk(UserShortVo.createVo(signAndPublish.getDecisionMakingUnitClerk()));
				signAndPublishVo.setDecisionMakingUnitLeader(UserShortVo.createVo(signAndPublish.getDecisionMakingUnitLeader()));
				signAndPublishVo.setId(signAndPublish.getId());
				signAndPublishVo.setInvalidDate(signAndPublish.getInvalidDate());
				signAndPublishVo.setLegalDoc(signAndPublish.getLegalDoc());
				signAndPublishVo.setName(signAndPublish.getName());
				signAndPublishVo.setNormativeFile(NormativeFileVo.createVo(signAndPublish.getNormativeFile()));
				signAndPublishVo.setPublishNo(signAndPublish.getPublishNo());
				signAndPublishVo.setPublishDate(signAndPublish.getPublishDate());
				signAndPublishVo.setSignDate(signAndPublish.getSignDate());
				signAndPublishVo.setSignLeaders(signAndPublish.getSignLeaders());
				signAndPublishVo.setValidDate(signAndPublish.getValidDate());
				signAndPublishVo.setDraftingUnit(OrgShortVo.createVo(signAndPublish.getDraftingUnit()));
					return signAndPublishVo;
					
			}
		public static  List<SignAndPublishVo> createVoList(List<SignAndPublish> signAndPublishList){//将实体集合转换为Vo集合
				List<SignAndPublishVo>  signAndPublishVoList = new ArrayList<SignAndPublishVo>();
			   for (SignAndPublish signAndPublish : signAndPublishList) {//将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
				   signAndPublishVoList.add(SignAndPublishVo.createVo(signAndPublish));//调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
			}
			return signAndPublishVoList;
		}
		
	

}
