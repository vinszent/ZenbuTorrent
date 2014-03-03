package main.java.local.util;

public class UrlEncoder
{
    public static String byteArrayToURLString(byte in[])
    {
        int i = 0;
        String chars = "0123456789ABCDEF";
        StringBuilder out = new StringBuilder();

        if(in == null || in.length <= 0)
        {
            return null;
        }

        while(i < in.length)
        {
            // First check to see if we need ASCII or HEX
            if((in[i] >= '0' && in[i] <= '9') || (in[i] >= 'a' && in[i] <= 'z') || (in[i] >= 'A' && in[i] <= 'Z') || in[i] == '-' || in[i] == '_' || in[i] == '.' || in[i] == '~')
            {
                out.append((char) in[i]);
            }
            else
            {
                out.append('%');
                byte b = (byte) in[i]; // 1111 1111
                out.append(chars.charAt((b & 0b1111_0000) >> 4)); // Mask low nibble and shift high nibble; 1111 0000 > 0000 1111
                out.append(chars.charAt(b & 0b0000_1111)); // Mask high nibble; 0000 1111
            }
            i++;
        }

        return out.toString();
    }
}
