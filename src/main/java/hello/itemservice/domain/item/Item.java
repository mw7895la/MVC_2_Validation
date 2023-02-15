package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;       // javax. beanvalidation이 표준적으로 제공해서 모든 구현체에서도 동작한다.
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message="총 합이 10,000원 넘게 입력해주세요.") //이렇게 쓰는건 추천하지 않는다.
public class Item {

    //@NotNull(groups = UpdateCheck.class)
    private Long id;

    //@NotBlank(groups={SaveCheck.class, UpdateCheck.class})
    private String itemName;

    //@NotNull(groups={SaveCheck.class, UpdateCheck.class})
    //@Range(min=1000, max=100000, groups={SaveCheck.class, UpdateCheck.class})
    private Integer price;

    //NotNull(groups={SaveCheck.class, UpdateCheck.class})
    //@Max(value=9999 ,groups = SaveCheck.class )       //요구 사항에서 수정시에는 9999개 넘겨도 된다고 함. 그래서 저장시에만 9999개 지키자.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
