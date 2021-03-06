package com.jsoniter;

public class Slice {

    public byte[] data;
    public int head;
    public int len;

    public Slice(byte[] data, int head, int len) {
        this.data = data;
        this.head = head;
        this.len = len;
    }

    public static Slice make(int len, int cap) {
        return new Slice(new byte[cap], 0, len);
    }

    public static Slice make(String str) {
        byte[] data = str.getBytes();
        return new Slice(data, 0, data.length);
    }

    public final void append(byte c) {
        if (len == data.length) {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[len++] = c;
    }

    public final byte at(int pos) {
        return data[head+pos];
    }

    @Override
    public final boolean equals(Object o) {
        Slice slice = (Slice) o;
        if (len != slice.len) return false;
        for (int i = head, j = slice.head; i < head+len; i++, j++)
            if (data[i] != slice.data[j])
                return false;
        return true;

    }

    @Override
    public final int hashCode() {
        int result = 1;
        for (int i = head; i < head+len; i++) {
            result = 31 * result + data[i];
        }
        return result;
    }

    @Override
    public final String toString() {
        return new String(data, head, len);
    }
}
