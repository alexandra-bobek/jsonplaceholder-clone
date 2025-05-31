package com.jsonplaceholder.service.impl;

import com.jsonplaceholder.dto.AddressDto;
import com.jsonplaceholder.dto.CompanyDto;
import com.jsonplaceholder.dto.GeoDto;
import com.jsonplaceholder.dto.UserDto;
import com.jsonplaceholder.model.Address;
import com.jsonplaceholder.model.Company;
import com.jsonplaceholder.model.Geo;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.UserRepository;
import com.jsonplaceholder.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        updateUserFromDto(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setWebsite(user.getWebsite());

        if (user.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setSuite(user.getAddress().getSuite());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setZipcode(user.getAddress().getZipcode());

            if (user.getAddress().getGeo() != null) {
                GeoDto geoDto = new GeoDto();
                geoDto.setLat(user.getAddress().getGeo().getLat());
                geoDto.setLng(user.getAddress().getGeo().getLng());
                addressDto.setGeo(geoDto);
            }

            dto.setAddress(addressDto);
        }

        if (user.getCompany() != null) {
            CompanyDto companyDto = new CompanyDto();
            companyDto.setName(user.getCompany().getName());
            companyDto.setCatchPhrase(user.getCompany().getCatchPhrase());
            companyDto.setBs(user.getCompany().getBs());
            dto.setCompany(companyDto);
        }

        return dto;
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setWebsite(dto.getWebsite());

        if (dto.getAddress() != null) {
            Address address = new Address();
            address.setStreet(dto.getAddress().getStreet());
            address.setSuite(dto.getAddress().getSuite());
            address.setCity(dto.getAddress().getCity());
            address.setZipcode(dto.getAddress().getZipcode());

            if (dto.getAddress().getGeo() != null) {
                Geo geo = new Geo();
                geo.setLat(dto.getAddress().getGeo().getLat());
                geo.setLng(dto.getAddress().getGeo().getLng());
                address.setGeo(geo);
            }

            user.setAddress(address);
        }

        if (dto.getCompany() != null) {
            Company company = new Company();
            company.setName(dto.getCompany().getName());
            company.setCatchPhrase(dto.getCompany().getCatchPhrase());
            company.setBs(dto.getCompany().getBs());
            user.setCompany(company);
        }

        return user;
    }

    private void updateUserFromDto(User user, UserDto dto) {
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setWebsite(dto.getWebsite());

        if (dto.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(new Address());
            }
            user.getAddress().setStreet(dto.getAddress().getStreet());
            user.getAddress().setSuite(dto.getAddress().getSuite());
            user.getAddress().setCity(dto.getAddress().getCity());
            user.getAddress().setZipcode(dto.getAddress().getZipcode());

            if (dto.getAddress().getGeo() != null) {
                if (user.getAddress().getGeo() == null) {
                    user.getAddress().setGeo(new Geo());
                }
                user.getAddress().getGeo().setLat(dto.getAddress().getGeo().getLat());
                user.getAddress().getGeo().setLng(dto.getAddress().getGeo().getLng());
            }
        }

        if (dto.getCompany() != null) {
            if (user.getCompany() == null) {
                user.setCompany(new Company());
            }
            user.getCompany().setName(dto.getCompany().getName());
            user.getCompany().setCatchPhrase(dto.getCompany().getCatchPhrase());
            user.getCompany().setBs(dto.getCompany().getBs());
        }
    }
} 