package vn.laivu.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.service.SubscriberService;
import vn.laivu.jobhunter.unity.Subscriber;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/${api.version}")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a new subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isExist = this.subscriberService.isExistEmail(subscriber.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@Valid @RequestBody Subscriber reqSub) throws IdInvalidException {
        Subscriber subDB = this.subscriberService.findById(reqSub.getId());
        if (subDB == null) {
            throw new IdInvalidException("ID " + reqSub.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.update(subDB, reqSub));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

    @PostMapping("/subscribers/send-email")
    @ApiMessage("Send job email to current subscriber")
    public ResponseEntity<Void> sendSubscribersEmail() throws IdInvalidException {
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
        return ResponseEntity.ok().body(null);
    }
}

