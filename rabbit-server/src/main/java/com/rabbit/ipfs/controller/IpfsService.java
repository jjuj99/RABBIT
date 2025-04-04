package com.rabbit.ipfs.controller;

import com.rabbit.global.ipfs.PinataUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class IpfsService {

    private final PinataUploader pinataUploader;

    public String uploadNftImage(MultipartFile file) {
        return pinataUploader.uploadFileToIPFS(file);
    }
}

