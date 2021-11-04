package com.example.demo.src.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AddressProvider addressProvider;
    @Autowired
    private final AddressService addressService;
    @Autowired
    private final JwtService jwtService;




    public AddressController(AddressProvider addressProvider, AddressService addressService, JwtService jwtService){
        this.addressProvider = addressProvider;
        this.addressService = addressService;
        this.jwtService = jwtService;
    }

    /**
     * 검색을 통해 동네 조회
     * [GET] /address?search={search}
     * @return BaseResponse<List<GetTownRes>>
     * 토큰 필요함 -> 토큰의 유저정보를 통해 승인된 주소 정보가 있는지 확인해야함
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetTownRes>> getTownSearchBySearch(@RequestParam("search") String search) {
        try{
            List<GetTownRes> getTownRes = addressProvider.getTownBySearch(search);
            return new BaseResponse<>(getTownRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 현재 위치를 통해 동네 조회
     * [GET] /address/location
     * @return BaseResponse<List<GetTownRes>>
     */
    @ResponseBody
    @GetMapping("/location")
    public BaseResponse<List<GetTownRes>> getTownSearchByLocation(@RequestBody GetTownReq getTownReq) {
        try{
            List<GetTownRes> getTownRes = addressProvider.getTownByLocation(getTownReq);
            return new BaseResponse<>(getTownRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

}
