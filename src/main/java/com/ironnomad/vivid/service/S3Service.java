package com.ironnomad.vivid.service;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.repository.UserRepository;
import com.ironnomad.vivid.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoMetadataService videoMetadataService;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;  // Store the region separately

    public S3Service(@Value("${aws.credentials.access-key}") String accessKey,
                     @Value("${aws.credentials.secret-key}") String secretKey,
                     @Value("${aws.s3.region}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        this.region = region; // Assign the region value
    }

    public Map<String, String> uploadVideo(String username, String title, String description, MultipartFile videoFile) throws IOException {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> fileUrls = new HashMap<>();

        // Generate a unique filename (without extension)
        String baseFileName = UUID.randomUUID().toString();

        // Define video and thumbnail filenames
        String videoFileName = "videos/" + baseFileName + ".mp4";
        String thumbnailFileName = "thumbnails/" + baseFileName + ".jpg";

        // Step 1: Save Video Temporarily
        File tempVideoFile = File.createTempFile(baseFileName, ".mp4");
        videoFile.transferTo(tempVideoFile);

        // Step 2: Upload Video to S3
        PutObjectRequest videoRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(videoFileName)
                .contentType(videoFile.getContentType())
                .build();

        s3Client.putObject(videoRequest, RequestBody.fromBytes(Files.readAllBytes(tempVideoFile.toPath())));
        String videoUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + videoFileName;
        fileUrls.put("videoUrl", videoUrl);

        // Step 3: Generate Thumbnail with the Same Name
        File thumbnailFile = generateThumbnail(tempVideoFile, baseFileName);

        // Step 4: Upload Thumbnail to S3
        if (thumbnailFile.exists() && thumbnailFile.length() > 0) {
            PutObjectRequest thumbnailRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailFileName)
                    .contentType("image/jpeg")
                    .build();

            s3Client.putObject(thumbnailRequest, RequestBody.fromBytes(Files.readAllBytes(thumbnailFile.toPath())));
            String thumbnailUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + thumbnailFileName;
            fileUrls.put("thumbnailUrl", thumbnailUrl);
        } else {
            System.err.println("Thumbnail file is missing or empty. Skipping upload.");
            fileUrls.put("thumbnailUrl", "Thumbnail generation failed.");
        }

        // Cleanup: Delete Temporary Files
        tempVideoFile.delete();
        if (thumbnailFile.exists()) {
            thumbnailFile.delete();
        }

        return videoMetadataService.saveVideoMetadata(user, title, description, fileUrls);
    }

    private File generateThumbnail(File videoFile, String baseFileName) throws IOException {
        // Create a platform-independent temp directory
        String tempDirPath = System.getProperty("java.io.tmpdir");
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // Create thumbnail file in the temp directory
        File thumbnailFile = new File(tempDir, baseFileName + ".jpg");

        // FFmpeg path
        String ffmpegPath = "/usr/bin/ffmpeg";

        // Verify FFmpeg exists
        File ffmpegFile = new File(ffmpegPath);
        if (!ffmpegFile.exists()) {
            throw new IOException("FFmpeg not found at: " + ffmpegPath);
        }

        // Get video duration for Amazon Linux
        String[] durationCmd = {
                ffmpegPath,
                "-i",
                videoFile.getAbsolutePath()
        };
        ProcessBuilder durationBuilder = new ProcessBuilder(durationCmd);
        Process durationProcess = durationBuilder.start();

        // Read output to find duration
        BufferedReader reader = new BufferedReader(new InputStreamReader(durationProcess.getErrorStream()));
        String line;
        String durationStr = null;

        while ((line = reader.readLine()) != null) {
            System.out.println("FFmpeg output: " + line); // Add this for debugging
            if (line.contains("Duration:")) {
                durationStr = line.split("Duration: ")[1].split(",")[0]; // Extract duration part
                break;
            }
        }
        reader.close();

        try {
            durationProcess.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Process interrupted", e);
        }

        if (durationStr == null) {
            throw new IOException("Could not determine video duration.");
        }

        // Convert duration to seconds
        String[] hms = durationStr.split(":");
        double durationInSeconds = Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Double.parseDouble(hms[2]);

        // Calculate middle timestamp (duration / 2)
        int middleSecond = (int) (durationInSeconds / 2);
        String thumbnailTime = String.format("00:00:%02d", middleSecond);

        System.out.println("Extracting thumbnail at: " + thumbnailTime + " (Middle of " + durationInSeconds + "s video)");

        // Step 2: Generate Thumbnail
        String[] command = {
                ffmpegPath,
                "-y",
                "-i", videoFile.getAbsolutePath(),
                "-ss", thumbnailTime,
                "-vframes", "1",
                "-q:v", "2",
                "-vf", "scale=320:-1",
                thumbnailFile.getAbsolutePath()
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Capture FFmpeg logs (Debugging)
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder ffmpegLogs = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                ffmpegLogs.append(errorLine).append("\n");
            }
            System.err.println("FFmpeg Output:\n" + ffmpegLogs);
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg execution was interrupted", e);
        }

        if (exitCode != 0) {
            throw new IOException("Failed to generate thumbnail using FFmpeg. Exit Code: " + exitCode);
        }

        if (!thumbnailFile.exists() || thumbnailFile.length() == 0) {
            throw new IOException("Generated thumbnail file is missing or empty.");
        }

        System.out.println("Thumbnail successfully generated at: " + thumbnailFile.getAbsolutePath());
        return thumbnailFile;
    }
    public void deleteVideo(String videoFileURL, String thumbnailFileURL) {

        // Delete video file
        if (videoFileURL != null) {
            String videoKey = extractKeyFromUrl(videoFileURL);
            DeleteObjectRequest deleteVideoRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(videoKey)
                    .build();
            s3Client.deleteObject(deleteVideoRequest);
        }

        // Delete thumbnail
        if (thumbnailFileURL != null) {
            String thumbnailKey = extractKeyFromUrl(thumbnailFileURL);
            DeleteObjectRequest deleteThumbnailRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailKey)
                    .build();
            s3Client.deleteObject(deleteThumbnailRequest);
        }
    }

    private String extractKeyFromUrl(String url) {
        // Example URL: https://bucket-name.s3.region.amazonaws.com/key
        return url.substring(url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
    }
}
