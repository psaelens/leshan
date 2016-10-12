package com.skylaneoptics.ipso;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Pierre Saelens
 */
public class Merge {

    public static void main(String[] args) throws IOException, FileNotFoundException {
        File[] files = new File("/Users/pierre/Projects/leshan/leshan-standalone/src/main/resources/ipso").listFiles();

        try (FileChannel channel = new RandomAccessFile("out.json", "rw").getChannel()) {
            channel.write(ByteBuffer.wrap("[".getBytes()));
            for (int i = 0; i < files.length; i++) {
                final File file = files[i];
                doWrite(channel, file);
                if (i < files.length - 1)
                    channel.write(ByteBuffer.wrap(",".getBytes()));
            }
            channel.write(ByteBuffer.wrap("]".getBytes()));
        }
    }

    private static void doWrite(final FileChannel channel, final File file)
            throws IOException {

        try (FileChannel inChannel = new RandomAccessFile(file, "r").getChannel()) {
            inChannel.transferTo(0, inChannel.size(), channel);
        }
    }


}
