package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;

import com.reimbes.constant.ResponseCode;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TesseractService ocrService;

    @Override
    public Transaction create(HttpServletRequest request, Transaction transaction) throws ReimsException{
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
        if (transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(id);
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
        if (transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);

        return transactionRepository.findOne(id);
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
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ "webp");
        if (!Files.exists(Paths.get("")))
            Files.createDirectory(Paths.get(path));

        String filename = imageValue.getName()+".webp";
        path = path + filename;

        byte[] data = imageValue.getBytes();
        InputStream inputStream = new ByteArrayInputStream(data);

        BufferedImage renderedImage = ImageIO.read(inputStream);
        ImageIO.write(renderedImage, "webp", new File(path));
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
        if (transaction.getImage()== null || !Files.exists(Paths.get(UrlConstants.IMAGE_FOLDER_PATH+transaction.getImage())))
            errorMessages.add("invalid image path");

        if (errorMessages.isEmpty())
            throw new DataConstraintException("Data constraint");
    }
}
