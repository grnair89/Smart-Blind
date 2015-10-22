package test.rit.harsh.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by patil on 10/10/2015.
 */
public class History implements Parcelable {

    private String DatenTime;
    private String Temp;

    public History(String Temp, String DatenTime) {
        this.Temp = Temp;
        this.DatenTime = DatenTime;
    }

    protected History(Parcel in) {
        Temp = in.readString();
        DatenTime = in.readString();
    }

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

    public String getTemp() {
        return Temp;
    }

    public String getDatenTime() {
        return DatenTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Temp);
        dest.writeString(DatenTime);
    }
}