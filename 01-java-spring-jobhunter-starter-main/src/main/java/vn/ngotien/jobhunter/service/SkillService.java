package vn.ngotien.jobhunter.service;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.response.skill.ResSkillDTO;
import vn.ngotien.jobhunter.unity.Skill;

import java.util.List;

@Service
public interface SkillService {

    List<ResSkillDTO> fetchAllSkill();

    Skill createSkill(Skill skill);
}
