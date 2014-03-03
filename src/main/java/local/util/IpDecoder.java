package main.java.local.util;

import java.net.InetAddress;
import java.util.ArrayList;

import main.java.logging.Log;

public class IpDecoder
{
    private IpDecoder()
    {
    }

    public static ArrayList<String[]>  decodeByteString(byte[] encodedByteString)
    {
        ArrayList<String[]> addresses = new ArrayList<>();

        for(int i = 0; i < encodedByteString.length / 6; i++)
        {
            byte[] peerAddress = new byte[6];
            String ipAddress;
            String port;

            System.arraycopy(encodedByteString, i * 6, peerAddress, 0, 6);

            // Need to mask all bytes with 16 bits to force unsigned ints
            ipAddress = (peerAddress[0] & 0xFF) + "." + (peerAddress[1] & 0xFF) + "." + (peerAddress[2] & 0xFF) + "." + (peerAddress[3] & 0xFF);
            port = String.valueOf(((peerAddress[4] & 0xFF) << 8) | (peerAddress[5] & 0xFF));

            addresses.add(new String[]{ipAddress, port});
        }

        return addresses;
    }
}
