package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController     //안에 @ResponseBody 있음.
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    //@RequestBody API방식은 JSON이 완전히 객체로 변환이 되어야 그제서야 Validation이 가능. ( 필드 하나하나만 보는 @ModelAttribute와는 다르다)

    //실패 요청  - JSON을 객체로 만들지도 못함 -> 컨트롤러로 넘어오지도 못함. (일부러 데이터 타입 오류 냈을 떄)
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {
                        //json형식의 api로 받을거야
        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()) {
            //JSON API가 온거라는거 잊지마라
            log.info("검증오류 발생 errors={} ", bindingResult);
            return bindingResult.getAllErrors();        //fieldError와 objectError를 전부다 반환한다. 그리고 JSON형식으로 화면에 보여준다.
        }

        log.info("성공 로직 실행");
        return form;
    }


}
