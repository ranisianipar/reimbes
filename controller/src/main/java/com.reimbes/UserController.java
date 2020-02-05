package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.reimbes.constant.General.*;
import static com.reimbes.constant.UrlConstants.*;


@CrossOrigin(origins = CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    @GetMapping(value = REPORT_PREFIX)
//    produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    public ResponseEntity<ByteArrayResource> getReport(
            @RequestParam(value = "start", defaultValue = "0") String start,
            @RequestParam(value = "end", defaultValue = "0") String end,
            @RequestParam(value = "type") String type
    ) {
        try {
            String filename = String.format("report-dummy-%s.xlsx",type);
            byte[] file = userService.getReport(new Long(start), new Long(end), type);
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            return new ResponseEntity<>(new ByteArrayResource(file), header, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // update profile
    @PutMapping
    public BaseResponse updateUser(@RequestBody ReimsUser user, HttpServletResponse response) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(userService.updateMyData(user, response));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping
    public BaseResponse getUser() {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getMapper().map(userService.get(IDENTITY_CODE), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(value = UrlConstants.IMAGE_PREFIX)
    public BaseResponse getImage(@RequestParam(value = "path") String imagePath) {
        BaseResponse<String> br = new BaseResponse();
        try {
            br.setData(userService.getImage(imagePath));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }


    private MapperFacade getMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ReimsUser.class, UserResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }


}
