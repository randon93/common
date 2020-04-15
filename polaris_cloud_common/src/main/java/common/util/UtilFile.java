package common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class UtilFile {

    private UtilFile() {
    }

    public static ByteBuffer readBytesFile(String ruta) {
        FileInputStream fileInputStream = null;
        try {
            File archivo = new File(ruta);
            fileInputStream = new FileInputStream(ruta);

            byte[] fileArray = new byte[(int) archivo.length()];
            int res = fileInputStream.read(fileArray);

            while (res != -1) {
                res = fileInputStream.read(fileArray);
            }
            return ByteBuffer.wrap(fileArray);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void deleteFile(String ruta) {
        try {
            File archivo = new File(ruta);
            FileInputStream fileInput = new FileInputStream(archivo);

            fileInput.close();
            archivo.delete();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createFileTemp(String nombre, String extension) {
        File fileTemp = createFileTempReturnFile(nombre, extension);
        if (fileTemp == null) {
            return null;
        }
        return fileTemp.getAbsolutePath();
    }

    public static File createFileTempReturnFile(String nombre, String extension) {
        File fileTemp = null;
        try {
            fileTemp = File.createTempFile(nombre, extension);
            fileTemp.deleteOnExit();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return fileTemp;
    }

    public static String createFile(String nombre, String extension, String data) {
        if (nombre != null && !nombre.isEmpty() && extension != null && !extension.isEmpty() && data != null && !data.isEmpty()) {
            BufferedWriter out = null;
            try {
                File tempFile = createFileTempReturnFile(nombre, extension);
                out = new BufferedWriter(new FileWriter(tempFile));
                out.write(data);
                out.close();
                return tempFile.getAbsolutePath();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}