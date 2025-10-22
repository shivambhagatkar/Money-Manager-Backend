package in.shivambhagatkar.moneymanager.controller;

import in.shivambhagatkar.moneymanager.dto.ExpenseDTO;
import in.shivambhagatkar.moneymanager.dto.FilterDTO;
import in.shivambhagatkar.moneymanager.dto.IncomeDTO;
import in.shivambhagatkar.moneymanager.service.ExpenseService;
import in.shivambhagatkar.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Filter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController  {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter) {
        LocalDateTime startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDateTime.MIN;
        LocalDateTime endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDateTime.now();

        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortFilter() != null ? filter.getSortFilter() : "date";

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortField);

        if ("income".equalsIgnoreCase(filter.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if ("expense".equalsIgnoreCase(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpense(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
        }
    }
}
