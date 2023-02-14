package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")                    //●●●순서가 중요하다 item에 바인딩된 결과가 bindingResult에 담기는것(오류도 담김).●●●
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //bindingResult 가 아까 V1에서의 errors 역할을 해줌. 그리고 스프링의 매커니즘임.
        //bindingResult의 결과는 자동으로  뷰로 넘어간다 그래서 우리가 ${#fields. ~ } 로 사용했던 것이다.
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            //objectName은 @ModelAttribute 이름, field 오류가 발생한 필드 이름 , defaultMessage 오류 기본 메시지
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000만 가능합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = "));
            }
        }

        //검증에 실패하면 위에서 담은 데이터를 가지고 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            //bindingResult는 자동으로 Model에 담겨서 View에 같이 넘어간다 그래서 아래 생략 가능
            //model.addAttribute("bindingResult",bindingResult);
            //다시 입력폼 뷰로 넘어갈 것임.
            return "validation/v2/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     *사용자 입력 오류메시지가 화면에 남도록 하자. EX) 입력한 가격이 1000원 미만이어도 남아있어야 한다.
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // 오류 메시지 처리하기.

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {

            // Crtl+P  2번째 생성자를 쓰면 3번째 파라미터로 rejectedValue가 올 수 있다. rejectedValue로 인해 우리가 계속 오류 데이터를 볼 수 있는것.

            bindingResult.addError(new FieldError("item","itemName",item.getItemName(), false,null,null,"상품 이름은 필수 입니다."));
        }

        // 컨트롤러로 넘어오기 전에 스프링이 FieldError의 rejectedValue에 qqq가 담기게 된다 이후 어쩔수 없이 item.getPrice()는 null이 되고 if문 안에는 getPrice()==null이다. 그리고 BindingResult가 갖고있던 rejecteValue의 값이 3번째 파라미터의 item.getPrice()로 들어감.
        // 이제 이 값도 같이 뷰로 넘어간다(addForm으로). 그래서 유지가 되는 것.
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),false,null,null,"가격은 1,000 ~ 1,000,000만 가능합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,null,null, "수량은 최대 9,999개 까지 허용합니다."));
        }
        //필드는 넘어온 값들이 있어야 하지만, 오브젝트 에러는 그런게 아니기 때문에 .. 바인딩 실패하거나 이런 일도 없음.
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                bindingResult.addError(new ObjectError("item", null,null,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = "));
            }
        }

        //검증에 실패하면 위에서 담은 데이터를 가지고 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            //bindingResult는 자동으로 Model에 담겨서 View에 같이 넘어간다 그래서 아래 생략 가능
            //model.addAttribute("bindingResult",bindingResult);
            //다시 입력폼 뷰로 넘어갈 것임.
            return "validation/v2/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    /**
     *  오류 메시지 처리 errors.properties
     */
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //오류된 데이터도 그대로 남아있게 하자.


        //bindingResult는 파라미터에서 검증할 객체 바로 다음에 온다 했다. 이말은 bindingResult객체가 뭘 검증해야 할 지 이미 알고 있다는 것.
        log.info("objecTName={}", bindingResult.getObjectName());       // item을 말함
        log.info("target={}", bindingResult.getTarget());           //Item클래스의 @Data때문에  toString()으로 보이는 것.

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {

            //codes 파라미터 부분이 배열로 된 이유는  첫번째 인자의 에러를 못찾으면 두번째 것을 errors.properties에서 찾는다
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(), false,new String[]{"required.item.itemName","required.default"},null,null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            //codes 는 String배열로 써줘야 한다.  arguments는 Obejct 배열로 값을 넘겨줘야 함.
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,100000},null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }
        //필드는 넘어온 값들이 있어야 하지만, 오브젝트 에러는 그런게 아니기 때문에 .. 바인딩 실패하거나 이런 일도 없음.
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
            }
        }

        //검증에 실패하면 위에서 담은 데이터를 가지고 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            //bindingResult는 자동으로 Model에 담겨서 View에 같이 넘어간다 그래서 아래 생략 가능
            //model.addAttribute("bindingResult",bindingResult);
            //다시 입력폼 뷰로 넘어갈 것임.
            return "validation/v2/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     *
     * rejectValue() , reject() 사용하여 검증 코드를 깔끔하게 하기.
     */
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {


        FieldError priceError = bindingResult.getFieldError("price");       //price에 아무것도 입력안하거나 범위 벗어나면  1000 ~ 100000이란 메시지 출력// qqq를 입력하면 타입오류 메시지 출력
        log.info("bindingResult.getFieldError()={}",bindingResult.getFieldError("price"));

        //bindingResult는 파라미터에서 검증할 객체 바로 다음에 온다 했다. 이말은 bindingResult객체가 뭘 검증해야 할 지 이미 알고 있다는 것.
        log.info("objecTName={}", bindingResult.getObjectName());       // item을 말함
        log.info("target={}", bindingResult.getTarget());           //Item클래스의 @Data때문에  toString()으로 보이는 것.


        //강의자료에 있는 스프링의 validationUtils 잠깐 소개 , 검증 로직 - itemName과 같은 역할이다.
        //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");


        //검증 로직 - itemName
        if (!StringUtils.hasText(item.getItemName())) {

            //ObjectName 안적나? bindingResult가 이미 target인 Item ObjectName을 알고있다. 또한, 위에서 처럼 rejectedValue를 스프링이 Error 객체를 생성하면서 사용자가 입력한 값을 자동으로 넣어준다.
            //errorCode는 new String[]{"required.item.itemName"}  에서  required만 적어준다. //errors.properties에서 required와 ObjectName, Field의 순서로 조합해서 준다.
            // errorCode파라미터 부분이 new String[]{"required.item.itemName","required"}; 처럼 되어있다고 생각하면 된다. 디테일한것이 우선순위가 높다!
            bindingResult.rejectValue("itemName","required");
        }

        if (priceError==null && (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)) {

            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {

            bindingResult.rejectValue("quantity", "max",new Object[]{9999},null);
        }
        //필드는 넘어온 값들이 있어야 하지만, 오브젝트 에러는 그런게 아니기 때문에 .. 바인딩 실패하거나 이런 일도 없음.
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                //bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 위에서 담은 데이터를 가지고 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            //bindingResult는 자동으로 Model에 담겨서 View에 같이 넘어간다 그래서 아래 생략 가능
            //model.addAttribute("bindingResult",bindingResult);
            //다시 입력폼 뷰로 넘어갈 것임.
            return "validation/v2/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }



    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

