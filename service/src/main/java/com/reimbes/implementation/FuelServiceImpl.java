package com.reimbes.implementation;

import com.reimbes.Fuel;
import com.reimbes.FuelRepository;
import com.reimbes.FuelService;
import com.reimbes.ReimsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuelServiceImpl implements FuelService {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FuelRepository fuelRepository;

    @Override
    public Fuel create(Fuel fuel) {
        return fuelRepository.save(fuel);
    }

    @Override
    public Fuel get(long id) {
        return fuelRepository.findOne(id);
    }

    @Override
    public List<Fuel> getAll(Pageable pageable) {
        return fuelRepository.findByUser( userService.getUserByUsername("rani"), pageable);
    }

    @Override
    public void delete(long id) {
        fuelRepository.delete(id);
    }

    @Override
    @Transactional
    public void deleteMany(List<Long> ids) {
        fuelRepository.deleteByIds(ids);
    }

    @Override
    public void deleteByUser(ReimsUser user) {
        // search the image files first, then remove them
        fuelRepository.deleteByUser(user);
    }
}
