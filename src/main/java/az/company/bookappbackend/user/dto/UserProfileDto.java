package az.company.bookappbackend.user.dto;

import az.company.bookappbackend.common.enums.Interests;
import az.company.bookappbackend.common.enums.ReadingFrequency;
import az.company.bookappbackend.common.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;
    private String email;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String avatarUrl;
    private String bio;
    private String readingExperience;
    private Interests interests;
    private SubscriptionType subscriptionType;
    private ReadingFrequency readingFrequency;
    private boolean isPrivate;
    private boolean verified;
}
