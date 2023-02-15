package hello.itemservice.web.validation.form;

import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemSaveForm {     //등록시 넘어오는 데이터 관련.
    //private Long id;

    @NotBlank
    private String itemName;
    @NotNull
    @Range(min=1000, max=100000)
    private Integer price;

    @Max(value=9999)
    private Integer quantity;
}
