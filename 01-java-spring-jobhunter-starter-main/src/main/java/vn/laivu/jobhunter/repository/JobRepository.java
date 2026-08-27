package vn.laivu.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.laivu.jobhunter.unity.Job;
import vn.laivu.jobhunter.unity.Skill;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Page<Job> findAll(Specification<Job> spec, Pageable page);

    List<Job> findBySkillsIn(List<Skill> skills);
}
