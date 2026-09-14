package vn.laivu.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.subscriber.ResEmailJob;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Subscriber;

@Service
public interface SubscriberService {

    boolean isExistEmail(String email);

    Subscriber create(Subscriber subs);

    Subscriber findById(long id);

    Subscriber update(Subscriber subsDB, Subscriber subsRequest);

//    ResEmailJob convertJobToSendEmail(Job job);
//
//    void sendSubscribersEmailJobs();

    Subscriber findByEmail(String email);
}
