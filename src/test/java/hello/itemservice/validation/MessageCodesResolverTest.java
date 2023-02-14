package hello.itemservice.validation;

import net.bytebuddy.pool.TypePool;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageCodesResolverTest {

    //인터페이스다. 에러코드를 하나 넣으면 여러개의 값들을 반환해줌.       //ex) required를 넣으면 여러가지 반환
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");

        for(String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }

        //messageCode = required.item
        //messageCode = required            2가지 메시지 코드를 뱉어냄 여기서 나온 배열을 아래처럼 rejectValue(), reject() 가 해주는 것.
        //new ObjectError("item", new String[]{"required.item", "required"});

        Assertions.assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        //messageCode = required.item.itemName      위에가 총 4가지를 만들어줌.
        //messageCode = required.itemName
        //messageCode = required.java.lang.String
        //messageCode = required

        //BindingResult.rejectValue() 가 내부적으로 MessageCodesResolver를 쓴다.  그래서 String[] messageCodes를 얻는다.
        //BindingResult.rejectValue("itemName","required"); 의 동작 -> rejectValue() 내부에서 codesResolver를 호출하고 위의 총 4가지(messageCodes)를 얻어서 new FiledError("item","itemName", ~ , ~ , messagedCodes, null, null) 을 넘기는 것이다.

        //iter + tab
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }


        //주어진 값이 (messageCodes) 순서대로 정확하게 주어진 값에 포함되는지
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }

}
