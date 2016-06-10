package com.subway.ladmin.subwaynyk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ladmin on 10/06/16.
 */

    public class MyParceAble implements Parcelable {

        private int mData;

    /* everything below here is for implementing Parcelable */

        // 99.9% of the time you can just ignore this
        @Override
        public int describeContents() {
            return 0;
        }

        // write your object's data to the passed-in Parcel
        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mData);
        }

        // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
        public static final Parcelable.Creator<MyParceAble> CREATOR = new Parcelable.Creator<MyParceAble>() {
            public MyParceAble createFromParcel(Parcel in) {
                return new MyParceAble(in);
            }

            public MyParceAble[] newArray(int size) {
                return new MyParceAble[size];
            }
        };

        // example constructor that takes a Parcel and gives you an object populated with it's values
        private MyParceAble(Parcel in) {
            mData = in.readInt();
        }
    }

