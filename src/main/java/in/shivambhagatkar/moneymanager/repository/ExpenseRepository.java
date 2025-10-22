package in.shivambhagatkar.moneymanager.repository;

import in.shivambhagatkar.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
      Long profileId,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String keyword,
      Sort sort
    );

    List<ExpenseEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    //select * from tbl_expense where profile_id = ?1 and date = ?2
    List<ExpenseEntity> findByProfileIdAndCreatedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);

}
