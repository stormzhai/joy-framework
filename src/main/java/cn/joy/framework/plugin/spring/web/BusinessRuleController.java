package cn.joy.framework.plugin.spring.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.spring.SpringResource;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
/**
 * 通用业务规则接口调用控制器，负责客户端调用服务器提供的服务接口规则方法
 * @author liyy
 * @date 2014-06-12
 */
public class BusinessRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(BusinessRuleController.class);
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "";
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}
		
		if(SpringResource.getSecurityManager()!=null){
			RuleResult checkResult = SpringResource.getSecurityManager().checkBusinessRequest(request);
			if(!checkResult.isSuccess()){
				Map<String, Object> checkResultContent = checkResult.getMapFromContent();
				if(checkResultContent.containsKey("statusCode"))
					response.setStatus(NumberKit.getInteger(checkResultContent.get("statusCode"), 500));
				
				HttpKit.writeResponse(response, checkResult.getMsg());
				return null;
			}
		}
		
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		String isMergeRequest = RuleKit.getStringParam(request, "imr");
		if(logger.isDebugEnabled())
			logger.debug("isMergeRequest="+isMergeRequest);
		if("y".equals(isMergeRequest)){
			String mergeKey = RuleKit.getStringParam(request, "mk");
			if(logger.isDebugEnabled())
				logger.debug("mergeKey="+mergeKey+", keyValues="+RuleKit.getStringParam(request, mergeKey));
			String[] keyValues = RuleKit.getStringParam(request, mergeKey).split(",");
			Map<String, RuleResult> mergeResult = new HashMap<String, RuleResult>();
			for(String kv:keyValues){
				if(StringKit.isEmpty(kv))
					continue;
				request.setAttribute("MK_"+mergeKey, kv);
				//执行分离，事务分离，合并结果
				RuleResult result = null;
				try{
					result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
				}catch(RuleException e){
					result = e.getFailResult();
				}
				//content = result.toJSON();
				if(logger.isDebugEnabled())
					logger.debug("kv="+kv+", content="+result.toJSON());
				mergeResult.put(kv, result);
				RuleExecutor.clearCurrentExecutor();
			}
			HttpKit.writeResponse(response, JsonKit.object2Json(mergeResult));
		}else{
			RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
			content = result.toJSON();
			HttpKit.writeResponse(response, content);
			RuleExecutor.clearCurrentExecutor();
		}
		
		return null;
	}
}
