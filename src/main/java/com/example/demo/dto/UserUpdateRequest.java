package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class UserUpdateRequest {
    @JsonProperty("personalDetails")
    private PersonalDetails personalDetails;

    @JsonProperty("healthDetails")
    private HealthDetails healthDetails;

    @JsonProperty("goals")
    private Goals goals;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    // Nested class for personal details
    public static class PersonalDetails {
        @JsonProperty("name")
        private String name;

        @JsonProperty("fullName")
        private String fullName;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("height")
        private Double height;

        @JsonProperty("weight")
        private Double weight;

        @JsonProperty("heightUnit")
        private String heightUnit;

        @JsonProperty("weightUnit")
        private String weightUnit;

        @JsonProperty("dateOfBirth")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate dateOfBirth;

        @JsonProperty("activityLevel")
        private String activityLevel;

        @JsonProperty("profilePicture")
        private String profilePicture;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }

        public String getHeightUnit() { return heightUnit; }
        public void setHeightUnit(String heightUnit) { this.heightUnit = heightUnit; }

        public String getWeightUnit() { return weightUnit; }
        public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getActivityLevel() { return activityLevel; }
        public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

        public String getProfilePicture() { return profilePicture; }
        public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    }

    // Nested class for health details
    public static class HealthDetails {
        @JsonProperty("fitnessGoals")
        private List<String> fitnessGoals;

        @JsonProperty("medicalConditions")
        private String medicalConditions;

        @JsonProperty("allergies")
        private String allergies;

        @JsonProperty("medications")
        private String medications;

        // Getters and Setters
        public List<String> getFitnessGoals() { return fitnessGoals; }
        public void setFitnessGoals(List<String> fitnessGoals) { this.fitnessGoals = fitnessGoals; }

        public String getMedicalConditions() { return medicalConditions; }
        public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

        public String getAllergies() { return allergies; }
        public void setAllergies(String allergies) { this.allergies = allergies; }

        public String getMedications() { return medications; }
        public void setMedications(String medications) { this.medications = medications; }
    }

    // Nested class for goals
    public static class Goals {
        @JsonProperty("calorieGoal")
        private Integer calorieGoal;

        @JsonProperty("waterGoal")
        private Integer waterGoal;

        @JsonProperty("stepsGoal")
        private Integer stepsGoal;

        @JsonProperty("exerciseGoal")
        private Integer exerciseGoal;

        @JsonProperty("weightGoal")
        private Double weightGoal;

        @JsonProperty("sleepGoal")
        private Integer sleepGoal;

        @JsonProperty("weeklyWorkouts")
        private Integer weeklyWorkouts;

        @JsonProperty("proteinGoal")
        private Integer proteinGoal;

        @JsonProperty("carbsGoal")
        private Integer carbsGoal;

        @JsonProperty("fatGoal")
        private Integer fatGoal;

        @JsonProperty("fiberGoal")
        private Integer fiberGoal;

        @JsonProperty("sodiumLimit")
        private Integer sodiumLimit;

        @JsonProperty("sugarLimit")
        private Integer sugarLimit;

        // Getters and Setters
        public Integer getCalorieGoal() { return calorieGoal; }
        public void setCalorieGoal(Integer calorieGoal) { this.calorieGoal = calorieGoal; }

        public Integer getWaterGoal() { return waterGoal; }
        public void setWaterGoal(Integer waterGoal) { this.waterGoal = waterGoal; }

        public Integer getStepsGoal() { return stepsGoal; }
        public void setStepsGoal(Integer stepsGoal) { this.stepsGoal = stepsGoal; }

        public Integer getExerciseGoal() { return exerciseGoal; }
        public void setExerciseGoal(Integer exerciseGoal) { this.exerciseGoal = exerciseGoal; }

        public Double getWeightGoal() { return weightGoal; }
        public void setWeightGoal(Double weightGoal) { this.weightGoal = weightGoal; }

        public Integer getSleepGoal() { return sleepGoal; }
        public void setSleepGoal(Integer sleepGoal) { this.sleepGoal = sleepGoal; }

        public Integer getWeeklyWorkouts() { return weeklyWorkouts; }
        public void setWeeklyWorkouts(Integer weeklyWorkouts) { this.weeklyWorkouts = weeklyWorkouts; }

        public Integer getProteinGoal() { return proteinGoal; }
        public void setProteinGoal(Integer proteinGoal) { this.proteinGoal = proteinGoal; }

        public Integer getCarbsGoal() { return carbsGoal; }
        public void setCarbsGoal(Integer carbsGoal) { this.carbsGoal = carbsGoal; }

        public Integer getFatGoal() { return fatGoal; }
        public void setFatGoal(Integer fatGoal) { this.fatGoal = fatGoal; }

        public Integer getFiberGoal() { return fiberGoal; }
        public void setFiberGoal(Integer fiberGoal) { this.fiberGoal = fiberGoal; }

        public Integer getSodiumLimit() { return sodiumLimit; }
        public void setSodiumLimit(Integer sodiumLimit) { this.sodiumLimit = sodiumLimit; }

        public Integer getSugarLimit() { return sugarLimit; }
        public void setSugarLimit(Integer sugarLimit) { this.sugarLimit = sugarLimit; }
    }

    // Getters and Setters for main class
    public PersonalDetails getPersonalDetails() { return personalDetails; }
    public void setPersonalDetails(PersonalDetails personalDetails) { this.personalDetails = personalDetails; }

    public HealthDetails getHealthDetails() { return healthDetails; }
    public void setHealthDetails(HealthDetails healthDetails) { this.healthDetails = healthDetails; }

    public Goals getGoals() { return goals; }
    public void setGoals(Goals goals) { this.goals = goals; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Convenience methods to access personal details
    public String getName() { return personalDetails != null ? personalDetails.getName() : null; }
    public String getFullName() { return personalDetails != null ? personalDetails.getFullName() : null; }
    public String getGender() { return personalDetails != null ? personalDetails.getGender() : null; }
    public Double getHeight() { return personalDetails != null ? personalDetails.getHeight() : null; }
    public Double getWeight() { return personalDetails != null ? personalDetails.getWeight() : null; }
    public String getHeightUnit() { return personalDetails != null ? personalDetails.getHeightUnit() : null; }
    public String getWeightUnit() { return personalDetails != null ? personalDetails.getWeightUnit() : null; }
    public LocalDate getDateOfBirth() { return personalDetails != null ? personalDetails.getDateOfBirth() : null; }
    public String getActivityLevel() { return personalDetails != null ? personalDetails.getActivityLevel() : null; }
    public String getProfilePicture() { return personalDetails != null ? personalDetails.getProfilePicture() : null; }

    // Convenience methods to access health details
    public List<String> getFitnessGoals() { return healthDetails != null ? healthDetails.getFitnessGoals() : null; }
    public String getMedicalConditions() { return healthDetails != null ? healthDetails.getMedicalConditions() : null; }
    public String getAllergies() { return healthDetails != null ? healthDetails.getAllergies() : null; }
    public String getMedications() { return healthDetails != null ? healthDetails.getMedications() : null; }

    // Convenience methods to access goals
    public Integer getCalorieGoal() { return goals != null ? goals.getCalorieGoal() : null; }
    public Integer getWaterGoal() { return goals != null ? goals.getWaterGoal() : null; }
    public Integer getStepsGoal() { return goals != null ? goals.getStepsGoal() : null; }
    public Integer getExerciseGoal() { return goals != null ? goals.getExerciseGoal() : null; }
    public Double getWeightGoal() { return goals != null ? goals.getWeightGoal() : null; }
    public Integer getSleepGoal() { return goals != null ? goals.getSleepGoal() : null; }
    public Integer getWeeklyWorkouts() { return goals != null ? goals.getWeeklyWorkouts() : null; }
    public Integer getProteinGoal() { return goals != null ? goals.getProteinGoal() : null; }
    public Integer getCarbsGoal() { return goals != null ? goals.getCarbsGoal() : null; }
    public Integer getFatGoal() { return goals != null ? goals.getFatGoal() : null; }
    public Integer getFiberGoal() { return goals != null ? goals.getFiberGoal() : null; }
    public Integer getSodiumLimit() { return goals != null ? goals.getSodiumLimit() : null; }
    public Integer getSugarLimit() { return goals != null ? goals.getSugarLimit() : null; }
} 