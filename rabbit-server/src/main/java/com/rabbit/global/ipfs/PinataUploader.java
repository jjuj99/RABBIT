package com.rabbit.global.ipfs;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class PinataUploader {

    private static final String PINATA_API_URL = "https://api.pinata.cloud/pinning/pinFileToIPFS";

    @Value("${pinata.apiKey}")
    private String apiKey;

    @Value("${pinata.secretKey}")
    private String secretApiKey;

    public String uploadFileToIPFS(MultipartFile file) {
        OkHttpClient client = new OkHttpClient();

        try {
            // 파일을 Multipart 형식으로 변환
            RequestBody fileBody = RequestBody.create(
                    file.getBytes(),
                    MediaType.parse(file.getContentType())
            );

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getOriginalFilename(), fileBody)
                    .build();

            // 요청 생성
            Request request = new Request.Builder()
                    .url(PINATA_API_URL)
                    .addHeader("pinata_api_key", apiKey)
                    .addHeader("pinata_secret_api_key", secretApiKey)
                    .post(requestBody)
                    .build();

            // 요청 전송
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Pinata 업로드 실패: {}", response.body().string());
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "파일 업로드 실패");
            }

            String responseBody = response.body().string();
            log.info("Pinata 응답: {}", responseBody);

            // 반환된 IPFS 해시 추출 (간단히 처리, 필요시 JSON 파싱)
            String ipfsHash = responseBody.split("\"IpfsHash\":\"")[1].split("\"")[0];
            return "https://gateway.pinata.cloud/ipfs/" + ipfsHash;

        } catch (IOException e) {
            throw new RuntimeException("IPFS 업로드 중 오류 발생", e);
        }
    }

    /**
     * 바이트 배열 콘텐츠를 IPFS에 업로드
     * @param content 업로드할 콘텐츠 바이트 배열
     * @param fileName 파일 이름
     * @param contentType 콘텐츠 타입 (MIME 타입)
     * @return IPFS URL
     */
    public String uploadContent(byte[] content, String fileName, String contentType) {
        OkHttpClient client = new OkHttpClient();

        try {
            // 파일을 Multipart 형식으로 변환
            RequestBody fileBody = RequestBody.create(
                    content,
                    MediaType.parse(contentType)
            );

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, fileBody)
                    .build();

            // 요청 생성
            Request request = new Request.Builder()
                    .url(PINATA_API_URL)
                    .addHeader("pinata_api_key", apiKey)
                    .addHeader("pinata_secret_api_key", secretApiKey)
                    .post(requestBody)
                    .build();

            // 요청 전송
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Pinata 업로드 실패: {}", response.body().string());
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "파일 업로드 실패");
            }

            String responseBody = response.body().string();
            log.info("Pinata 응답: {}", responseBody);

            // 반환된 IPFS 해시 추출 (간단히 처리, 필요시 JSON 파싱)
            String ipfsHash = responseBody.split("\"IpfsHash\":\"")[1].split("\"")[0];
            return "https://gateway.pinata.cloud/ipfs/" + ipfsHash;

        } catch (IOException e) {
            throw new RuntimeException("IPFS 업로드 중 오류 발생", e);
        }
    }
}

