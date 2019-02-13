package com.reimbes;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = Constant.BASE_URL)
@RestController
@RequestMapping(Constant.EMPLOYEE_PREFIX)
public class EmployeeController {


}
