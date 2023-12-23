package com.example.accessingdatamysql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.*;


@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int creatorId = -1;

    private boolean isAllowedNewAttempt = true;

    private String title;

    private String content;

    private List<Integer> usersId;
    private List<Integer> usersResult;

    private long sumResults = 0;

    public void createMapPassedUsers() {
        usersId = new ArrayList<>();
        usersResult = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setCreatorId(int userId) {
        creatorId = userId;
    }

    public void updateTitle(String newTitle) {
        title = newTitle;
    }

    public void updateContent(String newContent) {
        content = newContent;
    }

    public void updateAllowedNewAttempt(boolean value) {
        this.isAllowedNewAttempt = value;
    }

//    public AbstractMap.SimpleEntry<String, String> getTest() {
//
//    }

    public AbstractMap.SimpleEntry<String, Integer> getInfo() {
        return new AbstractMap.SimpleEntry<>(title, id);
    }

    public void savePass(int userId, int result) {
        for (int i = 0; i < usersId.size(); i++) {
            if (usersId.get(i) == userId) {
                if (isAllowedNewAttempt) {
                    int lastResult = usersResult.get(i);
                    if (result > lastResult) {
                        sumResults += result - lastResult;
                        usersResult.set(i, result);
                    }
                }
                return;
            }
        }
        usersId.add(userId);
        usersResult.add(result);
        sumResults += result;
    }

    public Map<Integer, Integer> getAllStatistics() {
        Map<Integer, Integer> mapPassedUsers = new HashMap<>();
        for (int i = 0; i < usersId.size(); i++) {
            mapPassedUsers.put(usersId.get(i), usersResult.get(i));
        }
        return mapPassedUsers;
    }

    public ShortStatistic getShortStatistics() {
        return new ShortStatistic(usersId.size(), (double) sumResults / usersId.size());
    }

    public boolean isCorrectID(int attemptId) {
        return id == attemptId;
    }
}
