package com.dairy.farm.dto;

import com.dairy.farm.entity.MilkSale;
import lombok.Data;

import java.util.List;
@Data
public class MilkSaleDto {

    private List<MilkDto> milkSalesList;
    private Integer totalPages;
    private Integer pageNumber;
}
