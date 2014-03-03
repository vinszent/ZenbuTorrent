package main.java.local.util;

import java.nio.ByteBuffer;

public class ArrayUtils
{
    private ArrayUtils()
    {
    }

    public static byte[] split(ByteBuffer bb)
    {
        byte[] temp = new byte[bb.remaining()];
        int i = 0;

        while(bb.hasRemaining())
        {
            temp[i] = bb.get();
            i++;
        }

        return temp;
    }
}
