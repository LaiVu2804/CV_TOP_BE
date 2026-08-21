package vn.ngotien.jobhunter.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngotien.jobhunter.domain.response.ApiResponse;
import vn.ngotien.jobhunter.domain.response.skill.ResSkillDTO;
import vn.ngotien.jobhunter.service.SkillService;
import vn.ngotien.jobhunter.unity.Skill;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createJob(@Valid @RequestBody Skill postSkill) {
        Skill skill = this.skillService.createSkill(postSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<ResSkillDTO>>> getAllSKill() {

        var result = new ApiResponse<List<ResSkillDTO>>(HttpStatus.OK, "Show skills", skillService.fetchAllSkill(), null);

        return ResponseEntity.ok().body(result);
    }
}
