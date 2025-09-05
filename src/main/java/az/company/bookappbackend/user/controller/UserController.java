package az.company.bookappbackend.user.controller;

import az.company.bookappbackend.user.dto.request.EditUserInfoRequest;
import az.company.bookappbackend.user.dto.response.FollowRequestResponseDTO;
import az.company.bookappbackend.user.dto.response.FollowingResponseDTO;
import az.company.bookappbackend.user.dto.response.SimpleUserProfileDto;
import az.company.bookappbackend.user.dto.response.UpdateUserVisibilityDto;
import az.company.bookappbackend.user.dto.response.UpdatedUserProfileDto;
import az.company.bookappbackend.user.dto.response.UserAvatarResponse;
import az.company.bookappbackend.user.dto.response.UserProfileResponse;

import az.company.bookappbackend.user.service.UserService;
import io.minio.GetObjectResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    //CRUD endpoints
    @GetMapping
    public ResponseEntity<Page<SimpleUserProfileDto>> getUsersPageable(
            @Parameter(hidden = true)
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UpdatedUserProfileDto> updateUserProfile(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestBody @Valid EditUserInfoRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, request));
    }

    @PutMapping("/{userId}/profile/visibility")
    public ResponseEntity<UpdateUserVisibilityDto> updateUserProfileVisibility(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("isPrivate") boolean isPublic
    ) {
        boolean isPublicNow = userService.updateUserVisibility(userId, isPublic);

        return ResponseEntity.ok(new UpdateUserVisibilityDto(isPublicNow));
    }

    // Profile picture endpoints
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> uploadUserAvatar(@PathVariable("userId") @NotNull @Min(1) Long userId, @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadUserAvatar(userId, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avatarUrl);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<InputStreamResource> getUserAvatar(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        UserAvatarResponse userAvatarResponse = userService.getUserAvatar(userId);

        String avatarUrl = userAvatarResponse.avatarUrl();
        GetObjectResponse userAvatar = userAvatarResponse.file();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + avatarUrl + "\"")
                .contentType(MediaType.parseMediaType(userAvatarResponse.contentType()))
                .body(new InputStreamResource(userAvatar));
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        userService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    // Follow system endpoints

    @GetMapping("/{userId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public Page<FollowingResponseDTO> getFollowers(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable
    ) {
        return userService.getFollowers(userId, pageable);
    }

    @GetMapping("/{userId}/following")
    @ResponseStatus(HttpStatus.OK)
    public Page<FollowingResponseDTO> getFollowing(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication
    ) {
        return userService.getFollowings(userId, pageable, authentication);
    }

    @PostMapping("{userId}/follow")
    @ResponseStatus(HttpStatus.OK)
    public FollowingResponseDTO followUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            Authentication authentication
    ) {
        return userService.followUser(userId, authentication);
    }

    @DeleteMapping("{userId}/follow")
    @ResponseStatus(HttpStatus.OK)
    public FollowingResponseDTO unfollowUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            Authentication authentication
    ) {
        return userService.unfollowUser(userId, authentication);
    }

    @GetMapping("/me/follow-requests/incoming")
    @ResponseStatus(HttpStatus.OK)
    public Page<FollowRequestResponseDTO> allIncomingFollowRequests(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable, Authentication authentication
    ) {
        return userService.getIncomingFollowRequests(pageable, authentication);
    }

    @GetMapping("/me/follow-requests/outgoing")
    @ResponseStatus(HttpStatus.OK)
    public Page<FollowRequestResponseDTO> allOutgoingFollowRequests(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable, Authentication authentication
    ) {
        return userService.getOutgoingFollowRequests(pageable, authentication);
    }

    @PostMapping("/me/follow-requests/outgoing/{reqId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public Void cancelOutgoingFollowRequest(
            @PathVariable("reqId") @NotNull @Min(1) Long reqId
    ) {
        return userService.cancelOutgoingFollowRequest(reqId);
    }

    @PostMapping("/me/follow-requests/incoming/{reqId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public Void acceptFollowRequest(
            @PathVariable("reqId") @NotNull @Min(1) Long reqId, Authentication authentication
    ) {
        return userService.acceptFollowRequest(reqId, authentication);
    }

    @PostMapping("/me/follow-requests/incoming/{reqId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public Void RejectFollowRequest(
            @PathVariable("reqId") @NotNull @Min(1) Long reqId, Authentication authentication
    ) {
        return userService.rejectFollowRequest(reqId, authentication);
    }

    @PostMapping("/me/followers/{followerId}/remove")
    @ResponseStatus(HttpStatus.OK)
    public Void removeFollower(
            @PathVariable("followerId") @NotNull @Min(1) Long followerId,
            Authentication authentication
    ) {
        return userService.removeFollower(followerId, authentication);
    }

    @PostMapping("/{userId}/block")
    @ResponseStatus(HttpStatus.OK)
    public Void blockUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            Authentication authentication
    ) {
        return userService.blockUser(userId, authentication);
    }

    @PostMapping("/{userId}/unblock")
    @ResponseStatus
    public Void unblockUser(@PathVariable("userId") @NotNull @Min(1) Long userId, Authentication authentication) {
        return userService.unblockUser(userId, authentication);
    }

}
