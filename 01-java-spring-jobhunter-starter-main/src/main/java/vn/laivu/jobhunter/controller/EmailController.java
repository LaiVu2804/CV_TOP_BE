package vn.laivu.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.laivu.jobhunter.service.SubscriberService;
import vn.laivu.jobhunter.unity.Subscriber;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/${api.version}")
public class EmailController {

    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

//Gửi cho các email tồn tại tròn database
    @GetMapping("/email")
    @ApiMessage("Send Email Sync")
    // @Transactional // tạo session vì hành động scheduled là tự động
    // @Scheduled(cron = "*/30 * * * * *") // 60s chạy 1 lần
    public String sendEmailFromTemplateSync() {
        this.subscriberService.sendSubscribersEmailJobs();
        return "OK";
    }

    //Chỉ gửi theo bearer token
    @GetMapping("/email/subscriber")
    @ApiMessage("Send email to current subscriber")
    public String sendEmailToCurrentSubscriber() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ hoặc bạn chưa đăng nhập");
        }

        Subscriber subscriber = this.subscriberService.findByEmail(email);
        if (subscriber == null) {
            throw new IdInvalidException("Không tìm thấy thông tin đăng ký (subscriber) với email: " + email);
        }

        this.subscriberService.sendSubscribersEmailJobByEmail(email);
        return "OK";
    }
}
