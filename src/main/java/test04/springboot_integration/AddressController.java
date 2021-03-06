package test04.springboot_integration;

import javax.annotation.Resource;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试Controller
 * @author zhangqingli
 *
 */
@Controller
@RequestMapping("/test")
public class AddressController {
	
	@Resource
    private KieSession kieSession;
	
	
	@ResponseBody
    @RequestMapping("/address")
	public void test01(){
		Address address = new Address();
        address.setPostcode("99425");
        AddressCheckResult result = new AddressCheckResult();
        kieSession.insert(address);
        kieSession.insert(result);
        
        int ruleFiredCount = kieSession.fireAllRules();
        System.out.println("触发了" + ruleFiredCount + "条规则");
        
        if(result.isPostCodeResult()){
            System.out.println("规则校验通过");
        }
	}
}
