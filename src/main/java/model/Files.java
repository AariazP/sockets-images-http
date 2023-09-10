package model;

import java.io.*;
import java.net.Socket;

public class Files {


    public static void sendFile(String filename, Socket socket) {
        try{
            System.out.println("filename = " + filename);
            File localFile = new File(filename);
            BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(localFile));

            long size = localFile.length();
            System.out.println("size = " + size);

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(filename);
            printWriter.println("Size:"+size);

            Thread.sleep(50);

            BufferedOutputStream toNetwork = new BufferedOutputStream(socket.getOutputStream());

            byte[] blockToSend = new byte[1024];
            int in;
            while ((in = fromFile.read(blockToSend)) != -1){
                toNetwork.write(blockToSend, 0, in);
            }

            Thread.sleep(50);
            toNetwork.flush();
            System.out.println(" Se envío el archivo ");

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
