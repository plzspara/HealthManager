package net.kevin.com.healthmanager.javaBean;

import java.util.List;

import cn.bmob.v3.BmobUser;

/*
 *@author panlister
 */
public class User extends BmobUser {
    private List<String> step;
    private List<String> stepDate;
    private String gender;
    private String year;
    private String headImage;
    private double weight;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public List<String> getStep() {
        return step;
    }

    public void setStep(List<String> step) {
        this.step = step;
    }

    public List<String> getStepDate() {
        return stepDate;
    }

    public void setStepDate(List<String> stepDate) {
        this.stepDate = stepDate;
    }
}
