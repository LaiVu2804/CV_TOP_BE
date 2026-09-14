package vn.laivu.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.laivu.jobhunter.service.EmailService;
import vn.laivu.jobhunter.service.SubscriberService;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;

@RestController
@RequestMapping("/api/${api.version}")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/send-simple-email")
    @ApiMessage("Send test simple email")
    public String sendSimpleEmail() {
        emailService.sendSimpleEmail();
        return "OK";
    }

    @GetMapping("/send-email-sync")
    @ApiMessage("Send Email Sync")
    public String sendEmailSync() {
        emailService.sendEmailSync(
                "laivu4864@gmail.com",
                "Test send email",
                "<h1><b>Hello</b></h1>",
                false,
                true);
        return "OK";
    }


    @GetMapping("/email")
    @ApiMessage("Send Email Sync")
    // @Transactional // tạo session vì hành động scheduled là tự động
    // @Scheduled(cron = "*/30 * * * * *") // 60s chạy 1 lần
    public String sendEmailFromTemplateSync() {
        this.emailService.sendEmailFromTemplateSync("laivu4864@gmail.com","Test send email","job");
        return "OK";
    }


//    @GetMapping("/email")
//    @ApiMessage("Send Email Sync")
//    // @Transactional // tạo session vì hành động scheduled là tự động
//    // @Scheduled(cron = "*/30 * * * * *") // 60s chạy 1 lần
//    public String sendEmailFromTemplateSync() {
//        this.subscriberService.sendSubscribersEmailJobs();
//        return "OK";
//    }
}
