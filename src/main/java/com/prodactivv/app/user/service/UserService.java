package com.prodactivv.app.user.service;

import com.prodactivv.app.admin.survey.controller.QuestionnaireService;
import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.workout.UsersWorkoutPlanService;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.IllegalAccessException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UnreachableFileStorageTypeException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.files.DatabaseFile;
import com.prodactivv.app.core.files.DatabaseFileService;
import com.prodactivv.app.core.files.UnsupportedStorageTypeException;
import com.prodactivv.app.core.security.JwtUtils;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.subscription.service.SubscriptionPlanService;
import com.prodactivv.app.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final String USER_NOT_FOUND_MSG = "User not found: %s";

    private final JwtUtils jwtUtils;

    private final UserRepository repository;
    private final UserProgressRepository progressRepository;
    private final UserDietRepository dietRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserSubscriptionService userSubscriptionService;
    private final UsersWorkoutPlanService usersWorkoutPlanService;
    private final QuestionnaireService questionnaireService;
    private final DatabaseFileService fileService;

    public InputStream getDietFile(Long id) throws FileNotFoundException, NotFoundException, UnreachableFileStorageTypeException {
        return fileService.downloadFile(id);
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return repository.findUserByEmail(email).orElseThrow(new NotFoundException(String.format("User %s not found!", email)));
    }

    public List<User.Dto.Full> getUsersWithSubscriptions(String token) throws DisintegratedJwsException {
        String demanderRole = jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ROLE);

        if (User.Roles.isDietitian(demanderRole)) {
            return repository.findAllUsers().stream()
                    .map(this::getFullUser)
                    .filter(User.Dto.Full::isSubscribedToPlanWithDiet)
                    .collect(Collectors.toList());
        } else {
            return repository.findAllUsers().stream()
                    .map(this::getFullUser)
                    .filter(User.Dto.Full::isSubscribedToPlanWithTrainings)
                    .collect(Collectors.toList());
        }
    }

    public List<User.Dto.Simple> getUsersWithSubscriptions() {
        return repository.findAllUsers()
                .stream()
                .map(this::updateAge)
                .map(User.Dto.Simple::fromUser)
                .collect(Collectors.toList());
    }

    public User.Dto.Simple getUserById(Long id) throws NotFoundException {
        return User.Dto.Simple.fromUser(getUser(id));
    }

    public User getUser(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(new NotFoundException(
                String.format(USER_NOT_FOUND_MSG, id)
        ));
    }

    private User updateAge(User user) {
        user.setAge(calculateUserAge(user));
        return repository.save(user);
    }

    public int calculateUserAge(User user) {
        int yearDifference = LocalDate.now().getYear() - user.getBirthday().getYear();
        yearDifference -= LocalDate.now().getDayOfYear() < user.getBirthday().getDayOfYear() ? 1 : 0;
        return yearDifference;
    }

    public UserSubscription.Dto.Full subscribe(Long userId, Long planId) throws UserNotFoundException, NotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(new UserNotFoundException(userId));

        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);

        return userSubscriptionService.subscribe(
                user, plan, LocalDate.now().plusMonths(plan.getIntermittency())
        );
    }

    public User.Dto.Full getFullUser(Long id) throws UserNotFoundException {
        User user = repository.findById(id).orElseThrow(new UserNotFoundException(id));
        return getFullUser(user);
    }

    public User.Dto.Full getFullUser(User user) {
        List<UserSubscription.Dto.FullUserLess> subscriptions = userSubscriptionService.getUserSubscriptions(user);
        List<UsersWorkoutPlan.Dto.WorkoutPlanData> userWorkoutPlansData = usersWorkoutPlanService.getUserWorkoutPlansData(user.getId());
        List<User.Dto.DietUserLess> userDiets = dietRepository.findAllUserDiets(user.getId()).stream().map(User.Dto.DietUserLess::fromUserDiet).collect(Collectors.toList());

        return User.Dto.Full.builder()
                .user(User.Dto.Simple.fromUser(user))
                .subscriptions(subscriptions)
                .workoutPlans(userWorkoutPlansData)
                .diets(userDiets)
                .build();
    }

    public List<Pair<Long, String>> getPlanQuestionnaires(Long userId) throws NotFoundException {
        User user = getUser(userId);
        List<UserSubscription.Dto.FullUserLess> userSubscriptions = userSubscriptionService.getUserSubscriptions(user);

        return userSubscriptions.stream()
                .map(UserSubscription.Dto.FullUserLess::getPlan)
                .map(this::toIdNameQuestionnaire)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public User.Dto.Diet addDiet(Long userId, MultipartFile file) throws NotFoundException, IOException, UnsupportedStorageTypeException {
        UserDiet userDiet = new UserDiet();
        userDiet.setUser(getUser(userId));
        userDiet.setDietFile(fileService.uploadFile(DatabaseFileService.StorageType.LOCAL, file));

        return User.Dto.Diet.fromUserDiet(dietRepository.save(userDiet));
    }

    public void deleteDiet(Long dietId) throws NotFoundException, IOException, UnsupportedStorageTypeException {
        UserDiet userDiet = dietRepository.findById(dietId).orElseThrow(new NotFoundException(String.format("Diet %s not found!", dietId)));
        dietRepository.delete(userDiet);
        fileService.deleteFile(userDiet.getDietFile());
    }

    public UserProgress.Dto.ShowProgress addProgress(UserProgress.Dto.CreateProgress progress, String userId) throws NotFoundException {
        UserProgress userProgress = UserProgress.builder()
                .weight(progress.getWeight())
                .arms(progress.getArms())
                .biceps(progress.getBiceps())
                .chest(progress.getChest())
                .waist(progress.getWaist())
                .hips(progress.getHips())
                .thigh(progress.getThigh())
                .date(progress.getDate() != null ? progress.getDate() : LocalDate.now())
                .user(getUser(Long.parseLong(userId)))
                .build();

       return UserProgress.Dto.ShowProgress.fromUserProgress(progressRepository.save(userProgress));
    }

    public List<UserProgress.Dto.ShowProgress> getProgress(String userId) {
        return progressRepository.getAllUserProgress(Long.parseLong(userId)).stream()
                .map(UserProgress.Dto.ShowProgress::fromUserProgress)
                .collect(Collectors.toList());
    }

    public UserProgress.Dto.ShowProgress deleteProgress(Long progressId, String userId) throws NotFoundException, IllegalAccessException {
        UserProgress userProgress = progressRepository.findById(progressId).orElseThrow(new NotFoundException(String.format("Progress %s not found", progressId)));
        if (userProgress.getUser().getId().equals(Long.parseLong(userId))) {
            progressRepository.delete(userProgress);
            return UserProgress.Dto.ShowProgress.fromUserProgress(userProgress);
        }
        throw new IllegalAccessException("Illegal access!");
    }

    public UserProgress.Dto.ShowProgress updateProgress(Long progressId, UserProgress.Dto.CreateProgress progress, String userId) throws NotFoundException, com.prodactivv.app.core.exceptions.IllegalAccessException {
        UserProgress userProgress = progressRepository.findById(progressId).orElseThrow(new NotFoundException(String.format("Progress %s not found", progressId)));
        if (userProgress.getUser().getId().equals(Long.parseLong(userId))) {
            userProgress.setArms(progress.getArms());
            userProgress.setBiceps(progress.getBiceps());
            userProgress.setChest(progress.getChest());
            userProgress.setHips(progress.getHips());
            userProgress.setThigh(progress.getThigh());
            userProgress.setWaist(progress.getWaist());
            userProgress.setWeight(progress.getWeight());
            if (progress.getDate() != null) {
                userProgress.setDate(progress.getDate());
            }

            return UserProgress.Dto.ShowProgress.fromUserProgress(progressRepository.save(userProgress));
        }

        throw new IllegalAccessException("Illegal access!");
    }

    public User.Dto.Simple setAvatar(Long userId, MultipartFile avatar) throws IOException, NotFoundException {
        DatabaseFile avatarFile = fileService.uploadFileToLocalStorage(avatar);
        User user = getUser(userId);
        user.setAvatar(avatarFile);
        return User.Dto.Simple.fromUser(repository.save(user));
    }

    private Optional<Pair<Long, String>> toIdNameQuestionnaire(SubscriptionPlan.Dto.Full plan) {
        Long questionnaireId = null;
        if (plan.getDietaryQuestionnaireId().isPresent()) {
            questionnaireId = plan.getDietaryQuestionnaireId().get();
        }

        if (plan.getTrainingQuestionnaireId().isPresent()) {
            questionnaireId = plan.getTrainingQuestionnaireId().get();
        }

        if (plan.getCombinedQuestionnaireId().isPresent()) {
            questionnaireId = plan.getCombinedQuestionnaireId().get();
        }

        try {
            if (questionnaireId != null) {
                Questionnaire questionnaire = questionnaireService.getQuestionnaire(questionnaireId);
                return Optional.of(Pair.of(questionnaire.getId(), questionnaire.getName()));
            }
        } catch (NotFoundException ignored) { }

        return Optional.empty();
    }
}
