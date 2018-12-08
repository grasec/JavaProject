package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Note {
    public String text;

    public Note(String text) {
        this.text = text;
    }

    public Note(InputStream inputStream) throws IOException, EndOfStream {
        int noteLength = inputStream.read();
        if (noteLength == -1)
            throw new EndOfStream();
        byte[] noteBytes = new byte[noteLength];
        int actuallyRead = inputStream.read(noteBytes);
        if (actuallyRead != noteLength)
            return;
        this.text = new String(noteBytes);

    }


    public void write(OutputStream outputStream) throws IOException {
        byte[] noteBytes = text.getBytes();
        outputStream.write(noteBytes.length);
        outputStream.write(noteBytes);

    }
}
