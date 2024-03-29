package com.api.siiconges.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.siiconges.model.Address;
import com.api.siiconges.repository.AddressRepository;

@Service
public class AddressService {
	@Autowired
	private AddressRepository addressRepository;
	
	public List<Address> getAllAddresses() {
		return addressRepository.findAll();
	}

	public Optional<Address> getAddressById(Long id) {
		return addressRepository.findById(id); 
	}
	
	public void deleteSpectacleTicketById(Long id) {		
		addressRepository.deleteById(id);

	}
}
