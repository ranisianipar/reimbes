package com.reimbes.implementation;

import com.reimbes.*;

import com.reimbes.constant.ResponseCode;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TesseractService ocrService;

    @Override
    public Transaction create(HttpServletRequest request, Transaction transaction) throws ReimsException {
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));

        if (user == null) {
            throw new ReimsException("In-Memory user not allowed"
                    , HttpStatus.METHOD_NOT_ALLOWED
                    , ResponseCode.UNAUTHORIZED);
        }
        transaction.setUser(user);
        validate(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(HttpServletRequest request, long id) throws ReimsException{
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(transaction);
    }

    public void deleteByUser(ReimsUser user) {
        List<Fuel> transaction = transactionRepository.findByUser(user);
        if (transaction == null)
            return;
        transactionRepository.delete(transaction);
    }

    @Override
    public void deleteAll(HttpServletRequest request) {
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        transactionRepository.deleteByUser(user);
    }

    @Override
    public Transaction get(HttpServletRequest request, long id) throws ReimsException{
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);

        return transaction;
    }

    public List<Transaction> getAll(HttpServletRequest request, Pageable pageable) {
        return null;
    }

    @Override
    public String upload(HttpServletRequest request, MultipartFile imageValue) throws Exception {
        HashMap userDetails = authService.getCurrentUserDetails(request);
        long userId;

        userId = userService.getUserByUsername((String) userDetails.get("username")).getId();

        String foldername = userId +"\\";
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ foldername);

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = UUID.randomUUID()+".jpg";
        path = path + filename;


        byte[] data = imageValue.getBytes();
        InputStream inputStream = new ByteArrayInputStream(data);

        // upload photo
        try {
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ocrService.readImage(foldername+filename);
    }

    @Override
    public byte[] getPhoto(String imagePath) {
        imagePath = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath);
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // will be deleted
    public String encodeImage(MultipartFile imageValue) throws IOException {
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ "webp/");

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = imageValue.getOriginalFilename().replaceAll(".jpg","")+".webp";
        log.info("Filename: "+filename);

        path = path + filename;

        RenderedImage renderedImage = ImageIO.read(imageValue.getInputStream());
        try {
            log.info("Check the buffered image: "+renderedImage.getData());

            String readers = "";
            for (String i: ImageIO.getReaderFormatNames()) {
                readers = readers+i;
            }
            log.info("Readers: "+readers);
            boolean status = ImageIO.write(renderedImage, "WBMP", new File(path));

            // upload photo
            try {
                log.info("Test path: "+path);
                Files.copy(imageValue.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }


            log.info("Status: "+status);
        } catch ( IOException e) {
            e.printStackTrace();
        }

        return path;

    }

    @Override
    public void deletePhoto(String imagePath) {
        File file = new File(StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+imagePath));
        if(file.delete()){
            System.out.println("Image "+imagePath+" has been removed");
        } else System.out.println("File /Users/pankaj/file.txt doesn't exist");
    }

    private void validate(Transaction transaction) throws ReimsException{
        // validate the data and data type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

        List<String> errorMessages = new ArrayList();

        if (transaction.getDate() == null)
            errorMessages.add("null date");

        if (transaction.getAmount() == 0)
            errorMessages.add("zero amount");

        if (transaction.getCategory() == null)
            errorMessages.add("null category");

        // validate image path
        if (transaction.getImage()== null || !Files.exists(Paths.get(
                UrlConstants.IMAGE_FOLDER_PATH + transaction.getImage())))
            errorMessages.add("invalid image path");

        if (errorMessages.isEmpty())
            throw new DataConstraintException("Data constraint");
    }
}
