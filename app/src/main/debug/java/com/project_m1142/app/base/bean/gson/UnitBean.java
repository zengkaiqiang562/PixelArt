package com.project_m1142.app.base.bean.gson;

import com.google.gson.annotations.SerializedName;

public class UnitBean {

    @SerializedName("id")
    private String id;

    @SerializedName("weight")
    private String weight;

    @SerializedName("type")
    private String type;

    public String getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        String suffixId = id;
        if (id != null && id.startsWith("ca-app-pub-")) {
            suffixId = id.substring("ca-app-pub-".length());
        }
        return "{" +
                "id='" + suffixId + '\'' +
                ", type='" + type + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnitBean that = (UnitBean) o;

        if (!id.equals(that.id)) return false;
        if (!weight.equals(that.weight)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + weight.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
