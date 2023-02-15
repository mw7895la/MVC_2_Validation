package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {


    private final ItemRepository itemRepository;

   /* @Autowired
    public ValidationItemControllerV2(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

    }*/


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }


    /**
     *
     * @ Validated 만 있으면 어노테이션 기반으로 검증하는 검증기가 자동으로 검증을 하고 결과를 bindingResult에 넣어준다.
     * @ Valid 도 사용 가능하다  @Valid는 자바 표준이다 스프링이 아닌 다른 프레임워크에서도 동작가능  @Validated는 스프링 프레임워크 것.
     * @Valid 는 jakarta-validation에 있기 때문에 build.gradle에 아까 우리가 했던 라이브러리를 추가해줘야한다.
     */
    //@PostMapping("/add")
    public String addItemV1(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //itemName 빈공백으로 하면 오류코드가 필드위에 정해준 어노테이션 이름으로 NotBlank.item.itemName,NotBlank.itemName,NotBlank.java.lang.String,NotBlank 찍힘.

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                //bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            return "validation/v3/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }


    /**
     *  Item 필드에 groups 설정, @Validated(value = SaveCheck.class)     //SaveCheck.class  SaveCheck로 된 인터페이스나 클래스 타입을 말하는것.
     */
    @PostMapping("/add")
    public String addItemV2(@Validated(value = SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /**
         * @Validated 에는 value()가 있다 Specify one or more validation groups to apply to the validation step kicked off by this annotation.
         * 이렇게 하면 @Validated가 먹힐때 SaveCheck가 있는 필드의 조건만 먹히는 것이다
         */

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                //bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()){
            log.info("bindingResult= {} ", bindingResult);

            return "validation/v3/addForm";
        }


        //아래는 성공 로직.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    //@PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId,@Validated @ModelAttribute Item item, BindingResult bindingResult) {

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                //bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }


    /**
     * 여기는 UpdateCheck.class @Validated 에 추가
     */
    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId,@Validated(value = UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //얘는 특정 필드에 대한 오류가 아님. 오브젝트 자체에서 오류가 난 것. 글로벌 오류.  @ModelAttribute에 담긴 item
                //bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

