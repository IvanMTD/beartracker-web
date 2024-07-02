package ru.beartrack.web.controllers.secure;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.Role;
import ru.beartrack.web.models.ApplicationUser;

@ControllerAdvice
public class SecurityControllerAdvice {
    @ModelAttribute(name = "auth")
    public Mono<Boolean> isAuthenticate(@AuthenticationPrincipal ApplicationUser user){
        if(user != null){
            return Mono.just(true);
        }else{
            return Mono.just(false);
        }
    }

    @ModelAttribute(name = "admin")
    public Mono<Boolean> isAdmin(@AuthenticationPrincipal ApplicationUser user){
        return getRoleAccess(user,Role.ADMIN);
    }

    @ModelAttribute(name = "moderator")
    public Mono<Boolean> isModerator(@AuthenticationPrincipal ApplicationUser user){
        return getRoleAccess(user,Role.MODERATOR);
    }

    private Mono<Boolean> getRoleAccess(ApplicationUser user, Role role){
        if(user != null){
            if(user.getRole().equals(role)){
                return Mono.just(true);
            }else{
                return Mono.just(false);
            }
        }else{
            return Mono.just(false);
        }
    }
}
