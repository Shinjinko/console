package com.console.app.main.model;

public class ExecutionResult {
    private String language;
    private String code;
    private String result;

    public ExecutionResult(String language, String code, String result) {
        this.language = language;
        this.code = code;
        this.result = result;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}