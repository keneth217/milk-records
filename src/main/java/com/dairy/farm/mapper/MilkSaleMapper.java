package com.dairy.farm.mapper;

import com.dairy.farm.dto.MilkDto;
import com.dairy.farm.entity.MilkSale;

public class MilkSaleMapper {



    // Static method to convert MilkSale to MilkDto
    public static MilkDto convertToMilkSaleDto(MilkSale milkSale) {
        MilkDto dto = new MilkDto();

        // Setting all the fields
        dto.setId(milkSale.getId());
        dto.setDate(milkSale.getDate());
        dto.setDayOfWeek(milkSale.getDayOfWeek());
        dto.setTotalLitres(milkSale.getTotalLitres());
        dto.setLitreCost(milkSale.getLitreCost());
        dto.setTotalAmount(milkSale.getTotalAmount());
        dto.setTotalPaidAmount(milkSale.getTotalPaidAmount());
        dto.setRemainingAmount(milkSale.getRemainingAmount());
        dto.setStatus(milkSale.getStatus());

        return dto; // Returning the mapped DTO object
    }
}
