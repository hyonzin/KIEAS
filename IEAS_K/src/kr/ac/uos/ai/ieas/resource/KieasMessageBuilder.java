package kr.ac.uos.ai.ieas.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXParseException;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Alert.MsgType;
import com.google.publicalerts.cap.Alert.Scope;
import com.google.publicalerts.cap.Alert.Status;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.CapException;
import com.google.publicalerts.cap.CapUtil;
import com.google.publicalerts.cap.CapValidator;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.CapXmlParser;
import com.google.publicalerts.cap.Group;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Info.Category;
import com.google.publicalerts.cap.Info.Certainty;
import com.google.publicalerts.cap.Info.ResponseType;
import com.google.publicalerts.cap.Info.Severity;
import com.google.publicalerts.cap.Info.Urgency;
import com.google.publicalerts.cap.NotCapException;
import com.google.publicalerts.cap.Resource;
import com.google.publicalerts.cap.ValuePair;

import kr.ac.uos.ai.ieas.db.dbModel.CAPAlert;
import kr.ac.uos.ai.ieas.db.dbModel.CAPArea;
import kr.ac.uos.ai.ieas.db.dbModel.CAPInfo;
import kr.ac.uos.ai.ieas.db.dbModel.CAPResource;
import kr.ac.uos.ai.ieas.db.dbModel.DisasterEventType;

public class KieasMessageBuilder
{
	private CapXmlBuilder 	capXmlBuilder;
	private CapXmlParser 	capXmlParser;
	private CapValidator 	capValidator;

	private Alert 			alert;
	private Info 			info;
	private Resource 		resource;
	private Area 			area;
	
	private HashMap<String, ArrayList<String>> capEnumMap;
	private String xmlMessage;
	

	public KieasMessageBuilder()  {

		this.capXmlBuilder = new CapXmlBuilder();
		this.capXmlParser = new CapXmlParser(true);
		this.capValidator = new CapValidator();
		
		this.capEnumMap = new HashMap<>();
		initAlertCapEnumMap();
		initInfoCapEnumMap();
		
		buildDefaultMessage();
	}
	
	private void initAlertCapEnumMap()
	{
		ArrayList<String> capEnum1 = new ArrayList<>();
		for (Status value : Alert.Status.values())
		{
			capEnum1.add(value.toString());
		}
		capEnumMap.put("Status", capEnum1);
		
		ArrayList<String> capEnum2 = new ArrayList<>();
		for (MsgType value : Alert.MsgType.values())
		{
			capEnum2.add(value.toString());
		}
		capEnumMap.put("MsgType", capEnum2);
		
		ArrayList<String> capEnum3 = new ArrayList<>();
		for (Scope value : Alert.Scope.values())
		{
			capEnum3.add(value.toString());
		}
		capEnumMap.put("Scope", capEnum3);	
	}
	
	private void initInfoCapEnumMap()
	{
		ArrayList<String> capEnum1 = new ArrayList<>();
		for (Category value : Info.Category.values())
		{
			capEnum1.add(value.toString());
		}
		capEnumMap.put("Category", capEnum1);
		
		ArrayList<String> capEnum2 = new ArrayList<>();
		for (Certainty value : Info.Certainty.values())
		{
			capEnum2.add(value.toString());
		}
		capEnumMap.put("Certainty", capEnum2);
		
		ArrayList<String> capEnum3 = new ArrayList<>();
		for (ResponseType value : Info.ResponseType.values())
		{
			capEnum3.add(value.toString());
		}
		capEnumMap.put("ResponseType", capEnum3);
		
		ArrayList<String> capEnum4 = new ArrayList<>();
		for (Severity value : Info.Severity.values())
		{
			capEnum4.add(value.toString());
		}
		capEnumMap.put("Severity", capEnum4);
		
		ArrayList<String> capEnum5 = new ArrayList<>();
		for (Urgency value : Info.Urgency.values())
		{
			capEnum5.add(value.toString());
		}
		capEnumMap.put("Urgency", capEnum5);
		
		ArrayList<String> capEnum6 = new ArrayList<String>();
		for (DisasterEventType value : DisasterEventType.values())
		{
			String str = value.toString() + "(" + value.getKoreanEventCode() + ")";
			capEnum6.add(str);
		}
		capEnumMap.put("EventCode", capEnum6);
		
		ArrayList<String> capEnum7 = new ArrayList<String>();
		for (String value : KieasConfiguration.IEAS_List.LANGUAGE_LIST)
		{
//			System.out.println(value);
			capEnum7.add(value);
		}
		capEnumMap.put("Language", capEnum7);
	}
	
	public HashMap<String, ArrayList<String>> getCapEnumMap()
	{
		return this.capEnumMap;
	}
	
	public void buildDefaultMessage() {
		
		this.alert = Alert.newBuilder().setXmlns(CapValidator.CAP_LATEST_XMLNS)
				.setIdentifier("Identifier")
				.setSender("Sender")
				.setSent("Sent")
				.setStatus(Alert.Status.SYSTEM)
				.setMsgType(Alert.MsgType.ALERT)
				.setScope(Alert.Scope.PUBLIC)
				.buildPartial();
		
		this.info = Info.newBuilder()
				.setLanguage("ko-KR")
				.addCategory(Info.Category.SAFETY)
				.setEvent("event")
				.setUrgency(Info.Urgency.UNKNOWN_URGENCY)
				.setSeverity(Info.Severity.UNKNOWN_SEVERITY)
				.setCertainty(Info.Certainty.UNKNOWN_CERTAINTY)
				.buildPartial();
		
		this.resource = Resource.newBuilder().buildPartial();
		
		this.area = Area.newBuilder().buildPartial();
				
		alert = Alert.newBuilder(alert).addInfo(info).build();
	}
	
	public boolean validateMessage()
	{
		try
		{
			capValidator.validateAlert(alert);
			return true;
		}
		catch (CapException e)
		{
			return false;
		}
	}
	
	public void build()
	{		
		alert = Alert.newBuilder(alert).clearInfo().addInfo(info).build();
	}	

	private String dateToString(Date date)
	{
		GregorianCalendar cal = new GregorianCalendar(SimpleTimeZone.getTimeZone("Asia/Seoul"));
		cal.setTime(date);
		return CapUtil.formatCapDate(cal);
	}
	
	public String transformToYmdhms(String date)
	{
		GregorianCalendar cal = new GregorianCalendar(SimpleTimeZone.getTimeZone("Asia/Seoul"));
		cal.setTime(CapUtil.toJavaDate(date));
		
		StringBuffer sb = new StringBuffer();
		sb.append(cal.get(Calendar.YEAR)).append("년")
		.append(cal.get(Calendar.MONTH)+1).append("월")
		.append(cal.get(Calendar.DATE)).append("일").append(" ")
		.append(cal.get(Calendar.HOUR_OF_DAY)).append("시")
		.append(cal.get(Calendar.MINUTE)).append("분")
		.append(cal.get(Calendar.SECOND)).append("초");
		
		return sb.toString();
	}
		
	public int getInfoCount()
	{
		return alert.getInfoCount();
	}
	
	public String getMessage()
	{
		try 
		{
			this.xmlMessage = capXmlBuilder.toXml(alert);
			return xmlMessage;
		}
		catch (NotCapException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Alert setMessage(String message) 
	{
		try
		{
			alert = capXmlParser.parseFrom(message);
			return alert;
		} 
		catch (NotCapException | SAXParseException | CapException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
	public String getIdentifier()
	{
		try
		{
			return alert.getIdentifier();
		}
		catch (NotCapException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getSender()
	{
		try 
		{
			return alert.getSender();

		} 
		catch (NotCapException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String getSent()
	{
		try
		{
			return alert.getSent();

		} 
		catch (NotCapException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSentCalendar()
	{
		try
		{
			return alert.getSent();

		} 
		catch (NotCapException e)
		{
			e.printStackTrace();
		}
		return null;
	}	

	public String getStatus()
	{
		return alert.getStatus().toString();
	}

	public String getMsgType() 
	{
		return alert.getMsgType().toString();
	}

	public String getSource() 
	{
		return alert.getSource().toString();
	}
	
	public String getScope()
	{
		return alert.getScope().toString();
	}
	
	public String getRestriction()
	{
		return alert.getRestriction().toString();
	}

	public String getAddresses()
	{
		try
		{
			return alert.getAddresses().getValue(0);
		}
		catch (NotCapException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCode()
	{
		return alert.getCode(0).toString();
	}		

	public String getLanguage()
	{
		return alert.getInfo(0).getLanguage().toString();
	}

	public String getCategory()
	{
		return alert.getInfo(0).getCategory(0).toString();
	}

	public String getEvent()
	{
		try
		{
			return alert.getInfo(0).getEvent();
		}
		catch (NotCapException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String getUrgency()
	{
		return alert.getInfo(0).getUrgency().toString();
	}

	public String getSeverity()
	{
		return alert.getInfo(0).getSeverity().toString();
	}

	public String getCertainty()
	{
		return alert.getInfo(0).getCertainty().toString();
	}

	public String getEventCode() 
	{
		return alert.getInfo(0).getEventCodeList().get(0).getValue().toString();
	}

	public String getEffective()
	{
		return alert.getInfo(0).getEffective().toString();
	}

	public String getSenderName()
	{
		return alert.getInfo(0).getSenderName().toString();
	}

	public String getHeadline()
	{
		return alert.getInfo(0).getHeadline().toString();
	}

	public String getDescrpition()
	{
		return alert.getInfo(0).getDescription().toString();
	}

	public String getWeb()
	{
		return alert.getInfo(0).getWeb().toString();
	}

	public String getContact()
	{
		return alert.getInfo(0).getContact().toString();
	}

	
	
	



	
	

	
	
	
	public void setIdentifier(String text)
	{
		alert = Alert.newBuilder(alert).setIdentifier(text).build();
	}	
	
	public void setSender(String sender)
	{
		alert = Alert.newBuilder(alert).setSender(sender).build();
	}
	
	public void setSent(String text)
	{
		alert = Alert.newBuilder(alert).setSent(text).build();
	}
	
	public void setSent(GregorianCalendar cal)
	{
		alert = Alert.newBuilder(alert).setSent(CapUtil.formatCapDate(cal)).build();
	}	

	public Status setStatus(String text)
	{
		text = text.toUpperCase();
		for (Status status : Alert.Status.values())
		{
			if(text.equals(status.toString()))
			{
				alert = Alert.newBuilder(alert).setStatus(status).build();
				return status;		
			}
		}
		return null;
	}

	public MsgType setMsgType(String text)
	{
		for (MsgType msgType : Alert.MsgType.values())
		{
			if(text.toUpperCase().equals(msgType.toString()))
			{
				alert = Alert.newBuilder(alert).setMsgType(msgType).build();		
				return msgType;
			}
		}
		return null;
	}

	public void setSource(String source) 
	{
		alert = Alert.newBuilder(alert).setSource(source).build();
	}
	
	public Scope setScope(String text)
	{
		for (Scope scope : Alert.Scope.values())
		{
			if(text.toUpperCase().equals(scope.toString()))
			{
				alert = Alert.newBuilder(alert).setScope(scope).build();		
				return scope;
			}
		}
		return null;
	}
	
	public void setAddresses(String string) 
	{
		alert = Alert.newBuilder(alert).setAddresses(Group.newBuilder().addValue(string).build()).build();		
	}

	public void setCode(String code) 
	{
		alert = Alert.newBuilder(alert).setCode(0, code).build();
	}

	public void setLanguage(String text)
	{
		info = Info.newBuilder(info).setLanguage(text).build();
	}

	public Category setCategory(String text)
	{
		for (Category category : Info.Category.values())
		{
			if(text.toUpperCase().equals(category.toString()))
			{
				info = Info.newBuilder(info).setCategory(0, category).build();				
				return category;
			}
		}
		return null;
	}
		
	public void setEvent(String event) 
	{
		info = Info.newBuilder(info).setEvent(event).build();
	}

	public Urgency setUrgency(String text)
	{
		for (Urgency urgency : Info.Urgency.values())
		{
			if(text.toUpperCase().equals(urgency.toString()))
			{
				info = Info.newBuilder(info).setUrgency(urgency).build();				
				return urgency;
			}
		}
		return null;
	}
	
	public Severity setSeverity(String text) 
	{
		for (Severity severity : Info.Severity.values())
		{
			if(text.toUpperCase().equals(severity.toString()))
			{
				info = Info.newBuilder(info).setSeverity(severity).build();				
				return severity;
			}
		}
		return null;
	}
	
	public Certainty setCertainty(String text)
	{
		for (Certainty certainty : Info.Certainty.values())
		{
			if(text.toUpperCase().equals(certainty.toString()))
			{
				info = Info.newBuilder(info).setCertainty(certainty).build();				
				return certainty;
			}
		}
		return null;
	}

	public void setEventCode(String text)
	{
		info = Info.newBuilder(info).setEventCode(0, Info.newBuilder().addEventCodeBuilder().setValueName("?먯뿰?щ궃").setValue(text).build()).build();
	}

	public void setEffective(GregorianCalendar cal)
	{
		info = Info.newBuilder(info).setEffective(CapUtil.formatCapDate(cal)).build();
	}

	public void setSenderName(String senderName)
	{
		info = Info.newBuilder(info).setSenderName(senderName).build();
	}

	public void setWeb(String web)
	{
		info = Info.newBuilder(info).setWeb(web).build();
	}

	public void setContact(String contact)
	{
		info = Info.newBuilder(info).setContact(contact).build();
	}

	public void setHeadline(String headline) {
		info = Info.newBuilder(info).setHeadline(headline).build();
	}

	public void setDescription(String description) {
		info = Info.newBuilder(info).setDescription(description).build();
	}
	
	
	private String getValueInJasonObject(String jsonInput) {
		
		try {
			JSONObject jsonObj = new JSONObject(jsonInput);
			return jsonObj.getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> databaseObjectToCapLibraryObject(ArrayList<CAPAlert> alertList) {
		
		ArrayList<String> capList = new ArrayList<String>();
		
		for (CAPAlert capAlert : alertList)
		{
			Alert alert = Alert.newBuilder().setXmlns(CapValidator.CAP_LATEST_XMLNS)
					.setIdentifier(capAlert.getIdentifier())
					.setSender(capAlert.getSender())
					.setSent(this.dateToString(capAlert.getSent()))
					.setStatus(this.setStatus(capAlert.getStatus().toString()))
					.setMsgType(this.setMsgType(capAlert.getMsgType().toString()))
					.setScope(this.setScope(capAlert.getScope()))
//					.addCode(capAlert.getCode())
					.buildPartial();

			for (CAPInfo capInfo : capAlert.getInfoList())
			{
				Info info = Info.newBuilder()
						.setLanguage(capInfo.getLanguage().toString())
						.addCategory(this.setCategory(capInfo.getCategory().toString()))
						.setEvent(capInfo.getEvent().toString())
						.setUrgency(this.setUrgency(capInfo.getUrgency().toString()))
						.setSeverity(this.setSeverity(capInfo.getSeverity().toString()))
						.setCertainty(this.setCertainty(capInfo.getCertainty().toString()))
						.addEventCode(Info.newBuilder().addEventCodeBuilder().setValueName("TTAS.KO-07.0046/R5 재난 종류 코드").setValue(getValueInJasonObject(capInfo.getEventCode())).build())
//						.setEffective(this.dateToString(capInfo.getEffective()))
						.setSenderName(capInfo.getSenderName())
						.setHeadline(capInfo.getHeadline())
						.setDescription(capInfo.getDescription())
						.setWeb(capInfo.getWeb())
						.setContact(capInfo.getContact())
						.buildPartial();
				

				for (CAPResource capResource : capInfo.getResList())
				{
					Resource resource = Resource.newBuilder()
							.setResourceDesc(capResource.getResourceDesc())
							.setMimeType(capResource.getMimeType())
							.setSize((long)capResource.getSize())
							.setUri(capResource.getUri())
//							.setDerefUri(capResource.getDeferURI())
//							.setDigest(capResource.getDigest().toString())
							.buildPartial();
					
					info = Info.newBuilder(info)
							.addResource(resource)
							.buildPartial();
				}
				for (CAPArea capArea : capInfo.getAreaList())
				{
					Area area = Area.newBuilder()
							.setAreaDesc(capArea.getAreaDesc())
//							.addGeocode(ValuePair.newBuilder().setValueName("G1").setValue(capArea.getGeocode()).build())
							.buildPartial();
					
					info = Info.newBuilder(info)
							.addArea(area)
							.buildPartial();
				}
				alert = Alert.newBuilder(alert)
						.addInfo(info)
						.build();
			}			
			capList.add(capXmlBuilder.toXml(alert));
		}
		return capList;
	}
}
