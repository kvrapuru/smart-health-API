package com.example.demo.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("username")
    private String username;

    @Column
    @JsonProperty("name")
    private String name;

    @Column
    @JsonProperty("profilePicture")
    private String profilePicture;

    @Column
    @JsonProperty("gender")
    private String gender;

    @Column
    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateOfBirth;

    @Column
    @JsonProperty("height")
    private Double height;

    @Column
    @JsonProperty("weight")
    private Double weight;

    @Column
    @JsonProperty("heightUnit")
    private String heightUnit; // CM, INCH

    @Column
    @JsonProperty("weightUnit")
    private String weightUnit; // KG, LB

    @Column
    @JsonProperty("activityLevel")
    private String activityLevel;

    // Health Details
    @Column
    @JsonProperty("fitnessGoals")
    private String fitnessGoals;

    @Column
    @JsonProperty("medicalConditions")
    private String medicalConditions;

    @Column
    @JsonProperty("allergies")
    private String allergies;

    @Column
    @JsonProperty("medications")
    private String medications;

    // Goals
    @Column
    @JsonProperty("calorieGoal")
    private Integer calorieGoal;

    @Column
    @JsonProperty("waterGoal")
    private Integer waterGoal;

    @Column
    @JsonProperty("stepsGoal")
    private Integer stepsGoal;

    @Column
    @JsonProperty("exerciseGoal")
    private Integer exerciseGoal;

    @Column
    @JsonProperty("weightGoal")
    private Double weightGoal;

    @Column
    @JsonProperty("sleepGoal")
    private Integer sleepGoal;

    @Column
    @JsonProperty("weeklyWorkouts")
    private Integer weeklyWorkouts;

    @Column
    @JsonProperty("proteinGoal")
    private Integer proteinGoal;

    @Column
    @JsonProperty("carbsGoal")
    private Integer carbsGoal;

    @Column
    @JsonProperty("fatGoal")
    private Integer fatGoal;

    @Column
    @JsonProperty("fiberGoal")
    private Integer fiberGoal;

    @Column
    @JsonProperty("sodiumLimit")
    private Integer sodiumLimit;

    @Column
    @JsonProperty("sugarLimit")
    private Integer sugarLimit;

    @Column
    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column
    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return null; // We're not using password authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getFitnessGoals() {
        return fitnessGoals;
    }

    public void setFitnessGoals(String fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public Integer getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(Integer calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public Integer getWaterGoal() {
        return waterGoal;
    }

    public void setWaterGoal(Integer waterGoal) {
        this.waterGoal = waterGoal;
    }

    public Integer getStepsGoal() {
        return stepsGoal;
    }

    public void setStepsGoal(Integer stepsGoal) {
        this.stepsGoal = stepsGoal;
    }

    public Integer getExerciseGoal() {
        return exerciseGoal;
    }

    public void setExerciseGoal(Integer exerciseGoal) {
        this.exerciseGoal = exerciseGoal;
    }

    public Double getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(Double weightGoal) {
        this.weightGoal = weightGoal;
    }

    public Integer getSleepGoal() {
        return sleepGoal;
    }

    public void setSleepGoal(Integer sleepGoal) {
        this.sleepGoal = sleepGoal;
    }

    public Integer getWeeklyWorkouts() {
        return weeklyWorkouts;
    }

    public void setWeeklyWorkouts(Integer weeklyWorkouts) {
        this.weeklyWorkouts = weeklyWorkouts;
    }

    public Integer getProteinGoal() {
        return proteinGoal;
    }

    public void setProteinGoal(Integer proteinGoal) {
        this.proteinGoal = proteinGoal;
    }

    public Integer getCarbsGoal() {
        return carbsGoal;
    }

    public void setCarbsGoal(Integer carbsGoal) {
        this.carbsGoal = carbsGoal;
    }

    public Integer getFatGoal() {
        return fatGoal;
    }

    public void setFatGoal(Integer fatGoal) {
        this.fatGoal = fatGoal;
    }

    public Integer getFiberGoal() {
        return fiberGoal;
    }

    public void setFiberGoal(Integer fiberGoal) {
        this.fiberGoal = fiberGoal;
    }

    public Integer getSodiumLimit() {
        return sodiumLimit;
    }

    public void setSodiumLimit(Integer sodiumLimit) {
        this.sodiumLimit = sodiumLimit;
    }

    public Integer getSugarLimit() {
        return sugarLimit;
    }

    public void setSugarLimit(Integer sugarLimit) {
        this.sugarLimit = sugarLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
