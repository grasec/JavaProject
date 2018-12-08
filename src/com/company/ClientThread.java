package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientThread extends Thread {
    public static final int ACTION1 = 100;
    public static final int ACTION2 = 101;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            int action = inputStream.read();
            switch (action) {
                case ACTION1:
                    getNotesFromClient(inputStream);
                    break;
                case ACTION2:
                    sendNotesToClient(outputStream);
                    break;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void getNotesFromClient(InputStream inputStream) {
        ArrayList<Note> notes = new ArrayList<Note>();
        try {
            byte[] arraySize = new byte[4];
            int actuallyRead = inputStream.read(arraySize);
            if (actuallyRead != 4)
                return;
            int arrayLength = ByteBuffer.wrap(arraySize).getInt();
            while (notes.size() != arrayLength)
                notes.add(new Note(inputStream));
            FileWriter writer = new FileWriter("Notes.txt");
            for (Note note : notes) {
                writer.write(note.text);
            }
            writer.close();
        } catch (IOException | EndOfStream e) {
            e.printStackTrace();
        }


    }

    private static void sendNotesToClient(OutputStream outputStream) {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("Notes.txt"))) {
                String line = br.readLine();
                ArrayList<Note> notes = new ArrayList<Note>();
                while (line != null) {
                    notes.add(new Note(line));
                    line = br.readLine();
                }
                byte[] arraySize = new byte[4];
                ByteBuffer.wrap(arraySize).putInt(notes.size());
                outputStream.write(arraySize);
                for (Note note : notes) {
                    note.write(outputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}