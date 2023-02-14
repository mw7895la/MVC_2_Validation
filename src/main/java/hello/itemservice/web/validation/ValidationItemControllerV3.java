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
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //itemName 빈공백으로 하면 오류코드가 필드위에 정해준 어노테이션 이름으로 NotBlank.item.itemName,NotBlank.itemName,NotBlank.java.lang.String,NotBlank 찍힘.

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

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

