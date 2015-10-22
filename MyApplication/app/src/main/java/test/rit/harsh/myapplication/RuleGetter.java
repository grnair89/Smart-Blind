package test.rit.harsh.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by patil on 10/9/2015.
 */
public class RuleGetter implements Parcelable {

    public static final Creator<RuleGetter> CREATOR = new Creator<RuleGetter>() {
        @Override
        public RuleGetter createFromParcel(Parcel in) {
            return new RuleGetter(in);
        }

        @Override
        public RuleGetter[] newArray(int size) {
            return new RuleGetter[size];
        }
    };
    private String name;

    public RuleGetter(String name) {
        this.name = name;
    }

    protected RuleGetter(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}