package com.akaxin.admin.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.google.protobuf.InvalidProtocolBufferException;

@Controller
@RequestMapping("webMessage")
public class WebMessageController extends AbstractController {

	@RequestMapping("/index")
	public String toIndex() {
		return "/webMsg/test";
	}

	@RequestMapping("/testU2Web")
	public void u2WebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);

		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(StringHelper.format("siteUserId={} siteAdmin={}", siteUserId, siteAdmin));

	}

	@RequestMapping("/testGroupWeb")
	public void groupWebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(StringHelper.format("siteUserId={} siteAdmin={}", siteUserId, siteAdmin));
	}

	@RequestMapping("/testU2WebNotice")
	@ResponseBody
	public void u2WebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(StringHelper.format("siteUserId={} siteAdmin={}", siteUserId, siteAdmin));
	}

	@RequestMapping("/testGroupWebNotice")
	@ResponseBody
	public void groupWebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		System.out.println(StringHelper.format("siteUserId={} siteAdmin={}", siteUserId, siteAdmin));
	}
}
