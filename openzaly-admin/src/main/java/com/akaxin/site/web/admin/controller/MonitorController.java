package com.akaxin.site.web.admin.controller;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.utils.GsonUtils;
import com.akaxin.proto.client.ImStcPsnProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.FileProto;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.dao.SiteLoginDao;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.utils.FileServerUtils;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.api.IUserSessionDao;
import com.akaxin.site.storage.bean.*;
import com.akaxin.site.storage.service.MessageDaoService;
import com.akaxin.site.storage.service.UserSessionDaoService;
import com.akaxin.site.web.admin.common.Timeutils;
import com.akaxin.site.web.admin.service.IMonitorService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.akaxin.site.web.admin.common.MsgUtils.buildU2MsgId;

@Controller
@RequestMapping("monitor")
public class MonitorController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);
    private IMessageDao messageDao = new MessageDaoService();
    private IMessageDao syncDao = new MessageDaoService();

    @Autowired
    private IMonitorService monitorService;

    @RequestMapping("/index")
    public ModelAndView toMonitor(@RequestBody byte[] bodyParam) {

        ModelAndView modelAndView = new ModelAndView("monitor/index");
        try {
            PluginProto.ProxyPluginPackage pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                return modelAndView;
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("to Monitor  error", e);
        }

        Map<String, Object> model = modelAndView.getModel();

        //转换可选时间
        model.put("data_2", Timeutils.getDate(2));
        model.put("data_3", Timeutils.getDate(3));
        model.put("data_4", Timeutils.getDate(4));
        model.put("data_5", Timeutils.getDate(5));
        model.put("data_6", Timeutils.getDate(6));
        model.put("flag", "success");
        return modelAndView;
    }

    @RequestMapping("/reDisplay")
    @ResponseBody
    public MonitorBean reDisplay(@RequestBody byte[] bodyParam) {
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            if (!isManager(getRequestSiteUserId(pluginPackage))) {
                return new MonitorBean();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        Map<String, String> uicReqMap = GsonUtils.fromJson(pluginPackage.getData(), Map.class);
        Integer day = null;
        if (uicReqMap != null) {

            day = Integer.parseInt(uicReqMap.get("dayNum"));
        }
        if (day == null) {
            day = 0;
        }

        long now = System.currentTimeMillis();
        int registerNum = monitorService.queryNumRegisterPerDay(now, day);
        int messageNum = monitorService.queryNumMessagePerDay(now, day);
        int groupMsgNum = monitorService.queryGroupMessagePerDay(now, day);
        int u2MsgNum = monitorService.queryU2MessagePerDay(now, day);
        int userNum = monitorService.getSiteUserNum(now, 0);
        int groupNum = monitorService.getGroupNum(now, 0);
        int friendNum = monitorService.friendNum(now, 0);
        return new MonitorBean(registerNum, messageNum, groupMsgNum, u2MsgNum, userNum, groupNum, friendNum);
    }

    @RequestMapping("/createFriend")
    @ResponseBody
    public String createFriend(@RequestBody byte[] bodyParam) {
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String requestSiteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(requestSiteUserId)) {
                return ERROR;
            }
            Random random = new Random();
            int count = 1;
            while (true) {
                UserProfileBean bean = new UserProfileBean();
                String Id = UUID.randomUUID().toString();
                bean.setSiteUserId(Id);
                bean.setUserIdPubk(UUID.randomUUID().toString());
                int flag = Math.random() > 0.5 ? 1 : 0;
                if (flag == 0) {
                    bean.setUserName(getRandomJianHan(2) + "|" + count);
                } else {
                    bean.setUserName(getStringRandom(4) + "|" + count);
                }
                System.out.println(bean.getUserName());
                byte[] bytes = StringToBytearray("U" + count);
                String s = FileServerUtils.saveFile(bytes, "site-file/", FileProto.FileType.USER_PORTRAIT, FileProto.FileDesc.newBuilder().build());
                bean.setApplyInfo("");
                bean.setUserPhoto(s);
                bean.setPhoneId("");
                bean.setUserStatus(0);
                bean.setRegisterTime(System.currentTimeMillis());
                boolean b = SiteLoginDao.getInstance().registerUser(bean);
                System.out.println("成功--" + count);
                if (requestSiteUserId == "" && count == 1) {
                    requestSiteUserId = bean.getSiteUserId();
                    count++;
                    continue;
                }
                boolean b1 = UserFriendDao.getInstance().agreeApply(bean.getSiteUserId(), requestSiteUserId, true);
                UserFriendDao.getInstance().agreeApplyWithClear(bean.getSiteUserId(), requestSiteUserId);
                count++;
                if (count > 1000) {
                    return SUCCESS;
                }
            }
        } catch (InvalidProtocolBufferException e) {
        }
        return ERROR;
    }

    public static byte[] StringToBytearray(String base64String) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(80, 40,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.setClip(0, 0, 80, 40);
        g.setColor(Color.white);
        g.fillRect(0, 0, 80, 40);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(new Font("微软雅黑", Font.PLAIN, 32));// 设置画笔字体
        /** 用于获得垂直居中y */
        Rectangle clip = g.getClipBounds();
        FontMetrics fm = g.getFontMetrics(new Font("微软雅黑", Font.PLAIN, 32));
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int y = (clip.height - (ascent + descent)) / 2 + ascent;
        for (int i = 0; i < 6; i++) {// 256 340 0 680
            g.drawString(base64String, i * 680, y);// 画出字符串
        }
        g.dispose();
        try {
            ImageIO.write(image, "png", byteArrayOutputStream);// 输出png图片
        } catch (IOException e) {

        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String getRandomJianHan(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定義高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 獲取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 獲取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 轉成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //參數length，表示生成幾位隨機數
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //輸出字母還是數字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //輸出是大寫字母還是小寫字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    @RequestMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(@RequestBody byte[] bodyParam) {
        PluginProto.ProxyPluginPackage pluginPackage = null;
        try {
            pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
            String requestSiteUserId = getRequestSiteUserId(pluginPackage);
            if (!isManager(requestSiteUserId)) {
                return ERROR;
            }
            //获取当前用户的sessionId
            Map<Integer, String> headerMap = pluginPackage.getPluginHeaderMap();
            String sessionId = headerMap.get(PluginProto.PluginHeaderKey.CLIENT_SITE_SESSION_ID_VALUE);
            //获取当前用户的deviceId
            IUserSessionDao sessionDao = new UserSessionDaoService();
            SimpleAuthBean authBean = sessionDao.getUserSession(sessionId);
            String deviceId = authBean.getDeviceId();
            new Thread(() -> {
                List<SimpleUserBean> friendLiset = UserFriendDao.getInstance().getUserFriends(requestSiteUserId);
                int count = 1;
                while (true) {
                    for (SimpleUserBean simpleUserBean : friendLiset) {
                        U2MessageBean u2Bean = new U2MessageBean();
                        u2Bean.setMsgId(buildU2MsgId(requestSiteUserId));
                        u2Bean.setMsgType(CoreProto.MsgType.TEXT_VALUE);
                        u2Bean.setSendUserId(simpleUserBean.getUserId());
                        u2Bean.setSiteUserId(requestSiteUserId);
                        u2Bean.setContent("你好啊,我是:"+simpleUserBean.getUserName()+"这是我第 "+count+" 给你发消息");
                        u2Bean.setMsgTime(System.currentTimeMillis());
                        try {
                            messageDao.saveU2Message(u2Bean);

                            long l = 0;
                            l = syncDao.queryMaxU2MessageId(requestSiteUserId);
                            syncDao.updateU2Pointer(requestSiteUserId, deviceId, l - 1);
                            //发送psn
                            CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
                                    .setAction(CommandConst.IM_STC_PSN);
                            ImStcPsnProto.ImStcPsnRequest pshRequest = ImStcPsnProto.ImStcPsnRequest.newBuilder().build();
                            commandResponse.setParams(pshRequest.toByteArray());
                            commandResponse.setErrCode2(ErrorCode2.SUCCESS);
                            ChannelWriter.writeByDeviceId(deviceId, commandResponse);
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                    }
                    count++;
                }
            }).start();
            return SUCCESS;
        } catch (Exception e) {
        }
        return ERROR;
    }
}
