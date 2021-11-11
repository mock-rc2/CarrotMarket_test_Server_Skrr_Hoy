package com.example.demo.src.chatting;

import com.example.demo.src.chatting.*;
import com.example.demo.src.post.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chatting.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/chatting")
public class ChattingController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChattingProvider chattingProvider;
    @Autowired
    private final ChattingService chattingService;
    @Autowired
    private final JwtService jwtService;




    public ChattingController(ChattingProvider chattingProvider, ChattingService chattingService, JwtService jwtService){
        this.chattingProvider = chattingProvider;
        this.chattingService = chattingService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetChattingRoom>> allChattingSelect(){
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try{
            int userId = jwtService.getUserId();
            List<GetChattingRoom> allChattingSelect = chattingProvider.allChattingSelect(userId);
            return new BaseResponse<>(allChattingSelect);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("/count")
    public BaseResponse<PostChattingCountRes> postChattingCount(@RequestParam("postId") int postId){
        try{
            PostChattingCountRes postChattingCount = chattingProvider.postChattingCount(postId);
            return new BaseResponse(postChattingCount);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("/last-chat")
    public BaseResponse<GetChattingContent> getChattingContent(@RequestParam("chattingRoomId") int chattingRoomId){
        try{
            GetChattingContent getChattingContent = chattingProvider.getChattingContent(chattingRoomId);
            return new BaseResponse(getChattingContent);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("{chattingRoomId}")
    public BaseResponse<List<GetChattingContent>> getAllChattingContent(@PathVariable("chattingRoomId") int chattingRoomId){
        try{
            List<GetChattingContent> getAllChattingContent = chattingProvider.getAllChattingContent(chattingRoomId);
            return new BaseResponse<>(getAllChattingContent);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @PostMapping("{sellerUserId}")
    public BaseResponse<PostChattingRes> postChattingRoom(@PathVariable("sellerUserId") int sellerUserId, @RequestBody PostChattingReq postChattingReq){
        try{

            PostChattingRes postChattingRes = chattingService.createChattingRoom(sellerUserId, postChattingReq);
            return new BaseResponse<>(postChattingRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("/{chattingRoomId}/status")
    public BaseResponse<String> modifyChattingRoomStatus(@PathVariable("chattingRoomId") int chattingRoomId, @RequestBody ChattingRoom chattingRoom){
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try {
            //jwt에서 idx 추출.
            int userId = jwtService.getUserId();

            if(chattingRoom.getStatus().equals("Invalid")){
                PatchChattingRoomStatus patchChattingRoomStatus = new PatchChattingRoomStatus(userId,chattingRoomId,chattingRoom.getStatus());
                chattingService.modifyChattingRoomStatus(patchChattingRoomStatus);

                String result = "";
                return new BaseResponse<>(result);
            }
            else{
                return new BaseResponse<>(PATCH_WISHLIST_INVALID_STATUS);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/{chattingRoomId}/content")
    public BaseResponse<PostChattingContentRes> postChattingContent(@PathVariable("chattingRoomId") int chattingRoomId, @RequestBody PostChattingContentReq postChattingContentReq){
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try{
            int userId = jwtService.getUserId();
            PostChattingContentRes postChattingContentRes = chattingService.createChattingContent(chattingRoomId, userId, postChattingContentReq);
            return new BaseResponse<>(postChattingContentRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }






}
