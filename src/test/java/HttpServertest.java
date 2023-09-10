import java.io.*;

public class HttpServertest {


    public static void main(String[] args) {
        File file = new File("icons8-nube-96.png");
        try (InputStream imageInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = imageInputStream.read(buffer)) != -1) {
                System.out.println("bytesRead = " + bytesRead);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
