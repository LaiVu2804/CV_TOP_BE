package vn.laivu.jobhunter.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailFromTemplateSync(String to, String subject, String templateName, String username, Object value);
}
