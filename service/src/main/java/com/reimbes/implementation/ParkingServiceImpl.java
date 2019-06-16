package com.reimbes.implementation;

import com.reimbes.Parking;
import com.reimbes.ParkingRepository;
import com.reimbes.ParkingService;
import com.reimbes.ReimsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ParkingRepository parkingRepository;


    @Override
    public Parking create(Parking parking) {
        return parkingRepository.save(parking);
    }

    @Override
    public Parking get(long id) {
        return parkingRepository.findOne(id);
    }

    @Override
    public List<Parking> getAll(Pageable pageable) {
        return parkingRepository.findByUser(userService.getUserByUsername("rani"), pageable);
    }

    @Override
    public void delete(long id) {
        parkingRepository.delete(id);
    }

    @Override
    @Transactional
    public void deleteMany(List<Long> ids) {
        parkingRepository.deleteByIds(ids);
    }

    @Override
    public void deleteByUser(ReimsUser user) {
        // search the image files first, then remove them
        parkingRepository.deleteByUser(user);
    }
}
