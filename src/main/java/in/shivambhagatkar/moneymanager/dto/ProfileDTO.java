package in.shivambhagatkar.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private long id;
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;
    private Boolean isActive;           // 🔥 added
    private String activationToken;     // 🔥 added
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
