package com.mycity.admin.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.mycity.shared.admindto.AdminPlaceResponseDTO;


@Service
public interface AdminServiceInterface 
{
   List<AdminPlaceResponseDTO> getAllPlaceDetails();
}
