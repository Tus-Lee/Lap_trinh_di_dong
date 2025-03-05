package com.example.english_learning.room;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class QuestionEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "questionId")
    private int questionId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "topicsJson")
    private String topicsJson;

    @ColumnInfo(name = "index")
    private int index;

    @ColumnInfo(name = "isSuccess", defaultValue = "0")
    private boolean isSuccess;  // Thêm trường trạng thái hoàn thành

    public QuestionEntity(String text, String topicsJson, int index, boolean isSuccess) {
        this.text = text;
        this.topicsJson = topicsJson;
        this.index = index;
        this.isSuccess = isSuccess;
    }

    protected QuestionEntity(Parcel in) {
        questionId = in.readInt();
        text = in.readString();
        topicsJson = in.readString();
        index = in.readInt();
        isSuccess = in.readByte() != 0;
    }

    public static final Creator<QuestionEntity> CREATOR = new Creator<QuestionEntity>() {
        @Override
        public QuestionEntity createFromParcel(Parcel in) {
            return new QuestionEntity(in);
        }

        @Override
        public QuestionEntity[] newArray(int size) {
            return new QuestionEntity[size];
        }
    };

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTopicsJson() {
        return topicsJson;
    }

    public void setTopicsJson(String topicsJson) {
        this.topicsJson = topicsJson;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionId);
        dest.writeString(text);
        dest.writeString(topicsJson);
        dest.writeInt(index);
        dest.writeByte((byte) (isSuccess ? 1 : 0));
    }

    public List<String> getTopics() {
        return new Gson().fromJson(topicsJson, new TypeToken<List<String>>() {}.getType());
    }
}