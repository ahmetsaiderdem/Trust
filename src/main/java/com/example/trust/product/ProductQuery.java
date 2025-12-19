package com.example.trust.product;

import java.math.BigDecimal;

public class ProductQuery {

    public String q;
    public Integer categoryId;
    public BigDecimal minPrice;
    public BigDecimal maxPrice;
    public Boolean inStock;
    public String sort;
    public int page=0;
    public int size=12;
}
