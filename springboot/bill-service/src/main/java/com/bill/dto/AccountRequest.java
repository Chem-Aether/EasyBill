package com.bill.dto;

import java.math.BigDecimal;

public record AccountRequest(String name, String code, BigDecimal openingBalance) {}
