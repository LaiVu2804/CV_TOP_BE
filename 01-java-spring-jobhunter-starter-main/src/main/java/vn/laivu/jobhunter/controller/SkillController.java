package vn.laivu.jobhunter.controller;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.laivu.jobhunter.domain.response.ApiResponse;
import vn.laivu.jobhunter.domain.response.ResultPaginationDTO;
import vn.laivu.jobhunter.domain.response.skill.ResSkillDTO;
import vn.laivu.jobhunter.service.SkillService;
import vn.laivu.jobhunter.unity.Skill;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.error.IdInvalidException;

import java.util.List;

@RestController
@RequestMapping("/api/${api.version}")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Created new a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        boolean isSkillExist = this.skillService.isNameExist(reqSkill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill " + reqSkill.getName() + " đã tồn tại.");
        }
        return ResponseEntity.ok().body(this.skillService.handleCreateSkill(reqSkill));
    }

    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Skill> specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.fetchAllSkills(specification, pageable));
    }

    @PutMapping("/skills")
    @ApiMessage("Updated a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        // check id
        Skill findSkill = this.skillService.handleGetSkillById(reqSkill.getId());
        if (findSkill == null) {
            throw new IdInvalidException("ID Skill: " + reqSkill.getId() + " không tồn tại.");
        }

        // check name
        boolean isSkillExist = this.skillService.isNameExist(reqSkill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Tên Skill " + reqSkill.getName() + " đã tồn tại.");
        }

        findSkill.setName(reqSkill.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(findSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill findSkill = this.skillService.handleGetSkillById(id);
        if (findSkill == null) {
            throw new IdInvalidException("Skill ID: " + "" + " không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
