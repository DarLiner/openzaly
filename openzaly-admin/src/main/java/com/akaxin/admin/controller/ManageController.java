package com.akaxin.admin.controller;

import com.akaxin.admin.service.ManageService;
import com.akaxin.admin.service.impl.ManageServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Set;

import static com.akaxin.proto.core.ConfigProto.ConfigKey.*;

@Controller
@RequestMapping("manage")
public class ManageController {
    private ManageService manageService = new ManageServiceImpl();

    @RequestMapping("/index")
    public String defaultHTML() {

        return "platform/index";
    }

    @RequestMapping("/basic")
    public ModelAndView toSetBasic() {
        ModelAndView modelAndView = new ModelAndView("platform/basic/setBasic");
        Map<String, Object> model = modelAndView.getModel();
        Map<Integer, String> map = manageService.getSiteConfig();
        Set<Integer> integers = map.keySet();
        String site_prot = "";
        String site_address = "";
        String http_prot = "";
        String http_address = "";
        for (Integer integer : integers) {
            String res = map.get(integer);
            //设置默认的属性

            switch (integer) {
                case SITE_NAME_VALUE:
                    model.put("site_name", res);
                    break;
                case SITE_ADDRESS_VALUE:
                    site_address = res;
                    break;
                case SITE_PORT_VALUE:
                    site_prot = res;
                    break;
                case SITE_HTTP_ADDRESS_VALUE:
                    http_address = res;
                    break;
                case SITE_HTTP_PORT_VALUE:
                    http_prot = res;
                    break;
                case SITE_LOGO_VALUE:
                    model.put("site_logo", res);
                    break;
                case SITE_INTRODUCTION_VALUE:
                    model.put("site_desc", res);
                    break;
                case REGISTER_WAY_VALUE:
                    model.put("site_register_way", res);
                    break;
                case PIC_SIZE_VALUE:
                    model.put("pic_size", res);
                    break;
                case PIC_PATH_VALUE:
                    model.put("pic_path", res);
                    break;
                case GROUP_MEMBERS_COUNT_VALUE:
                    model.put("group_members_count", res);
                    break;
                case U2_ENCRYPTION_STATUS_VALUE:
                    model.put("u2_encryption_status", res);
                    break;
                case PUSH_CLIENT_STATUS_VALUE:
                    model.put("push_client_status", res);
                    break;
                case LOG_LEVEL_VALUE:
                    model.put("log_level", res);
                    break;
                case SITE_MANAGER_VALUE:
                    model.put("subgenus_admin", res);
                    break;
            }


        }
        model.put("siteAddressAndPort", site_address + ":" + site_prot);
        model.put("httpAddressAndPort", http_address + ":" + http_prot);
        return modelAndView;
    }
}
