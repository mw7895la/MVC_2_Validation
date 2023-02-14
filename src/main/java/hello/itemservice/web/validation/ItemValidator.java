package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
        //스프링이 제공하는 Validator를 쓴다.


    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        //Item.class.isAssignableFrom(clazz); 의 의미는?? - clazz가 Item 클래스(인터페이스)를 통해 구현한 것인지 확인하는것.  결과가 True면 이제 밑에 validate() 가 수행된다.
        //item == clazz 파라미터로 넘어오는 클래스가 item 타입이랑 같냐
        // item의 자식 클래스여도 통과한다.
    }

    @Override
    public void validate(Object target, Errors errors) {
        //Object target은 item
        Item item= (Item) target;
        //Errors는 BindingResult의 부모
        //Errors도 rejectValue() reject() 있음.


        /*ValidationUtils.rejectIfEmptyOrWhitespace(errors,"itemName","required");*/    //값이 null이거나 0이거나 공백문자인 경우 "itemName"필드에 rejectedValue값과 오류메시지 추가

        FieldError priceError = errors.getFieldError("price");

        if (!StringUtils.hasText(item.getItemName())) {

            errors.rejectValue("itemName","required");
        }

        if (priceError==null && (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)) {

            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {

            errors.rejectValue("quantity", "max",new Object[]{9999},null);
        }

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){

                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


    }
}
