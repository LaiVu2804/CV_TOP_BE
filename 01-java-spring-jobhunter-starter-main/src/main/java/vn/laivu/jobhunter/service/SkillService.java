package vn.laivu.jobhunter.service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.skill.ResSkillDTO;
import vn.laivu.jobhunter.unity.Skill;

import java.util.List;

@Service
public interface SkillService {

    Skill handleCreateSkill(Skill skill);

    boolean isNameExist(String name);

    Skill handleUpdateSkill(Skill skill);

    Skill handleGetSkillById(long id);

    ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable);

    void handleDeleteSkill(long id);
}
