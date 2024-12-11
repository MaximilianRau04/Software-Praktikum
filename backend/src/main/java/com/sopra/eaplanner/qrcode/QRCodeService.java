package com.sopra.eaplanner.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QRCodeService {
    public static final String QR_CODE_DIR = "C:/Users/maxpe/var/www/appdata/qrcodes";

    public static void generateQRCodeImage(String url, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static byte[] getQRCodeImage(String url, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF00002, 0xFFFFC041);

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, config);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    public static void saveQRCode(byte[] qrCode, String fileName) throws Exception {
        Path directory = Paths.get(QR_CODE_DIR);
        if (!Files.exists(directory)) {
            System.out.println("Im making new directory");
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(fileName);
        Files.write(filePath, qrCode);
    }
}
