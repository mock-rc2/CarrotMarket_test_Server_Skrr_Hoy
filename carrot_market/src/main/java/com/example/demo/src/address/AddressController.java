package com.example.demo.src.address;

import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexPhone;

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
     * [GET] /address?search={search}&townId={townId}
     * @return BaseResponse<List<GetTownRes>>
     * 토큰 필요함 -> 토큰의 유저정보를 통해 승인된 주소 정보가 있는지 확인해야함
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetTownRes>> getTownSearchBySearch(@RequestParam("search") String search, @RequestParam("townId") int townId ) {

        try{
            List<GetTownRes> getTownRes = addressProvider.getTownBySearch(search, townId);
            return new BaseResponse<>(getTownRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 현재 위치를 통해 주변 동네 조회
     * [GET] /address/location?townId={townId}
     * @return BaseResponse<List<GetTownRes>>
     */
    @ResponseBody
    @GetMapping("/location")
    public BaseResponse<List<GetTownRes>> getTownSearchByLocation(@RequestParam("townId") int townId) {
        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }
        try{
            List<GetTownRes> getTownRes = addressProvider.getTownByLocation(townId);
            return new BaseResponse<>(getTownRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 현재 위치한 동네에 해당하는 동네id 조회
     * [GET] /address/townId?city={city}&district={district}&townName={townName}
     * @return BaseResponse<List<GetTownRes>>
     */
    @ResponseBody
    @GetMapping("/townId")
    public BaseResponse<Integer> getTownIdByCurrentLocation(@RequestParam("city") String city, @RequestParam("district") String district, @RequestParam("townName") String townName) throws BaseException {


        try{
            int townId = addressProvider.getTownId(city, district, townName);
            return new BaseResponse<>(townId);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 특정 동네의 range 별 근처 동네 리스트 반환
     * [GET] /address/near?townId={townId}
     * @return BaseResponse<List<GetTownRes>>
     */
    @ResponseBody
    @GetMapping("/near")
    public BaseResponse<GetNearTownListRes> getNearTownLiST(@RequestParam("townId") int townId) throws BaseException {

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        try{
            GetNearTownListRes getNearTownListRes  = addressProvider.getNearTownList(townId);
            return new BaseResponse<>(getNearTownListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 내 동네 추가
     * [POST] /address/:townId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{townId}")
    public BaseResponse<String> PostAddress(@PathVariable("townId") int townId){

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        //토큰 유효기간 파악
        try {
           Date current = new Date(System.currentTimeMillis());
           if(current.after(jwtService.getExp())){
               throw new BaseException(INVALID_JWT);
           }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {
            userIdByJwt = jwtService.getUserId();

            addressService.postAddress(userIdByJwt, townId);

            String result = "동네가 추가되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 내 동네 삭제
     * [PATCH] /address/:townId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{townId}")
    public BaseResponse<String> PatchAddress(@PathVariable("townId") int townId){

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {

            userIdByJwt = jwtService.getUserId();

            addressService.patchAddress(userIdByJwt, townId);

            String result = "동네가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 내 동네 바꾸기
     * [Patch] /address/change/:townId
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/change/{townId}")
    public BaseResponse<String> PostChangeAddress(@PathVariable("townId") int townId){

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {
            userIdByJwt = jwtService.getUserId();


            addressService.patchChangeAddress(userIdByJwt, townId);


            String result = "동네가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 유저가 설정한 townId, 인증여부, 범위 가져오기
     * [GET] /address
     * @return BaseResponse<GetAddressRes>
     */
    @ResponseBody
    @GetMapping("/info")
    public BaseResponse<GetAddressRes> getAddress() throws BaseException {

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {
            userIdByJwt = jwtService.getUserId();

            GetAddressRes getAddressRes = addressProvider.getAddress(userIdByJwt);

            return new BaseResponse<>(getAddressRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 입력받은 townId를 동네 이름으로 반환하기
     * [GET] /address/:townId
     * @return BaseResponse<GetTownRes>
     */
    @ResponseBody
    @GetMapping("/{townId}")
    public BaseResponse<GetTownNameRes> getTownName(@PathVariable("townId") int townId) throws BaseException {

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }
        try {

            GetTownNameRes getTownNameRes = addressProvider.getTownName(townId);
            return new BaseResponse<>(getTownNameRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 동네 설정 범위 변경
     * [Patch] /address/:townId/:range
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{townId}/{range}")
    public BaseResponse<String> PostChangeAddressRange(@PathVariable("townId") int townId, @PathVariable("range") int range){

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }
        if( range < 0 || range > 3){
            return new BaseResponse<>(PATCH_RANGE_RANGE_ERROR);
        }

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {
            userIdByJwt = jwtService.getUserId();

            addressService.patchAddressRange(userIdByJwt, townId, range);

            String result = "범위가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 설정된 동네들 조회
     * [GET] /address/user-address
     * @return BaseResponse<GetTownRes>
     */
    @ResponseBody
    @GetMapping("/user-address")
    public BaseResponse<List<GetTownNameRes>> getUserAddress() throws BaseException {

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
        int userIdByJwt;
        try {
            userIdByJwt = jwtService.getUserId();
            List<GetTownNameRes> getUserAddressRes = addressProvider.getUserAddress(userIdByJwt);
            return new BaseResponse<>(getUserAddressRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 동네 인증 추가하기
     * [Patch] /address/certification/:townId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/certification/{townId}")
    public BaseResponse<String> PatchCertificationAddress(@PathVariable("townId") int townId){

        if(townId < 1 || townId >6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;

        try {
            userIdByJwt = jwtService.getUserId();

            addressService.patchCertificationAddress(userIdByJwt, townId);

            String result = "동네가 인증되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}


