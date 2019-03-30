package com.reimbes.implementation;

import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;
import com.reimbes.constant.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction create(Transaction transaction) {
        // dihubungin ke user yg bersangkutan
        return null;
    }

    @Override
    public void delete(long id) {
        transactionRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        transactionRepository.deleteAll();
    }

    @Override
    public Transaction get(long id) {
        return transactionRepository.findOne(id);
    }

    @Override
    public String upload(MultipartFile image) throws IOException {
        // ngecek org yg bersangkutan udah ada foldernya apa engga
        String userId = "ID_USER"; // GANTI
        String userPath = UrlConstants.IMAGE_FOLDER_PATH + userId +"/";
        if (!Files.exists(Paths.get(userPath)))
            Files.createDirectory(Paths.get(userPath));
        String localPath = userPath + UUID.randomUUID();

        InputStream inputStream = image.getInputStream();
        Files.copy(inputStream, Paths.get(userPath + localPath));
        return localPath;
    }
}
