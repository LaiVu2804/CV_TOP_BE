package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.subscriber.ResEmailJob;
import vn.laivu.jobhunter.repository.JobRepository;
import vn.laivu.jobhunter.repository.SkillRepository;
import vn.laivu.jobhunter.repository.SubscriberRepository;
import vn.laivu.jobhunter.service.EmailService;
import vn.laivu.jobhunter.service.SubscriberService;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Skill;
import vn.laivu.jobhunter.unity.Subscriber;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
                                 JobRepository jobRepository,
                                 EmailService emailService
    ) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subs) {
        // check skills
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subs.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subs);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> optionalSub = this.subscriberRepository.findById(id);
        if (optionalSub.isPresent()) {
            return optionalSub.get();
        }
        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

//    public void sendSubscribersEmailJobs() {
//        List<Subscriber> listSubs = this.subscriberRepository.findAll();
//        if (listSubs != null && listSubs.size() > 0) {
//            for (Subscriber sub : listSubs) {
//                List<Skill> listSkills = sub.getSkills();
//                if (listSkills != null && listSkills.size() > 0) {
//                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
//                    if (listJobs != null && listJobs.size() > 0) {
//
//                        /* Fix bug thymeleaf exceptions TemplateInputException */
//                        List<ResEmailJob> arr = listJobs.stream().map(job -> this.convertJobToSendEmail(job))
//                                .collect(Collectors.toList());
//
//                        this.emailService.sendEmailFromTemplateSync(
//                                sub.getEmail(),
//                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
//                                "jobs",
//                                sub.getName(),
//                                // listJobs); // Giải thích ở file SubscriberService
//                                arr);
//                    }
//                }
//            }
//        }
//    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

}
