package vn.laivu.jobhunter.service.ServiceImpl;

import org.springframework.stereotype.Service;
import vn.laivu.jobhunter.domain.response.skill.ResSkillDTO;
import vn.laivu.jobhunter.repository.SkillRepository;
import vn.laivu.jobhunter.service.SkillService;
import vn.laivu.jobhunter.unity.Skill;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public List<ResSkillDTO> fetchAllSkill() {
        // 1. Lấy danh sách gốc từ Database (giả sử dùng repository)
        List<Skill> listSkills = skillRepository.findAll();

        // 2. Chuyển đổi toàn bộ danh sách sang DTO
        return listSkills.stream().map(skill -> {
            ResSkillDTO res = new ResSkillDTO();
            res.setId(skill.getId());
            res.setName(skill.getName());
            res.setCreateAt(skill.getCreatedAt());
            res.setUpdateAt(skill.getUpdatedAt());
            return res;
        }).collect(Collectors.toList());
    }

}
