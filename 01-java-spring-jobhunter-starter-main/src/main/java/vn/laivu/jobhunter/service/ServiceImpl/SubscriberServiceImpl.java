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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        // check email
        boolean isExistEmail = this.subscriberRepository.existsByEmail(subs.getEmail());
        if (isExistEmail) {
            return null;
        }

        // check skills
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills()
                    .stream().map(Skill::getId)
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
                    .stream().map(Skill::getId)
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
        res.setLocation(job.getLocation());
        if (job.getCompany() != null) {
            res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        }
        if (job.getSkills() != null) {
            List<ResEmailJob.SkillEmail> s = job.getSkills().stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                    .collect(Collectors.toList());
            res.setSkills(s);
        } else {
            res.setSkills(new ArrayList<>());
        }
        return res;
    }

    public void sendEmailToSingleSubscriber(Subscriber sub) {
        if (sub == null) {
            return;
        }
        List<Skill> listSkills = sub.getSkills();

        if (listSkills != null && !listSkills.isEmpty()) {
            List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);

            if (listJobs != null && !listJobs.isEmpty()) {
                List<ResEmailJob> arr = listJobs.stream().map(this::convertJobToSendEmail)
                        .collect(Collectors.toList());

                this.emailService.sendEmailFromTemplateSync(
                        sub.getEmail(),
                        "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                        "jobs",
                        sub.getName(),
                        arr);
            }
        }
    }

    @Transactional
    public void sendSubscribersEmailJobByEmail(String email) {
        Subscriber sub = this.subscriberRepository.findByEmail(email);
        if (sub != null) {
            this.sendEmailToSingleSubscriber(sub);
        }
    }

    @Transactional
    public void sendSubscribersEmailJobs() {
        int page = 0;
        int pageSize = 100; // Số lượng subscriber xử lý trong mỗi batch
        Page<Subscriber> pageSubs;

        do {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());
            pageSubs = this.subscriberRepository.findAll(pageable);
            List<Subscriber> listSubs = pageSubs.getContent();

            if (listSubs != null && !listSubs.isEmpty()) {
                for (Subscriber sub : listSubs) {
                    this.sendEmailToSingleSubscriber(sub);
                }
            }
            page++;
        } while (pageSubs.hasNext());
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

}
