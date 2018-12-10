package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientThread extends Thread {
    public static final int ACTION1 = 101;
    public static final int ACTION2 = 100;

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
            File file = new File("Notes.txt");
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(notes.size());
            for (Note note : notes) {
                note.write(outputStream);
            }
            /*FileWriter writer = new FileWriter("Notes.txt");
            for (Note note : notes) {
                writer.write(note.text);
            }
            writer.close();*/
        } catch (IOException | EndOfStream e) {
            e.printStackTrace();
        }


    }

    private static void sendNotesToClient(OutputStream outputStream) {
        try {
            File file = new File("Notes.txt");
            InputStream inputStream = new FileInputStream(file);
            int notesArraySize = inputStream.read();
            ArrayList<Note> notes = new ArrayList<>();
            for (int i = 0; i < notesArraySize; i++) {
                Note note = new Note(inputStream);
                notes.add(note);
            }
            byte[] arraySize = new byte[4];
            ByteBuffer.wrap(arraySize).putInt(notes.size());
            outputStream.write(arraySize);
            for (Note note : notes) {
                note.write(outputStream);
            }
            /*try (BufferedReader br = new BufferedReader(new FileReader("Notes.txt"))) {
            String line = br.readLine();
            ArrayList<Note> notes = new ArrayList<Note>();
            while (line != null) {
                notes.add(new Note(line));
                line = br.readLine();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}