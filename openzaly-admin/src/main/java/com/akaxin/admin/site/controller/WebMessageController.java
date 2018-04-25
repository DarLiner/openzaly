package com.akaxin.admin.site.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.admin.site.bean.WebMessageBean;
import com.akaxin.admin.site.common.MsgUtils;
import com.akaxin.admin.site.service.IMessageManageService;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.google.protobuf.InvalidProtocolBufferException;

@Controller
@RequestMapping("webMessage")
public class WebMessageController extends AbstractController {

	@Resource(name = "messageManageService")
	private IMessageManageService messageService;

	@RequestMapping("/index")
	public ModelAndView toIndex() {
		return new ModelAndView("/webMsg/test");
	}

	@RequestMapping("/testU2Web")
	@ResponseBody
	public void u2WebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(
				StringHelper.format("======================siteUserId={} siteAdmin={}", siteUserId, siteAdmin));

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteAdmin));
		bean.setHeight(200);
		bean.setWidth(100);
		bean.setWebCode("<div>测试u2web消息</div>");
		bean.setSiteUserId(siteAdmin);
		bean.setSiteFriendId(siteUserId);
		bean.setMsgTime(System.currentTimeMillis());
		messageService.sendU2WebMessage(bean);

	}

	@RequestMapping("/testU2WebNotice")
	@ResponseBody
	public void u2WebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(
				StringHelper.format("======================siteUserId={} siteAdmin={}", siteUserId, siteAdmin));

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteAdmin));
		bean.setWebCode("<div>测试u2web消息</div>");
		bean.setSiteUserId(siteAdmin);
		bean.setSiteFriendId(siteUserId);
		bean.setMsgTime(System.currentTimeMillis());
		messageService.sendU2WebNoticeMessage(bean);
	}

	@RequestMapping("/testGroupWeb")
	@ResponseBody
	public void groupWebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(StringHelper.format("======================siteUserId={}", siteUserId));

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteUserId));
		bean.setHeight(200);
		bean.setWidth(100);
		bean.setWebCode("<div>测试GroupWeb消息</div>");
		bean.setSiteUserId(siteUserId);
		bean.setSiteGroupId("10000");
		bean.setMsgTime(System.currentTimeMillis());
		messageService.sendGroupWebMessage(bean);
	}

	@RequestMapping("/testGroupWebNotice")
	@ResponseBody
	public void groupWebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(
				StringHelper.format("======================siteUserId={} siteAdmin={}", siteUserId, siteAdmin));

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteUserId));
		bean.setWebCode("<div>测试GroupWeb消息</div>");
		bean.setSiteUserId(siteUserId);
		bean.setSiteGroupId("10000");
		bean.setMsgTime(System.currentTimeMillis());
		messageService.sendGroupWebNoticeMessage(bean);
	}
}
