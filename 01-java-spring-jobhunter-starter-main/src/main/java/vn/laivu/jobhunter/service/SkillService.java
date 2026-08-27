package vn.laivu.jobhunter.service;
import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.skill.ResSkillDTO;
import vn.laivu.jobhunter.unity.Skill;

import java.util.List;

@Service
public interface SkillService {

    List<ResSkillDTO> fetchAllSkill();

    Skill createSkill(Skill skill);
}
