package in.shivambhagatkar.moneymanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterDTO {

    private String type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;
    private String sortFilter;
    private String sortOrder;
}
