package com.example.demo.src.address.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.dialect.Ingres9Dialect;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNearTownListRes {
    private List<Integer> range1;
    private List<Integer> range2;
    private List<Integer> range3;
    private List<Integer> range4;

}
