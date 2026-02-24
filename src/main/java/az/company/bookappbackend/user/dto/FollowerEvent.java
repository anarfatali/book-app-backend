package az.company.bookappbackend.user.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowerEvent extends ApplicationEvent {

    private final Long userToFollow;
    private final Long currentUser;

    public FollowerEvent(Object source, Long userToFollow, Long currentUser) {
        super(source);
        this.userToFollow = userToFollow;
        this.currentUser = currentUser;
    }
}