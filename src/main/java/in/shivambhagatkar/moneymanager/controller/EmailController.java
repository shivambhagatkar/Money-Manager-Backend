package in.shivambhagatkar.moneymanager.controller;

import in.shivambhagatkar.moneymanager.entity.ProfileEntity;
import in.shivambhagatkar.moneymanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<?> sendIncomeExcelReport() {
        ProfileEntity profile = profileService.getCurrentProfile();

        // ✅ Run Excel generation + Email in background thread
        new Thread(() -> {
            try {
                var baos = new java.io.ByteArrayOutputStream();
                excelService.writeIncomesToExcel(baos, incomeService.getCurrentMonthIncomesForCurrentUser());

                emailService.sendEmailWithAttachment(
                        profile.getEmail(),
                        "Your Income Excel Report",
                        "<p>Hello " + profile.getFullName() + ",</p>"
                                + "<p>Please find attached your <strong>income report</strong> for this month.</p>"
                                + "<p>Thank you for using <strong>Money Manager</strong>!</p>",
                        baos.toByteArray(),
                        "income.xlsx"
                );

                baos.close();
                System.out.println("✅ Income Excel email sent to " + profile.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send income Excel email: " + e.getMessage());
            }
        }).start();

        return ResponseEntity.ok(Map.of("message", "Income Excel report is being generated and will be emailed soon!"));
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<?> sendExpenseExcelReport() {
        ProfileEntity profile = profileService.getCurrentProfile();

        // ✅ Run Excel generation + Email in background thread
        new Thread(() -> {
            try {
                var baos = new java.io.ByteArrayOutputStream();
                excelService.writeExpensesToExcel(baos, expenseService.getCurrentMonthExpensesForCurrentUser());

                emailService.sendEmailWithAttachment(
                        profile.getEmail(),
                        "Your Expense Excel Report",
                        "<p>Hello " + profile.getFullName() + ",</p>"
                                + "<p>Please find attached your <strong>expense report</strong> for this month.</p>"
                                + "<p>Thank you for using <strong>Money Manager</strong>!</p>",
                        baos.toByteArray(),
                        "expenses.xlsx"
                );

                baos.close();
                System.out.println("✅ Expense Excel email sent to " + profile.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send expense Excel email: " + e.getMessage());
            }
        }).start();

        return ResponseEntity.ok(Map.of("message", "Expense Excel report is being generated and will be emailed soon!"));
    }
}
