package vn.laivu.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/${api.version}")
public class NotificationController {

    @GetMapping("/notifications")
    public ResponseEntity<List<Object>> fetchNotifications() {
        // Trả về danh sách rỗng tạm thời cho frontend
        return ResponseEntity.ok(new ArrayList<>());
    }

    @org.springframework.web.bind.annotation.PutMapping("/notifications/{id}")
    public ResponseEntity<Object> markReadNotification(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        return ResponseEntity.ok().build();
    }
}
