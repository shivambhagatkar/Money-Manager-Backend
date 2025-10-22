package in.shivambhagatkar.moneymanager.service;

import in.shivambhagatkar.moneymanager.dto.ExpenseDTO;
import in.shivambhagatkar.moneymanager.dto.IncomeDTO;
import in.shivambhagatkar.moneymanager.entity.CategoryEntity;
import in.shivambhagatkar.moneymanager.entity.ExpenseEntity;
import in.shivambhagatkar.moneymanager.entity.IncomeEntity;
import in.shivambhagatkar.moneymanager.entity.ProfileEntity;
import in.shivambhagatkar.moneymanager.repository.CategoryRepository;
import in.shivambhagatkar.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;  // 👈 yeh add karo
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO dto) {
        // 1. Current user ka profile
        ProfileEntity profile = profileService.getCurrentProfile();
        // 2. Category validate karo
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        // 3. DTO -> Entity convert
        IncomeEntity newIncome = toEntity(dto, profile, category);
        // 4. DB me save
        newIncome = incomeRepository.save(newIncome);
        // 5. Entity -> DTO return
        return toDTO(newIncome);
    }

    //Retrives all income for current month/based on the start date and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile  = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDateTime startDate = now.withDayOfMonth(1).atStartOfDay();;
        LocalDateTime endDate = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenWithCategory(
                profile.getId(), startDate, endDate
        );


        return list.stream().map(this::toDTO).toList();
    }

    //delete income id for current user
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (entity.getProfile().getId() != profile.getId()) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(entity);
    }

    //Get latest 5 expenses for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId()); // 👈 instance use karo
        return list.stream().map(this::toDTO).toList();
    }


    //Get total income for current user
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDateTime startDate, LocalDateTime endDate, String  keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }



    // DTO -> Entity
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate() != null ? dto.getDate().atStartOfDay() : LocalDateTime.now()) // LocalDate → LocalDateTime
                .profile(profile)
                .category(category)
                .build();
    }

    // Entity -> DTO
    private IncomeDTO toDTO(IncomeEntity entity) {
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate() != null ? entity.getDate().toLocalDate() : null) // LocalDateTime → LocalDate
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
